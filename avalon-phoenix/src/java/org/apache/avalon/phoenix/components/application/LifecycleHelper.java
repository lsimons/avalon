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
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;

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
    private final static int STAGE_CREATE = 0;
    private final static int STAGE_LOGGER = 1;
    private final static int STAGE_CONTEXT = 2;
    private final static int STAGE_COMPOSE = 3;
    private final static int STAGE_CONFIG = 4;
    private final static int STAGE_INIT = 5;
    private final static int STAGE_START = 6;
    private final static int STAGE_STOP = 7;
    private final static int STAGE_DISPOSE = 8;
    private final static int STAGE_DESTROY = 9;

    //Constants to designate type of component
    private final static int TYPE_BLOCK = 0;
    private final static int TYPE_LISTENER = 1;

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
    private BlockListenerSupport m_listenerSupport = new BlockListenerSupport();

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
     * @exception Exception if an error occurs when listener passes
     *            through a specific lifecycle stage
     */
    public void startupListener( final BlockListenerMetaData metaData )
        throws Exception
    {
        final String name = metaData.getName();

        final ClassLoader classLoader = m_context.getClassLoader();
        final Class clazz = classLoader.loadClass( metaData.getClassname() );
        final BlockListener listener = (BlockListener)clazz.newInstance();

        if( listener instanceof Configurable )
        {
            final Configuration configuration = getConfiguration( name, TYPE_LISTENER );
            ( (Configurable)listener ).configure( configuration );
        }

        m_listenerSupport.addBlockListener( listener );
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
     * @exception Exception if an error occurs when block passes
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
            if( block instanceof Loggable )
            {
                notice( name, stage );
                ( (Loggable)block ).setLogger( m_context.getLogger( name ) );
            }
            else if( block instanceof LogEnabled )
            {
                notice( name, stage );
                final Logger logger = new LogKitLogger( m_context.getLogger( name ) );
                ( (LogEnabled)block ).enableLogging( logger );
            }

            //Contextualize stage
            stage = STAGE_CONTEXT;
            if( block instanceof Contextualizable )
            {
                notice( name, stage );
                final BlockContext context = createBlockContext( name );
                ( (Contextualizable)block ).contextualize( context );
            }

            //Composition stage
            stage = STAGE_COMPOSE;
            if( block instanceof Composable )
            {
                notice( name, stage );
                final ComponentManager componentManager = createComponentManager( metaData );
                ( (Composable)block ).compose( componentManager );
            }

            //Configuring stage
            stage = STAGE_CONFIG;
            if( block instanceof Configurable )
            {
                notice( name, stage );
                final Configuration configuration = getConfiguration( name, TYPE_BLOCK );
                ( (Configurable)block ).configure( configuration );
            }

            //Initialize stage
            stage = STAGE_INIT;
            if( block instanceof Initializable )
            {
                notice( name, stage );
                ( (Initializable)block ).initialize();
            }

            //Start stage
            stage = STAGE_START;
            if( block instanceof Startable )
            {
                notice( name, stage );
                ( (Startable)block ).start();
            }

            entry.setState( State.STARTED );
            entry.setBlock( block );

            final BlockEvent event =
                new BlockEvent( name, entry.getProxy(), metaData.getBlockInfo() );
            m_listenerSupport.blockAdded( event );
        }
        catch( final Throwable t )
        {
            entry.setState( State.FAILED );
            fail( name, stage, t );
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
        m_listenerSupport.blockRemoved( event );

        final Block block = entry.getBlock();

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
                ( (Startable)block ).stop();
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
                ( (Disposable)block ).dispose();
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
     * Utility method to create a <code>Block</code> object
     * from specified BlockMetaData.
     *
     * @param metaData the BlockMetaData
     * @return the newly created Block object
     * @exception Exception if an error occurs
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
     * @exception ConfigurationException if an error occurs
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
     * @exception Exception containing error
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
