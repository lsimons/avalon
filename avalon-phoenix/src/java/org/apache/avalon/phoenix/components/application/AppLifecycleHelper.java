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
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.lifecycle.LifecycleHelper;
import org.apache.avalon.phoenix.components.lifecycle.State;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;

/**
 * This is a class to help an Application manage lifecycle of
 * <code>Blocks</code> and <code>BlockListeners</code>. The
 * class will run each individual Entry through each lifecycle stage,
 * and manage erros in a consistent way.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
class AppLifecycleHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AppLifecycleHelper.class );

    ///Frame in which block executes
    private ApplicationContext m_context;

    /**
     * Object to support notification of ApplicationListeners.
     */
    private ListenerSupport m_listenerSupport =
        new ListenerSupport();

    private final LifecycleHelper m_lifecycleHelper = new LifecycleHelper();
    private final ExportHelper m_exportHelper = new ExportHelper();

    /**
     * ResourceAccessor for blocks.
     */
    private final BlockAccessor m_blockAccessor;

    /**
     * ResourceAccessor for listeners.
     */
    private ListenerAccessor m_listenerAccessor;

    /**
     * Construct helper object for specified application,
     * in specified frame.
     *
     * @param application the Application that this object is helper to
     * @param context the ApplicationContext in which this helper operates
     */
    protected AppLifecycleHelper( final Application application,
                                  final ApplicationContext context )
    {
        m_context = context;
        m_blockAccessor = new BlockAccessor( context, application );
        m_listenerAccessor = new ListenerAccessor( context );
    }

    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        setupLogger( m_lifecycleHelper );
        setupLogger( m_exportHelper );
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
        final Object listener = m_listenerAccessor.createObject( metaData );

        if( listener instanceof LogEnabled )
        {
            final Logger logger = m_listenerAccessor.createLogger( metaData );
            ContainerUtil.enableLogging( listener, logger );
        }

        if( listener instanceof Configurable )
        {
            final Configuration configuration = m_listenerAccessor.createConfiguration( metaData );
            ContainerUtil.configure( listener, configuration );
        }

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

    ListenerSupport getAppListenerSupport()
    {
        return m_listenerSupport;
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
        State state = State.FAILED;
        try
        {
            entry.setState( State.STARTING );

            final Object block =
                m_lifecycleHelper.startup( entry, m_blockAccessor );

            m_exportHelper.exportBlock( m_context, entry.getMetaData(), block );

            state = State.STARTED;
            entry.setObject( block );

            m_listenerSupport.fireBlockAddedEvent( entry );
        }
        finally
        {
            entry.setState( state );
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
        entry.setState( State.DESTROYING );
        m_listenerSupport.fireBlockRemovedEvent( entry );

        //Remove block from Management system
        m_exportHelper.unexportBlock( m_context, entry.getMetaData(), entry.getObject() );

        try
        {
            m_lifecycleHelper.shutdown( entry );
        }
        finally
        {
            entry.setObject( null );
            entry.setState( State.DESTROYED );
        }
    }
}
