/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ApplicationException;
import org.apache.avalon.phoenix.interfaces.ApplicationMBean;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:leosimons@apache.org">Leo Simons</a>
 */
public final class DefaultApplication
    extends AbstractLogEnabled
    implements Application, ApplicationMBean, Initializable, Startable, Disposable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplication.class );

    private static final String PHASE_STARTUP = "startup";
    private static final String PHASE_SHUTDOWN = "shutdown";

    private boolean m_running = false;

    private ApplicationContext m_context;
    private LifecycleHelper m_lifecycle;
    private HashMap m_entrys = new HashMap();
    private SarMetaData m_sarMetaData;

    public DefaultApplication( SarMetaData sarMetaData )
    {
        m_sarMetaData = sarMetaData;
    }

    ///////////////////////
    // LifeCycle Methods //
    ///////////////////////

    public void initialize()
        throws Exception
    {
    }

    /**
     * Start the application running.
     * This is only valid when isRunning() returns false,
     * otherwise it will generate an IllegalStateException.
     *
     * @throws IllegalStateException if application is already running
     * @throws ApplicationException if the application failed to start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup
     */
    public void start()
        throws IllegalStateException, ApplicationException
    {
        if( isRunning() )
        {
            throw new IllegalStateException();
        }
        else
        {
            try
            {
                final BlockMetaData[] blocks = m_context.getMetaData().getBlocks();
                for( int i = 0; i < blocks.length; i++ )
                {
                    final String blockName = blocks[ i ].getName();
                    final BlockEntry blockEntry = new BlockEntry( blocks[ i ] );
                    m_entrys.put( blockName, blockEntry );
                }

                // load block listeners
                loadBlockListeners();

                // load blocks
                runPhase( PHASE_STARTUP );
            }
            catch( final Throwable t )
            {
                getLogger().info( "exception while starting:" + t.getMessage() + "\n" );
                t.printStackTrace();
                throw new ApplicationException( t.getMessage(), t );
            }

            m_running = true;
        }
    }

    /**
     * Shutdown and restart the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     * This is equivelent to  calling stop() and then start()
     * in succession.
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to stop or start.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to startup/shutdown
     */
    public void restart()
        throws IllegalStateException, ApplicationException
    {
        stop();
        start();
    }

    /**
     * Stop the application running.
     * This is only valid when isRunning() returns true,
     * otherwise it will generate an IllegalStateException.
     *
     * @throws IllegalStateException if application is not already running
     * @throws ApplicationException if the application failed to shutdown.
     *            the message part of exception will contain more information
     *            pertaining to why the application failed to shutodwn
     */
    public void stop()
        throws IllegalStateException, ApplicationException
    {
        if( !isRunning() )
        {
            throw new IllegalStateException();
        }
        else
        {
            try
            {
                runPhase( PHASE_SHUTDOWN );
            }
            catch( final Throwable t )
            {
                getLogger().info( "exception while stopping:" + t.getMessage() + "\n" );
                t.printStackTrace();
                throw new ApplicationException( t.getMessage(), t );
            }

            m_running = false;
        }
    }

    public void dispose()
    {
        m_entrys.clear();
    }

    ////////////////////////////
    // Public Utility Methods //
    ////////////////////////////
    public void setApplicationContext( final ApplicationContext context )
    {
        m_context = context;
        m_lifecycle = new LifecycleHelper( this, m_context );
        setupLogger( m_lifecycle, "lifecycle" );
    }

    public String[] getBlockNames()
    {
        return (String[])m_entrys.keySet().toArray( new String[ 0 ] );
    }

    public Block getBlock( final String name )
    {
        final BlockEntry entry = (BlockEntry)m_entrys.get( name );
        if( null == entry ) return null;
        return entry.getProxy();
    }

    /**
     * Get the name of the application.
     *
     * @return the name of the application
     */
    public String getName()
    {
        return m_sarMetaData.getName();
    }

    /**
     * Get the name to display in Management UI.
     *
     * @return the name of the application to display in UI
     */
    public String getDisplayName()
    {
        return m_sarMetaData.getName();
    }

    /**
     * Get the string used to describe the application in the UI.
     *
     * @return a short description of the application
     */
    public String getDescription()
    {
        return "The " + m_sarMetaData.getName() + " application.";
    }

    /**
     * Get location of Application installation
     *
     * @return the home directory of application
     */
    public String getHomeDirectory()
    {
        return m_sarMetaData.getHomeDirectory().getPath();
    }

    /**
     * Return true if the application is
     * running or false otherwise.
     *
     * @return true if application is running, false otherwise
     */
    public boolean isRunning()
    {
        return m_running;
    }

    /////////////////////////////
    // Private Utility Methods //
    /////////////////////////////

    private void loadBlockListeners()
        throws Exception
    {
        //Setup thread context for calling visitors
        ThreadContext.setThreadContext( m_context.getThreadContext() );

        try
        {
            doLoadBlockListeners();
        }
        finally
        {
            ThreadContext.setThreadContext( null );
        }
    }

    /**
     * Actually perform loading of each individual Listener.
     * Note that by this stage it is assumed that the ThreadContext
     * has already been setup correctly.
     */
    private void doLoadBlockListeners()
        throws Exception
    {
        final BlockListenerMetaData[] listeners = m_context.getMetaData().getListeners();
        for( int i = 0; i < listeners.length; i++ )
        {
            try
            {
                m_lifecycle.startupListener( listeners[ i ] );
            }
            catch( final Exception e )
            {
                final String name = listeners[ i ].getName();
                final String message =
                    REZ.getString( "bad-listener", "startup", name, e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }

    /**
     * Run a phase for application.
     * Each phase transitions application into new state and processes
     * all the blocks to make sure they are in that state aswell.
     * Exceptions leave the blocks in an indeterminate state.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void runPhase( final String name )
        throws Exception
    {
        //Setup thread context for calling visitors
        ThreadContext.setThreadContext( m_context.getThreadContext() );

        try
        {
            doRunPhase( name );
        }
        finally
        {
            ThreadContext.setThreadContext( null );
        }
    }

    /**
     * Actually run applications phas.
     * By this methods calling it is assumed that ThreadContext
     * has already been setup.
     *
     * @param name the name of phase (for logging purposes)
     * @throws Exception if an error occurs
     */
    private final void doRunPhase( final String name )
        throws Exception
    {
        final BlockMetaData[] blocks = m_context.getMetaData().getBlocks();
        final String[] order = DependencyGraph.walkGraph( PHASE_STARTUP == name, blocks );

        //Log message describing the number of blocks
        //the phase in and the order in which they will be
        //processed
        if( getLogger().isInfoEnabled() )
        {
            final Integer count = new Integer( blocks.length );
            final List pathList = Arrays.asList( order );
            final String message =
                REZ.getString( "blocks-processing", count, name, pathList );
            getLogger().info( message );
        }

        //All blocks about to be processed ...
        if( PHASE_STARTUP == name )
        {
            //... for startup, so indicate to applicable listeners
            final ApplicationEvent event =
                new ApplicationEvent( m_sarMetaData.getName(), m_sarMetaData );
            m_lifecycle.applicationStarting( event );
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_lifecycle.applicationStopping();
        }

        //Process blocks, one by one.

        for( int i = 0; i < order.length; i++ )
        {
            final String block = order[ i ];

            //Log message saying we are processing block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString( "process-block", block, name );
                getLogger().debug( message );
            }

            try
            {
                final BlockEntry entry = (BlockEntry)m_entrys.get( block );
                if( PHASE_STARTUP == name )
                {
                    m_lifecycle.startup( entry );
                }
                else
                {
                    m_lifecycle.shutdown( entry );
                }
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.getString( "app.error.run-phase", name, block, e.getMessage() );
                getLogger().error( message, e );
                m_lifecycle.applicationFailure( e );
                throw e;
            }

            //Log message saying we have processed block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString( "processed-block", block, name );
                getLogger().debug( message );
            }
        }

        //All blocks processed ...
        if( PHASE_STARTUP == name )
        {
            //... for startup, so indicate to applicable listeners
            m_lifecycle.applicationStarted();
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_lifecycle.applicationStopped();
        }
    }
}
