/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Application;
import org.apache.avalon.framework.atlantis.ApplicationException;
import org.apache.avalon.framework.atlantis.SystemManager;
import org.apache.avalon.framework.camelot.AbstractContainer;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.phoenix.engine.blocks.BlockDAG;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.BlockVisitor;
import org.apache.avalon.phoenix.engine.blocks.RoleEntry;
import org.apache.avalon.phoenix.engine.facilities.ApplicationFrame;
import org.apache.avalon.phoenix.engine.facilities.ApplicationManager;
import org.apache.avalon.phoenix.engine.facilities.ConfigurationRepository;
import org.apache.avalon.phoenix.engine.facilities.application.DefaultApplicationManager;
import org.apache.avalon.phoenix.engine.facilities.configuration.DefaultConfigurationRepository;
import org.apache.avalon.phoenix.engine.facilities.frame.DefaultApplicationFrame;
import org.apache.avalon.phoenix.engine.phases.ShutdownPhase;
import org.apache.avalon.phoenix.engine.phases.StartupPhase;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.BlockInfoBuilder;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;

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
    private final static class PhaseEntry
    {
        protected BlockDAG.Traversal  m_traversal;
        protected BlockVisitor        m_visitor;
    }

    private final BlockInfoBuilder   m_builder          = new BlockInfoBuilder();

    private HashMap                  m_phases           = new HashMap();
    private BlockDAG                 m_dag              = new BlockDAG();

    //the following are used for setting up facilities
    private Context                  m_context;
    private Configuration            m_configuration;
    private ComponentManager         m_componentManager;

    ///SystemManager provided by kernel
    private SystemManager            m_systemManager;

    //these are the facilities (internal components) of ServerApplication
    private ApplicationFrame         m_frame;
    private ApplicationManager       m_manager;
    private ConfigurationRepository  m_repository;

    public void contextualize( final Context context )
        throws ContextException
    {
        //save it to contextualize facilities
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.lookup( SystemManager.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        createComponents();

        //setup the component manager
        m_componentManager = createComponentManager();

        setupComponents();

        setupPhases();
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
            throw new ContainerException( "Only Entries of type BlockEntry " +
                                          "may be placed in container." );
        }
    }

    protected void setupPhases()
        throws Exception
    {
        PhaseEntry entry = new PhaseEntry();
        entry.m_visitor = new StartupPhase();
        entry.m_traversal = BlockDAG.FORWARD;
        m_phases.put( "startup", entry );
        setupComponent( entry.m_visitor, "<core>.phases." + entry.m_traversal.getName(), null );

        entry = new PhaseEntry();
        entry.m_visitor = new ShutdownPhase();
        entry.m_traversal = BlockDAG.REVERSE;
        m_phases.put( "shutdown", entry );
        setupComponent( entry.m_visitor, "<core>.phases." + entry.m_traversal.getName(), null );
    }

    public void start()
        throws Exception
    {
        // load block info
        try { loadBlockInfos(); }
        catch( final Exception e )
        {
            getLogger().warn( "Error loading block infos: " + e.getMessage(), e );
            throw e;
        }

        // load blocks
        try
        {
            getLogger().info( "Number of blocks to load: " + getEntryCount() );
            final PhaseEntry entry = (PhaseEntry)m_phases.get( "startup" );
            runPhase( entry );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error loading blocks: " + e.getMessage(), e );
            throw e;
        }
    }

    public void stop()
        throws Exception
    {
    }

    public void dispose()
    {
        getLogger().info( "Number of blocks to unload: " + getEntryCount() );

        try
        {
            final PhaseEntry entry = (PhaseEntry)m_phases.get( "shutdown" );
            runPhase( entry );
        }
        catch( final Exception e )
        {
            getLogger().error( "Error shutting down application", e );
        }
    }

    private void loadBlockInfos()
        throws Exception
    {
        final Iterator names = list();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            final BlockEntry entry = (BlockEntry)getEntry( name );

            final BlockInfo info = getBlockInfo( name, entry );
            entry.setInfo( info );

            //Make sure the entry has all relevent
            //dependencies specified and that they match up with
            //dependencies indicated in BlockInfo.
            verifyDependenciesMap( name, entry );
        }
    }

    private BlockInfo getBlockInfo( final String name, final BlockEntry entry )
        throws Exception
    {
        //We should cache copies here...
        final String className = entry.getLocator().getName();
        final String resourceName = className.replace( '.', '/' ) + ".xinfo";

        getLogger().info( "Creating block info from " + resourceName );

        final ClassLoader classLoader = m_frame.getClassLoader();
        final URL resource = classLoader.getResource( resourceName );

        if( null == resource )
        {
            final String message =
                "Unable to locate resource for block info " + resourceName;

            getLogger().error( message );
            throw new Exception( message );
        }

        try { return m_builder.build( resource.toString() ); }
        catch( final Exception e )
        {
            getLogger().error( "Failed to create block info for from " + resourceName, e );
            throw e;
        }
    }

    /**
     * Create all required components.
     *
     * @exception Exception if an error occurs
     */
    protected void createComponents()
        throws Exception
    {
        m_frame = new DefaultApplicationFrame();
        m_repository = new DefaultConfigurationRepository();
        m_manager = new DefaultApplicationManager();
    }

    /**
     * Setup all the components. (ir run all required lifecycle methods).
     *
     * @exception Exception if an error occurs
     */
    protected void setupComponents()
        throws Exception
    {
        setupComponent( m_frame, "<core>.frame", m_configuration );
        setupComponent( m_repository, "<core>.config", null );
        setupComponent( m_manager, "<core>.application-manager", null );

        setupComponent( m_dag, "<core>.dag", null );
    }

    protected final void setupComponent( final Component object,
                                         final String logName,
                                         final Configuration configuration )
        throws Exception
    {
        setupLogger( object, logName );

        if( object instanceof Contextualizable )
        {
            ((Contextualizable)object).contextualize( m_context );
        }

        if( object instanceof Composable )
        {
            ((Composable)object).compose( m_componentManager );
        }

        if( object instanceof Configurable )
        {
            ((Configurable)object).configure( configuration );
        }

        if( object instanceof Initializable )
        {
            ((Initializable)object).initialize();
        }
    }

    protected final void runPhase( final PhaseEntry phase )
        throws Exception
    {
        m_dag.walkGraph( phase.m_visitor, phase.m_traversal );
    }

    /**
     * Retrieve a list of RoleEntry objects that were specified
     * in configuration file and verify they were expected based
     * on BlockInfo file. Also verify that all entries specified
     * in BlockInfo file have been provided.
     *
     * @param entry the BlockEntry describing block
     * @return the list of RoleEntry objects
     */
    private void verifyDependenciesMap( final String name, final BlockEntry entry )
        throws Exception
    {
        //Make sure all role entries specified in config file are valid
        final RoleEntry[] roleEntrys = entry.getRoleEntrys();
        for( int i = 0; i < roleEntrys.length; i++ )
        {
            final String role = roleEntrys[ i ].getRole();
            final DependencyDescriptor descriptor = entry.getBlockInfo().getDependency( role );

            if( null == descriptor )
            {
                final String message = "Unknown dependency " + roleEntrys[ i ].getName() +
                    " with role " + role + " declared for Block " + name;

                getLogger().warn( message );
                throw new Exception( message );
            }
        }

        //Make sure all dependencies in BlockInfo file are satisfied
        final DependencyDescriptor[] dependencies = entry.getBlockInfo().getDependencies();
        for( int i = 0; i < dependencies.length; i++ )
        {
            final RoleEntry roleEntry =
                entry.getRoleEntry( dependencies[ i ].getRole() );

            if( null == roleEntry )
            {
                final String message = "Dependency " + dependencies[ i ].getRole() +
                    " not provided in configuration for Block " + name;

                getLogger().warn( message );
                throw new Exception( message );
            }
        }
    }

    /**
     * Create a ComponentManager containing all components in engine.
     *
     * @return the ComponentManager
     */
    private ComponentManager createComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( SystemManager.ROLE, m_systemManager );
        componentManager.put( ApplicationFrame.ROLE, m_frame );
        componentManager.put( ApplicationManager.ROLE, m_manager );
        componentManager.put( ConfigurationRepository.ROLE, m_repository );
        componentManager.put( Container.ROLE, this );
        return componentManager;
    }
}
