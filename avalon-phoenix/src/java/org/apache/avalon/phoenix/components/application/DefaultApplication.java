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
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ApplicationException;
import org.apache.avalon.phoenix.interfaces.ApplicationMBean;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.excalibur.containerkit.lifecycle.LifecycleException;
import org.apache.excalibur.containerkit.lifecycle.LifecycleHelper;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @phoenix:mx-topic name="Application"
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
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

    private HashMap m_entries = new HashMap();

    /**
     * ResourceProvider for blocks.
     */
    private BlockResourceProvider m_blockAccessor;

    /**
     * ResourceProvider for listeners.
     */
    private ListenerResourceProvider m_listenerAccessor;

    /**
     * Object to support notification of ApplicationListeners.
     */
    private ListenerSupport m_listenerSupport = new ListenerSupport();

    /**
     * Object to support running objects through lifecycle phases.
     */
    private final LifecycleHelper m_lifecycleHelper = new LifecycleHelper();

    /**
     * Object to help exporting object.
     */
    private final ExportHelper m_exportHelper = new ExportHelper();

    ///////////////////////
    // LifeCycle Methods //
    ///////////////////////
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_lifecycleHelper );
    }

    public void initialize()
        throws Exception
    {
        try
        {
            // load block listeners
            loadBlockListeners();
        }
        catch( final Throwable t )
        {
            getLogger().info( "exception while loading listeners:" + t.getMessage() + "\n" );
            t.printStackTrace();
            throw new ApplicationException( t.getMessage(), t );
        }
    }

    /**
     * Start the application running.
     * This is only valid when isRunning() returns false,
     * otherwise it will generate an IllegalStateException.
     *
     * @phoenix:mx-operation
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
                    m_entries.put( blockName, blockEntry );
                }

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
     * @phoenix:mx-operation
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
     * @phoenix:mx-operation
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
        m_entries.clear();
    }

    ////////////////////////////
    // Public Utility Methods //
    ////////////////////////////
    public void setApplicationContext( final ApplicationContext context )
    {
        m_context = context;
        m_blockAccessor = new BlockResourceProvider( context, this );
        setupLogger( m_blockAccessor, "lifecycle" );
        m_listenerAccessor = new ListenerResourceProvider( context );
        setupLogger( m_listenerAccessor, "lifecycle" );
    }

    public String[] getBlockNames()
    {
        return (String[])m_entries.keySet().toArray( new String[ 0 ] );
    }

    /**
     *
     *
     * @param name
     * @return
     */
    public Object getBlock( final String name )
    {
        final BlockEntry entry = (BlockEntry)m_entries.get( name );
        if( null == entry )
        {
            return null;
        }
        else
        {
            return entry.getProxy();
        }
    }

    /**
     * Get the name of the application.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application
     */
    public String getName()
    {
        return getMetaData().getName();
    }

    /**
     * Get the name to display in Management UI.
     *
     * @phoenix:mx-attribute
     *
     * @return the name of the application to display in UI
     */
    public String getDisplayName()
    {
        return getMetaData().getName();
    }

    /**
     * Get the string used to describe the application in the UI.
     *
     * @phoenix:mx-attribute
     *
     * @return a short description of the application
     */
    public String getDescription()
    {
        return "The " + getDisplayName() + " application.";
    }

    /**
     * Get location of Application installation
     *
     * @phoenix:mx-attribute
     *
     * @return the home directory of application
     */
    public String getHomeDirectory()
    {
        return getMetaData().getHomeDirectory().getPath();
    }

    /**
     * Return true if the application is
     * running or false otherwise.
     *
     * @phoenix:mx-attribute
     *
     * @return true if application is running, false otherwise
     */
    public boolean isRunning()
    {
        return m_running;
    }

    protected final SarMetaData getMetaData()
    {
        return m_context.getMetaData();
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
                startupListener( listeners[ i ] );
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
            m_listenerSupport.fireApplicationStartingEvent( getMetaData() );
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_listenerSupport.applicationStopping();
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
                final BlockEntry entry = (BlockEntry)m_entries.get( block );
                if( PHASE_STARTUP == name )
                {
                    startup( entry );
                }
                else
                {
                    shutdown( entry );
                }
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.getString( "app.error.run-phase", name, block, e.getMessage() );
                getLogger().error( message, e );
                m_listenerSupport.applicationFailure( e );
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
            m_listenerSupport.applicationStarted();
        }
        else
        {
            //... for shutdown, so indicate to applicable listeners
            m_listenerSupport.applicationStopped();
        }
    }

    /**
     * Method to run a <code>Block</code> through it's startup phase.
     * This will involve notification of <code>BlockListener</code>
     * objects, creation of the Block/Block Proxy object, calling the startup
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropriately and
     * cause exceptions with useful messages to be raised.
     *
     * @param entry the entry containing Block
     * @throws Exception if an error occurs when block passes
     *            through a specific lifecycle stage
     */
    public void startup( final BlockEntry entry )
        throws Exception
    {
        final Object block =
            m_lifecycleHelper.startup( entry.getName(),
                                       entry,
                                       m_blockAccessor );

        m_exportHelper.exportBlock( m_context,
                                    entry.getMetaData(),
                                    block );

        entry.setObject( block );

        m_listenerSupport.fireBlockAddedEvent( entry );
    }

    /**
     * Method to run a <code>Block</code> through it's shutdown phase.
     * This will involve notification of <code>BlockListener</code>
     * objects, invalidating the proxy object, calling the shutdown
     * Avalon Lifecycle methods and updating State property of BlockEntry.
     * Errors that occur during shutdown will be logged appropraitely.
     *
     * @param entry the entry containing Block
     */
    public void shutdown( final BlockEntry entry )
        throws LifecycleException
    {
        m_listenerSupport.fireBlockRemovedEvent( entry );

        final Object object = entry.getObject();
        try
        {
            //Remove block from Management system
            m_exportHelper.unexportBlock( m_context,
                                          entry.getMetaData(),
                                          object );
            entry.invalidate();

            m_lifecycleHelper.shutdown( entry.getName(),
                                        object );
        }
        finally
        {
            entry.setObject( null );
        }
    }

    /**
     * Method to run a <code>BlockListener</code> through it's startup phase.
     * This will involve creation of BlockListener object and configuration of
     * object if appropriate.
     *
     * @param metaData the BlockListenerMetaData
     * @throws Exception if an error occurs when listener passes
     *            through a specific lifecycle stage
     */
    public void startupListener( final BlockListenerMetaData metaData )
        throws Exception
    {
        final String name = metaData.getName();
        final Object listener =
            m_lifecycleHelper.startup( name,
                                       metaData,
                                       m_listenerAccessor );

        // However onky ApplicationListners can avail of block events.
        if( listener instanceof ApplicationListener )
        {
            m_listenerSupport.addApplicationListener( (ApplicationListener)listener );
        }
        else
        {
            // As ApplicationListners are BlockListeners then
            //this is applicable for all
            m_listenerSupport.addBlockListener( (BlockListener)listener );

            final String message =
                REZ.getString( "helper.isa-blocklistener.error",
                               name,
                               metaData.getClassname() );
            getLogger().error( message );
            System.err.println( message );
        }
    }
}
