/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import org.apache.avalon.excalibur.thread.ThreadContext;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ShutdownPhase
    extends AbstractLoggable
    implements BlockVisitor, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ShutdownPhase.class );

    private ApplicationFrame  m_frame;
    private Container         m_container;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_frame = (ApplicationFrame)componentManager.lookup( ApplicationFrame.ROLE );
        m_container = (Container)componentManager.lookup( Container.ROLE );
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception Exception if walking is to be stopped
     */
    public void visitBlock( final String name, final BlockEntry entry )
        throws Exception
    {
        if( State.STARTED != entry.getState() ) return;

        if( getLogger().isInfoEnabled() )
        {
            final String message = REZ.getString( "shutdown.notice.processing.name", name );
            getLogger().info( message );
        }
        
        //TODO: remove this and place in deployer/application
        if( getLogger().isDebugEnabled() )
        {
            final String message = 
                REZ.getString( "shutdown.notice.processing.classloader", m_frame.getClassLoader() );
            getLogger().debug( message );
        }

        //HACK: Hack-o-mania here - Fix when each Application is
        //run in a separate thread group
        Thread.currentThread().setContextClassLoader( m_frame.getClassLoader() );
        ThreadContext.setCurrentThreadPool( m_frame.getDefaultThreadPool() );

        final Object object = entry.getInstance();

        //Stoppable stage
        if( object instanceof Startable )
        {
            getLogger().debug( REZ.getString( "shutdown.notice.stop.pre" ) );

            try
            {
                ((Startable)object).stop();
                getLogger().debug( REZ.getString( "shutdown.notice.stop.success" ) );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "shutdown.error.stop.fail", name );
                getLogger().warn( message, e );
            }
        }

        //Disposable stage
        if( object instanceof Disposable )
        {
            getLogger().debug( REZ.getString( "shutdown.notice.dispose.pre" ) );

            try
            {
                ((Disposable)object).dispose();
                getLogger().debug( REZ.getString( "shutdown.notice.dispose.success" ) );
            }
            catch( final Throwable t )
            {
                final String message = REZ.getString( "shutdown.error.dispose.fail", name );
                getLogger().warn( message, t );
            }
        }

        //Destruction stage
        getLogger().debug( REZ.getString( "shutdown.notice.destroy.pre" ) );
        entry.setInstance( null );
        entry.setState( State.DESTROYED );
        getLogger().debug( REZ.getString( "shutdown.notice.destroy.success" ) );

        final String message = REZ.getString( "shutdown.error.phase.completed", name );
        getLogger().info( message );
    }
}
