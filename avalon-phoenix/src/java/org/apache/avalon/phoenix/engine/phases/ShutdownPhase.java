/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.phases;

import org.apache.avalon.excalibur.thread.ThreadContext;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.atlantis.ApplicationException;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.BlockVisitor;
import org.apache.avalon.phoenix.engine.facilities.ClassLoaderManager;
import org.apache.avalon.phoenix.engine.facilities.ThreadManager;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ShutdownPhase
    extends AbstractLoggable
    implements BlockVisitor, Composable
{
    private ClassLoader                 m_classLoader;
    private ThreadManager               m_threadManager;
    private Container                   m_container;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        final ClassLoaderManager classLoaderManager =
            (ClassLoaderManager)componentManager.lookup( ClassLoaderManager.ROLE );

        m_classLoader = classLoaderManager.getClassLoader();

        m_threadManager = (ThreadManager)componentManager.lookup( ThreadManager.ROLE );
        m_container = (Container)componentManager.lookup( Container.ROLE );
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception ApplicationException if walking is to be stopped
     */
    public void visitBlock( final String name, final BlockEntry entry )
        throws ApplicationException
    {
        if( entry.getState() != Phases.STARTEDUP ) return;

        getLogger().info( "Processing Block: " + name );
        getLogger().debug( "Processing with classloader " + m_classLoader );

        //HACK: Hack-o-mania here - Fix when each Application is
        //run in a separate thread group
        Thread.currentThread().setContextClassLoader( m_classLoader );
        ThreadContext.setCurrentThreadPool( m_threadManager.getDefaultThreadPool() );

        final Object object = entry.getInstance();

        //Stoppable stage
        if( object instanceof Startable )
        {
            getLogger().debug( "Pre-Stoppable Stage" );

            try
            {
                ((Startable)object).stop();
                getLogger().debug( "Stoppable successful." );
            }
            catch( final Exception e )
            {
                getLogger().warn( "Unable to stop block " + name, e );
            }
        }

        //Disposable stage
        if( object instanceof Disposable )
        {
            getLogger().debug( "Pre-Disposable Stage" );

            try
            {
                ((Disposable)object).dispose();
                getLogger().debug( "Disposable successful." );
            }
            catch( final Throwable t )
            {
                getLogger().warn( "Unable to dispose block " + name, t );
            }
        }

        //Destruction stage
        getLogger().debug( "Pre-Destruction Stage" );
        entry.setInstance( null );
        entry.setState( Phases.SHUTDOWN );
        getLogger().debug( "Destruction successful." );

        try
        {
            m_container.remove( name );
            getLogger().debug( "Removed entry from container." );
        }
        catch( final Throwable t )
        {
            getLogger().warn( "Unable to remove entry from container " + name, t );
        }

        getLogger().info( "Ran Shutdown Phase for " + name );
    }
}
