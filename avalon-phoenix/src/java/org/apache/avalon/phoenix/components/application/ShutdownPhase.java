/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.listeners.BlockListenerManager;

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

    ///Listener for when blocks are created
    private BlockListener        m_listener;

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_frame = (ApplicationFrame)componentManager.lookup( ApplicationFrame.ROLE );
        m_listener = (BlockListenerManager)componentManager.lookup( BlockListenerManager.ROLE );
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

        ThreadContext.setThreadContext( m_frame.getThreadContext() );


        final BlockEvent event =
            new BlockEvent( name, entry.getProxy(), entry.getMetaData().getBlockInfo() );
        m_listener.blockRemoved( event );

        final Block block = entry.getBlock();

        entry.invalidate();

        //Stoppable stage
        if( block instanceof Startable )
        {
            getLogger().debug( REZ.getString( "shutdown.notice.stop.pre" ) );

            try
            {
                entry.setState( State.STOPPING );
                ((Startable)block).stop();
                entry.setState( State.STOPPED );

                getLogger().debug( REZ.getString( "shutdown.notice.stop.success" ) );
            }
            catch( final Exception e )
            {
                entry.setState( State.FAILED );
                final String message = REZ.getString( "shutdown.error.stop.fail", name );
                getLogger().warn( message, e );
            }
        }

        //Disposable stage
        if( block instanceof Disposable )
        {
            getLogger().debug( REZ.getString( "shutdown.notice.dispose.pre" ) );

            try
            {
                entry.setState( State.DESTROYING );
                ((Disposable)block).dispose();

                getLogger().debug( REZ.getString( "shutdown.notice.dispose.success" ) );
            }
            catch( final Throwable t )
            {
                final String message = REZ.getString( "shutdown.error.dispose.fail", name );
                getLogger().warn( message, t );
            }
        }

        entry.setState( State.DESTROYED );
        final String message = REZ.getString( "shutdown.error.phase.completed", name );
        getLogger().info( message );
    }
}
