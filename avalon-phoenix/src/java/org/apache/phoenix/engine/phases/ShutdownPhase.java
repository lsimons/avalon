/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Disposable;
import org.apache.avalon.Stoppable;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.util.thread.ThreadContext;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.phoenix.engine.blocks.BlockEntry;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ShutdownPhase
    extends AbstractLoggable
    implements Phase, Composer
{
    private ClassLoader                 m_classLoader;
    private ThreadManager               m_threadManager;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_classLoader = (ClassLoader)componentManager.lookup( "java.lang.ClassLoader" );

        m_threadManager = (ThreadManager)componentManager.
            lookup( "org.apache.avalon.util.thread.ThreadManager" );
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
        if( entry.getState() != Phase.STARTEDUP ) return;

        getLogger().info( "Processing Block: " + name );
        getLogger().debug( "Processing with classloader " + m_classLoader );

        //HACK: Hack-o-mania here - Fix when each Application is
        //run in a separate thread group
        Thread.currentThread().setContextClassLoader( m_classLoader );
        ThreadContext.setCurrentThreadPool( m_threadManager.getDefaultThreadPool() );

        final Object object = entry.getInstance();

        //Stoppable stage
        if( object instanceof Stoppable )
        {
            getLogger().debug( "Pre-Stoppable Stage" );

            try
            {
                ((Stoppable)object).stop();
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
            catch( final Exception e )
            {
                getLogger().warn( "Unable to dispose block " + name, e );
            }
        }

        //Destruction stage
        getLogger().debug( "Pre-Destruction Stage" );
        entry.setInstance( null );
        entry.setState( Phase.SHUTDOWN );
        getLogger().debug( "Destruction successful." );

        getLogger().info( "Ran Shutdown Phase for " + name );
    }
}
