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
import org.apache.avalon.phoenix.components.phases.Traversal;
import org.apache.avalon.phoenix.components.phases.BlockDAG;
import org.apache.avalon.phoenix.components.phases.BlockVisitor;
import org.apache.avalon.phoenix.components.phases.ShutdownPhase;
import org.apache.avalon.phoenix.components.phases.StartupPhase;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.metadata.RoleMetaData;
import org.apache.avalon.phoenix.metadata.BlockListenerMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.BlockInfoBuilder;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.components.listeners.BlockListenerSupport;
import org.apache.avalon.phoenix.components.listeners.BlockListenerManager;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;

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

    private final static class PhaseEntry
    {
        protected Traversal           m_traversal;
        protected BlockVisitor        m_visitor;
    }

    private DefaultConfigurationBuilder     m_builder = new DefaultConfigurationBuilder();

    private HashMap                  m_phases           = new HashMap();
    private BlockDAG                 m_dag              = new BlockDAG();

    //the following are used for setting up facilities
    private Context                  m_context;
    private Configuration            m_configuration;
    private ComponentManager         m_componentManager;

    //these are the facilities (internal components) of ServerApplication
    private ApplicationFrame         m_frame;
    private BlockListenerManager     m_listenerManager;
    private BlockListenerMetaData[]     m_listenerEntrys;

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    ///Name of application, phase is running in
    private String               m_appName;

    public DefaultServerApplication()
    {
        m_frame = createFrame();
        m_listenerManager = new BlockListenerSupport();
    }

    public void contextualize( final Context context )
        throws ContextException
    {
        //save it to contextualize facilities
        m_context = context;

        m_appName = (String)context.get( "app.name" );
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

    public void addBlockListeners( final BlockListenerMetaData[] listeners )
    {
        m_listenerEntrys = listeners;
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
        setupComponent( m_dag, "dag" );

        setupPhases();
    }

    /**
     * Create a frame for application.
     * Overide this in subclasses to provide different types of frames.
     *
     * @return the ApplicationFrame
     */
    protected ApplicationFrame createFrame()
    {
        return new DefaultApplicationFrame();
    }

    /**
     * Make sure Entry is of correct type.
     *
     * @param name the name of entry
     * @param entry the entry
     * @exception ContainerException to stop removal of entry
     */
    protected final void preAdd( final String name, final Entry entry )
        throws ContainerException
    {
        if( !(entry instanceof BlockEntry) )
        {
            final String message = REZ.getString( "app.error.bad.entry-type" );
            throw new ContainerException( message );
        }
    }

    /**
     * Initialize and setup the phases for application.
     * Currently there is only the Startup and Shutdown
     * phases implemented.
     *
     * @exception Exception if an error occurs
     */
    protected void setupPhases()
        throws Exception
    {
        PhaseEntry entry = new PhaseEntry();
        entry.m_visitor = new StartupPhase();
        entry.m_traversal = Traversal.FORWARD;
        m_phases.put( "startup", entry );
        setupComponent( entry.m_visitor, "StartupPhase" );

        entry = new PhaseEntry();
        entry.m_visitor = new ShutdownPhase();
        entry.m_traversal = Traversal.REVERSE;
        m_phases.put( "shutdown", entry );
        setupComponent( entry.m_visitor, "ShutdownPhase" );
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
                                              new Integer( getEntryCount() ) );
        getLogger().info( message );

        // load block info
        loadBlockInfos();

        // load block listeners
        loadBlockListeners();

        // load blocks
        final PhaseEntry entry = (PhaseEntry)m_phases.get( "startup" );
        runPhase( "startup", entry );
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
                                              new Integer( getEntryCount() ) );
        getLogger().info( message );

        final PhaseEntry entry = (PhaseEntry)m_phases.get( "shutdown" );
        runPhase( "shutdown", entry );
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
        for( int i = 0; i < m_listenerEntrys.length; i++ )
        {
            final BlockListenerMetaData entry = m_listenerEntrys[ i ];
            
            try
            {
                loadBlockListener( entry );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "app.error.bad-listener", entry.getName() );
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
                configuration = m_repository.getConfiguration( m_appName, name );
            } 
            catch( final ConfigurationException ce ) 
            {
                // missing configuration (probably).
                final String message = REZ.getString( "app.error.listener.noconfiguration", name );
                throw new ConfigurationException( message, ce );
            }
            
            ((Configurable)listener).configure( configuration );
        }
        
        m_listenerManager.addBlockListener( listener );
    }

    /**
     * Load the block infos for every block.
     * Make sure the block infos correctly represents each block.
     *
     * @exception Exception if an error occurs
     */
    private void loadBlockInfos()
        throws Exception
    {
        final String[] names = list();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = (String)names[ i ];
            final BlockEntry entry = (BlockEntry)getEntry( name );

            final BlockInfo info = getBlockInfo( name, entry );
            entry.setInfo( info );

            //Make sure the entry has all relevent
            //dependencies specified and that they match up with
            //dependencies indicated in BlockInfo.
            verifyDependenciesMap( name, entry );
        }
    }

    /**
     * Get a BlockInfo for a particular block.
     * The BlockInfo may be loaded from cache or via ClassLoader.
     *
     * @param name the name of entry
     * @param entry the block entry
     * @return the BlockInfo
     * @exception Exception if an error occurs
     */
    private BlockInfo getBlockInfo( final String name, final BlockEntry entry )
        throws Exception
    {
        //We should cache copies here...
        final String classname = entry.getMetaData().getClassname();
        final String resourceName = classname.replace( '.', '/' ) + ".xinfo";

        final String notice = REZ.getString( "app.notice.blockinfo.resource", resourceName );
        getLogger().info( notice );

        final ClassLoader classLoader = m_frame.getClassLoader();
        final URL resource = classLoader.getResource( resourceName );

        if( null == resource )
        {
            final String message = REZ.getString( "app.error.blockinfo.missing", name, resourceName );
            getLogger().error( message );
            throw new Exception( message );
        }

        try 
        { 
            final Configuration info = m_builder.build( resource.toString() );
            return BlockInfoBuilder.build( classname, info ); 
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "app.error.blockinfo.nocreate", name, resourceName );
            getLogger().error( message, e );
            throw e;
        }
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
     * @param name the name of phase for logging purposes
     * @param phase the phase
     * @exception Exception if an error occurs
     */
    protected final void runPhase( final String name, final PhaseEntry phase )
        throws Exception
    {
        try
        {
            m_dag.walkGraph( phase.m_visitor, phase.m_traversal );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "app.error.phase.run", name );
            getLogger().error( message, e );
            throw e;
        }
    }

    /**
     * Retrieve a list of RoleMetaData objects that were specified
     * in configuration file and verify they were expected based
     * on BlockInfo file. Also verify that all entries specified
     * in BlockInfo file have been provided.
     *
     * @param entry the BlockEntry describing block
     * @return the list of RoleMetaData objects
     */
    private void verifyDependenciesMap( final String name, final BlockEntry entry )
        throws Exception
    {
        //Make sure all role entries specified in config file are valid
        final RoleMetaData[] roles = entry.getMetaData().getRoles();
        for( int i = 0; i < roles.length; i++ )
        {
            final String role = roles[ i ].getRole();
            final DependencyDescriptor descriptor = entry.getBlockInfo().getDependency( role );

            if( null == descriptor )
            {
                final String message = REZ.getString( "app.error.depends.unknown",
                                                      roles[ i ].getName(),
                                                      role,
                                                      name );
                getLogger().warn( message );
                throw new Exception( message );
            }
        }

        //Make sure all dependencies in BlockInfo file are satisfied
        final DependencyDescriptor[] dependencies = entry.getBlockInfo().getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final RoleMetaData role = entry.getMetaData().getRole( dependencies[ i ].getRole() );

            if( null == role )
            {
                final String message = REZ.getString( "app.error.depends.noprovide",
                                                      dependencies[ i ].getRole(),
                                                      name );
                getLogger().warn( message );
                throw new Exception( message );
            }
        }
    }
}
