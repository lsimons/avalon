/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.LifecycleUtil;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * This is a class to help an Application manage lifecycle of
 * <code>Blocks</code> and <code>BlockListeners</code>. The
 * class will run each individual Entry through each lifecycle stage,
 * and manage erros in a consistent way.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
class LifecycleHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( LifecycleHelper.class );

    //Constants to designate stages
    private static final int STAGE_CREATE = 0;
    private static final int STAGE_LOGGER = 1;
    private static final int STAGE_CONTEXT = 2;
    private static final int STAGE_COMPOSE = 3;
    private static final int STAGE_CONFIG = 4;
    private static final int STAGE_PARAMETER = 5;
    private static final int STAGE_INIT = 6;
    private static final int STAGE_START = 7;
    private static final int STAGE_STOP = 8;
    private static final int STAGE_DISPOSE = 9;
    private static final int STAGE_DESTROY = 10;

    //Constants to designate type of component
    private static final int TYPE_BLOCK = 0;
    private static final int TYPE_LISTENER = 1;

    ///Frame in which block executes
    private ApplicationContext m_context;

    /**
     * The Application which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private Application m_application;

    /**
     * Object to support notification of BlockListeners.
     */
    private BlockListenerSupport m_blockListenerSupport = new BlockListenerSupport();

    /**
     * Object to support notification of ApplicationListeners.
     */
    private ApplicationListenerSupport m_applicationListenerSupport =
        new ApplicationListenerSupport();

    /**
     * Construct helper object for specified application,
     * in specified frame.
     *
     * @param application the Application that this object is helper to
     * @param context the ApplicationContext in which this helper operates
     */
    protected LifecycleHelper( final Application application,
                               final ApplicationContext context )
    {
        m_application = application;
        m_context = context;
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

        final ClassLoader classLoader = m_context.getClassLoader();
        final Class clazz = classLoader.loadClass( metaData.getClassname() );
        final BlockListener listener = (BlockListener)clazz.newInstance();

        if( listener instanceof LogEnabled )
        {
            final Logger logger = new LogKitLogger( m_context.getLogger( name ) );
            LifecycleUtil.logEnable( listener, logger );
        }

        if( listener instanceof Configurable )
        {
            final Configuration configuration = getConfiguration( name, TYPE_LISTENER );
            LifecycleUtil.configure( listener, configuration );
        }

        // As ApplicationListners are BlockListeners then this is applicable for all
        m_blockListenerSupport.addBlockListener( listener );

        // However onky ApplicationListners can avail of block events.
        if( listener instanceof ApplicationListener )
        {
            m_applicationListenerSupport.addApplicationListener( (ApplicationListener)listener );
        }

    }

    public void applicationStarting( ApplicationEvent appEvent ) throws Exception
    {
        m_applicationListenerSupport.applicationStarting( appEvent );
    }

    public void applicationStarted()
    {
        m_applicationListenerSupport.applicationStarted();
    }

    public void applicationStopping()
    {
        m_applicationListenerSupport.applicationStopping();
    }

    public void applicationStopped()
    {
        m_applicationListenerSupport.applicationStopped();
    }

    public void applicationFailure( Exception causeOfFailure )
    {
        m_applicationListenerSupport.applicationFailure( causeOfFailure );
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
        final BlockMetaData metaData = entry.getMetaData();
        final String name = metaData.getName();

        //The number of stage currently at
        //(Used in constructing error messages)
        int stage = 0;

        try
        {
            //Creation stage
            stage = STAGE_CREATE;
            notice( name, stage );
            final Block block = createBlock( metaData );

            //LogEnabled stage
            stage = STAGE_LOGGER;
            setupLogging( name, block, stage );

            //Contextualize stage
            stage = STAGE_CONTEXT;
            if( block instanceof Contextualizable )
            {
                notice( name, stage );
                final BlockContext context = createBlockContext( name );
                LifecycleUtil.contextualize( block, context );
            }

            //Composition stage
            stage = STAGE_COMPOSE;
            if( block instanceof Composable )
            {
                notice( name, stage );
                final ComponentManager componentManager = createComponentManager( metaData );
                LifecycleUtil.compose( block, componentManager );
            }
            else if( block instanceof Serviceable )
            {
                notice( name, stage );
                final ServiceManager manager = createServiceManager( metaData );
                LifecycleUtil.service( block, manager );
            }

            //Configuring stage
            stage = STAGE_CONFIG;
            if( block instanceof Configurable )
            {
                notice( name, stage );
                final Configuration configuration = getConfiguration( name, TYPE_BLOCK );
                LifecycleUtil.configure( block, configuration );
            }

            //Parameterizing stage
            stage = STAGE_PARAMETER;
            if( block instanceof Parameterizable )
            {
                notice( name, stage );
                final Parameters parameters =
                    Parameters.fromConfiguration( getConfiguration( name, TYPE_BLOCK ) );
                parameters.makeReadOnly();
                LifecycleUtil.parameterize( block, parameters );
            }

            //Initialize stage
            stage = STAGE_INIT;
            if( block instanceof Initializable )
            {
                notice( name, stage );
                LifecycleUtil.initialize( block );
            }

            //Start stage
            stage = STAGE_START;
            if( block instanceof Startable )
            {
                notice( name, stage );
                LifecycleUtil.start( block );
            }

            entry.setState( State.STARTED );
            entry.setBlock( block );

            exportBlock( metaData, block );

            final Block proxy = entry.getProxy();
            final BlockEvent event =
                new BlockEvent( name, proxy, metaData.getBlockInfo() );
            m_blockListenerSupport.blockAdded( event );
        }
        catch( final Throwable t )
        {
            entry.setState( State.FAILED );
            fail( name, stage, t );
        }
    }

    private void setupLogging( final String name, final Block block, int stage )
        throws Exception
    {
        if( block instanceof Loggable )
        {
            notice( name, stage );
            LifecycleUtil.setupLoggable( block, m_context.getLogger( name ) );
        }
        else if( block instanceof LogEnabled )
        {
            notice( name, stage );
            final Logger logger = new LogKitLogger( m_context.getLogger( name ) );
            LifecycleUtil.logEnable( block, logger );
        }
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
    {
        final BlockMetaData metaData = entry.getMetaData();
        final String name = metaData.getName();

        final BlockEvent event =
            new BlockEvent( name, entry.getProxy(), metaData.getBlockInfo() );
        m_blockListenerSupport.blockRemoved( event );

        final Block block = entry.getBlock();

        //Remove block from Management system
        unexportBlock( metaData, block );

        //Invalidate entry. This will invalidate
        //and null out Proxy object aswell as nulling out
        //block property
        entry.invalidate();

        //Stoppable stage
        if( block instanceof Startable )
        {
            notice( name, STAGE_STOP );
            try
            {
                entry.setState( State.STOPPING );
                LifecycleUtil.stop( block );
                entry.setState( State.STOPPED );
            }
            catch( final Throwable t )
            {
                entry.setState( State.FAILED );
                safeFail( name, STAGE_STOP, t );
            }
        }

        //Disposable stage
        if( block instanceof Disposable )
        {
            notice( name, STAGE_DISPOSE );
            try
            {
                entry.setState( State.DESTROYING );
                LifecycleUtil.dispose( block );
            }
            catch( final Throwable t )
            {
                entry.setState( State.FAILED );
                safeFail( name, STAGE_DISPOSE, t );
            }
        }

        notice( name, STAGE_DESTROY );
        entry.setState( State.DESTROYED );
    }

    /**
     * Export the services of block, declared to be management
     * services, into management system.
     */
    private void exportBlock( final BlockMetaData metaData,
                              final Block block )
        throws CascadingException
    {
        final ServiceDescriptor[] services = metaData.getBlockInfo().getManagementAccessPoints();
        final String name = metaData.getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                final Class clazz = classLoader.loadClass( service.getName() );
                m_context.exportObject( name, clazz, block );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "export.error", name, service.getName(), reason );
                getLogger().error( message );
                throw new CascadingException( message, e );
            }
        }
    }

    /**
     * Unxport the services of block, declared to be management
     * services, into management system.
     */
    private void unexportBlock( final BlockMetaData metaData,
                                final Block block )
    {
        final ServiceDescriptor[] services = metaData.getBlockInfo().getManagementAccessPoints();
        final String name = metaData.getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                final Class clazz = classLoader.loadClass( service.getName() );
                m_context.unexportObject( name, clazz );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "unexport.error", name, service.getName(), reason );
                getLogger().error( message );
            }
        }
    }

    /**
     * Utility method to create a <code>Block</code> object
     * from specified BlockMetaData.
     *
     * @param metaData the BlockMetaData
     * @return the newly created Block object
     * @throws Exception if an error occurs
     */
    private Block createBlock( final BlockMetaData metaData )
        throws Exception
    {
        final ClassLoader classLoader = m_context.getClassLoader();
        final Class clazz = classLoader.loadClass( metaData.getClassname() );
        return (Block)clazz.newInstance();
    }

    /**
     * Create a BlockContext object for Block with specified name.
     *
     * @param name the name of Block
     * @return the created BlockContext
     */
    private BlockContext createBlockContext( final String name )
    {
        final DefaultBlockContext context = new DefaultBlockContext( name, m_context );
        setupLogger( context );
        return context;
    }

    /**
     * Retrieve a configuration for specified component.
     * If the configuration is missing then a exception
     * is raised with an appropraite error message.
     *
     * @param name the name of component
     * @return the Configuration object
     * @throws ConfigurationException if an error occurs
     */
    private Configuration getConfiguration( final String name, final int type )
        throws ConfigurationException
    {
        try
        {
            return m_context.getConfiguration( name );
        }
        catch( final ConfigurationException ce )
        {
            //Note that this shouldn't ever happen once we
            //create a Config validator
            final String message =
                REZ.getString( "missing-configuration", new Integer( type ), name );
            throw new ConfigurationException( message, ce );
        }
    }

    /**
     * Create a <code>ComponentManager</code> object for a
     * specific <code>Block</code>. This requires that for
     * each dependency a reference to providing <code>Block</code>
     * is aaqiured from the Application and placing it in
     * <code>ComponentManager</code> under the correct name.
     *
     * @param metaData the BlockMetaData representing block
     * @return the created ComponentManager
     */
    private ComponentManager createComponentManager( final BlockMetaData metaData )
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        final DependencyMetaData[] roles = metaData.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Block dependency = m_application.getBlock( role.getName() );
            componentManager.put( role.getRole(), dependency );
        }

        return componentManager;
    }

    private ServiceManager createServiceManager( final BlockMetaData metaData )
    {
        final DefaultServiceManager manager = new DefaultServiceManager();
        final DependencyMetaData[] roles = metaData.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Block dependency = m_application.getBlock( role.getName() );
            manager.put( role.getRole(), dependency );
        }

        return manager;
    }

    /**
     * Utility method to report that a lifecycle stage is about to be processed.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     */
    private void notice( final String name, final int stage )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "lifecycle-stage.notice", name, new Integer( stage ) );
            getLogger().debug( message );
        }
    }

    /**
     * Utility method to report that there was an error processing
     * specified lifecycle stage.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     */
    private void safeFail( final String name, final int stage, final Throwable t )
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.getString( "lifecycle-fail.error", name, new Integer( stage ), reason );
        getLogger().error( message );
    }

    /**
     * Utility method to report that there was an error processing
     * specified lifecycle stage. It will also rethrow an exception
     * with a better error message.
     *
     * @param name the name of block that caused failure
     * @param stage the stage
     * @param t the exception thrown
     * @throws Exception containing error
     */
    private void fail( final String name, final int stage, final Throwable t )
        throws Exception
    {
        //final String reason = t.getMessage();
        final String reason = t.toString();
        final String message =
            REZ.getString( "lifecycle-fail.error", name, new Integer( stage ), reason );
        getLogger().error( message );
        throw new CascadingException( message, t );
    }
}
