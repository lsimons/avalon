/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.frame.DefaultApplicationFrame;
import org.apache.avalon.phoenix.components.manager.SystemManager;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class DefaultServerApplication
    extends AbstractLoggable
    implements Application, Composable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultServerApplication.class );

    private static final String  PHASE_STARTUP  = "startup";
    private static final String  PHASE_SHUTDOWN = "shutdown";

    //the following are used for setting up facilities
    private Configuration     m_configuration;
    private ComponentManager  m_componentManager;

    //these are the facilities (internal components) of ServerApplication
    private ApplicationFrame  m_frame;

    private LifecycleHelper   m_lifecycle;

    private SarMetaData       m_metaData;
    private ClassLoader       m_classLoader;

    private HashMap           m_entrys = new HashMap();

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = componentManager;
    }

    public void setup( final SarMetaData metaData, final ClassLoader classLoader )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
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

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    /**
     * Initialize application.
     * This involves setting up the phases, the ApplicationFrame and the DAG used to run phases.
     *
     * @exception Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        m_frame = new DefaultApplicationFrame( m_classLoader, m_metaData );
        setupComponent( m_frame, "frame" );

        m_lifecycle = new LifecycleHelper( this, m_frame );
        setupLogger( m_lifecycle, "lifecycle" );
    }

    /**
     * Startup application by running startup phase on all the blocks.
     *
     * @exception Exception if an error occurs
     */
    public void start()
        throws Exception
    {
        final BlockMetaData[] blocks = m_metaData.getBlocks();
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

    /**
     * Shutdown the application.
     * This involves shutting down every contained block.
     *
     * @exception Exception if an error occurs
     */
    public void stop()
        throws Exception
    {
        runPhase( PHASE_SHUTDOWN );
    }

    public void dispose()
    {
        m_entrys.clear();
    }

    private void loadBlockListeners()
        throws Exception
    {
        final BlockListenerMetaData[] listeners = m_metaData.getListeners();

        for( int i = 0; i < listeners.length; i++ )
        {
            final BlockListenerMetaData listener = listeners[ i ];
            try
            {
                loadBlockListener( listener );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "app.error.bad-listener", listener.getName() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }

    private void loadBlockListener( final BlockListenerMetaData entry )
        throws Exception
    {
        final Class clazz = m_classLoader.loadClass( entry.getClassname() );
        final BlockListener listener = (BlockListener)clazz.newInstance();

        if( listener instanceof Configurable )
        {
            final String name = entry.getName();

            Configuration configuration = null;
            try
            {
                configuration = m_frame.getConfiguration( name );
            }
            catch( final ConfigurationException ce )
            {
                final String message = REZ.getString( "missing-listener-configuration", name );
                throw new ConfigurationException( message, ce );
            }

            ((Configurable)listener).configure( configuration );
        }

        m_frame.addBlockListener( listener );
    }

    /**
     * Setup a component in this application.
     *
     * @param object the component
     * @param logName the name to use to setup logging
     * @exception Exception if an error occurs
     */
    protected final void setupComponent( final Component component, final String logName )
        throws Exception
    {
        setupLogger( component, logName );

        if( component instanceof Composable )
        {
            ((Composable)component).compose( m_componentManager );
        }

        if( component instanceof Configurable )
        {
            ((Configurable)component).configure( m_configuration );
        }

        if( component instanceof Initializable )
        {
            ((Initializable)component).initialize();
        }
    }

    /**
     * Run a phase for application.
     * Each phase transitions application into new state and processes
     * all the blocks to make sure they are in that state aswell.
     * Exceptions leave the blocks in an indeterminate state.
     *
     * @param name the name of phase (for logging purposes)
     * @exception Exception if an error occurs
     */
    protected final void runPhase( final String name )
        throws Exception
    {
        final BlockMetaData[] blocks = m_metaData.getBlocks();
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

        //Setup thread context for calling visitors
        ThreadContext.setThreadContext( m_frame.getThreadContext() );

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
                if( PHASE_STARTUP == name ) m_lifecycle.startup( entry );
                else m_lifecycle.shutdown( entry );
            }
            catch( final Exception e )
            {
                final String message =
                    REZ.getString( "app.error.run-phase", name, block, e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }

            //Log message saying we have processed block
            if( getLogger().isDebugEnabled() )
            {
                final String message = REZ.getString( "processed-block", block, name );
                getLogger().debug( message );
            }
        }
    }
}
