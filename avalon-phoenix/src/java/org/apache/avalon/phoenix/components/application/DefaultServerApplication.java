/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.components.manager.SystemManager;
import org.apache.avalon.excalibur.container.AbstractContainer;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.frame.DefaultApplicationFrame;
import org.apache.avalon.phoenix.components.phases.BlockVisitor;
import org.apache.avalon.phoenix.components.phases.ShutdownPhase;
import org.apache.avalon.phoenix.components.phases.StartupPhase;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.components.listeners.BlockListenerSupport;
import org.apache.avalon.phoenix.components.listeners.BlockListenerManager;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.tools.verifier.Verifier;
import org.apache.avalon.phoenix.tools.verifier.DefaultVerifier;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public final class DefaultServerApplication
    extends AbstractContainer
    implements Application, Contextualizable, Composable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultServerApplication.class );

    private DefaultConfigurationBuilder     m_builder = new DefaultConfigurationBuilder();

    //the following are used for setting up facilities
    private Context                  m_context;
    private Configuration            m_configuration;
    private ComponentManager         m_componentManager;

    //these are the facilities (internal components) of ServerApplication
    private ApplicationFrame         m_frame;
    private BlockListenerManager     m_listenerManager;

    private BlockListenerMetaData[]  m_listeners;
    private BlockMetaData[]          m_blocks;

    private BlockVisitor             m_startupVisitor;
    private BlockVisitor             m_shutdownVisitor;

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    private SarMetaData             m_metaData;

    public DefaultServerApplication()
    {
        m_frame = new DefaultApplicationFrame();
        m_listenerManager = new BlockListenerSupport();
        m_startupVisitor = new StartupPhase();
        m_shutdownVisitor = new ShutdownPhase();
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        //save it to contextualize facilities
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        final DefaultComponentManager newComponentManager =
            new DefaultComponentManager( componentManager );

        //Setup component manager with new components added
        //by application
        newComponentManager.put( ApplicationFrame.ROLE, m_frame );
        newComponentManager.put( BlockListenerManager.ROLE, m_listenerManager );
        newComponentManager.put( Container.ROLE, this );
        newComponentManager.makeReadOnly();

        m_componentManager = newComponentManager;

        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
    }

    public void setup( final SarMetaData metaData )
    {
        m_metaData = metaData;
        m_blocks = metaData.getBlocks();
        m_listeners = metaData.getListeners();
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
        setupComponent( m_frame, "frame" );
        setupComponent( m_startupVisitor, "startup" );
        setupComponent( m_shutdownVisitor, "shutdown" );
    }

    /**
     * Startup application by running startup phase on all the blocks.
     *
     * @exception Exception if an error occurs
     */
    public void start()
        throws Exception
    {      
        final String message = REZ.getString( "app.notice.block.loading-count",
                                              new Integer( m_blocks.length ) );
        getLogger().info( message );

        final DefaultVerifier verifier = new DefaultVerifier();
        setupLogger( verifier );
        verifier.verifySar( m_metaData, m_frame.getClassLoader() );

        for( int i = 0; i < m_blocks.length; i++ )
        {
            final String blockName = m_blocks[ i ].getName();
            final BlockEntry blockEntry = new BlockEntry( m_blocks[ i ] );
            add( blockName, blockEntry );
        }

        // load block listeners
        loadBlockListeners();

        // load blocks
        runPhase( "startup", m_startupVisitor, true  );
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
        final String message = REZ.getString( "app.notice.block.unloading-count",
                                              new Integer( m_blocks.length ) );
        getLogger().info( message );

        runPhase( "shutdown", m_shutdownVisitor, false  );
    }

    public void dispose()
    {
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            try { remove( names[ i ] ); }
            catch( final ContainerException ce )
            {
                final String message = REZ.getString( "app.error.failremove", names[ i ] );
                getLogger().warn( message, ce );
            }
        }
    }

    private void loadBlockListeners()
        throws Exception
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            final BlockListenerMetaData listener = m_listeners[ i ];
            
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
        final ClassLoader classLoader = m_frame.getClassLoader();
        final Class clazz = classLoader.loadClass( entry.getClassname() );
        final BlockListener listener = (BlockListener)clazz.newInstance();
        
        if( listener instanceof Configurable )
        {
            final String name = entry.getName();

            Configuration configuration = null;
            try 
            {
                configuration = m_repository.getConfiguration( m_metaData.getName(), name );
            } 
            catch( final ConfigurationException ce ) 
            {
                final String message = REZ.getString( "app.error.listener.noconfiguration", name );
                throw new ConfigurationException( message, ce );
            }
            
            ((Configurable)listener).configure( configuration );
        }
        
        m_listenerManager.addBlockListener( listener );
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

        if( component instanceof Contextualizable )
        {
            ((Contextualizable)component).contextualize( m_context );
        }

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
    protected final void runPhase( final String name, 
                                   final BlockVisitor visitor, 
                                   final boolean forward )
        throws Exception
    {
        final String[] path = DependencyGraph.walkGraph( forward, m_blocks );
        
        if( getLogger().isInfoEnabled() )
        {
            final List pathList = Arrays.asList( path );
            final String message = REZ.getString( "app.notice.dependency-path", name, pathList );
            getLogger().info( message );
            System.out.println( message );
        }
        
        for( int i = 0; i < path.length; i++ )
        {
            try
            {
                final BlockEntry entry = (BlockEntry)getEntry( path[ i ] );
                visitor.visitBlock( path[ i ], entry );
            }
            catch( final Exception e )
            {
                final String message = 
                    REZ.getString( "app.error.run-phase", name, path[ i ], e.getMessage() );
                getLogger().error( message, e );
                throw e;
            }
        }
    }
}
