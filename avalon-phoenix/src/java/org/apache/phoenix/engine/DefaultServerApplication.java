/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine;

import java.security.Policy;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.DefaultContext;
import org.apache.avalon.Initializable;
import org.apache.avalon.atlantis.Application;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.camelot.AbstractContainer;
import org.apache.avalon.camelot.ContainerException;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.Factory;
import org.apache.phoenix.engine.facilities.ComponentBuilder;
import org.apache.phoenix.engine.facilities.ComponentManagerBuilder;
import org.apache.phoenix.engine.facilities.ConfigurationRepository;
import org.apache.phoenix.engine.facilities.LoggerBuilder;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.phoenix.engine.blocks.BlockDAG;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.blocks.RoleEntry;
import org.apache.phoenix.engine.facilities.DefaultComponentBuilder;
import org.apache.phoenix.engine.facilities.DefaultComponentManagerBuilder;
import org.apache.phoenix.engine.facilities.DefaultConfigurationRepository;
import org.apache.phoenix.engine.facilities.DefaultLogManager;
import org.apache.phoenix.engine.facilities.DefaultLoggerBuilder;
import org.apache.phoenix.engine.facilities.security.DefaultPolicy;
import org.apache.phoenix.engine.facilities.DefaultThreadManager;
import org.apache.phoenix.engine.facilities.classmanager.SarClassLoader;
import org.apache.phoenix.engine.phases.ShutdownPhase;
import org.apache.phoenix.engine.phases.StartupPhase;
import org.apache.phoenix.engine.blocks.BlockVisitor;
import org.apache.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.util.Enum;

/**
 * This is the basic container of blocks. A server application
 * represents an aggregation of blocks that act together to form
 * an application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 */
public class DefaultServerApplication
    extends AbstractContainer
    implements Application, Configurable, Contextualizable
{

    protected final static Traversal  FORWARD     = new Traversal( "FORWARD" );
    protected final static Traversal  REVERSE     = new Traversal( "REVERSE" );
    protected final static Traversal  LINEAR      = new Traversal( "LINEAR" );

    protected final static class Traversal
        extends Enum
    {
        protected Traversal( final String name )
        {
            super( name );
        }
    }
    
    protected final static class PhaseEntry
    {
        protected Traversal     m_traversal;
        protected BlockVisitor  m_visitor;
    }

    protected HashMap                  m_phases           = new HashMap();
    protected BlockDAG                 m_dag              = new BlockDAG();

    //the following are used for setting up facilities
    protected Context                  m_context;
    protected Configuration            m_configuration;
    protected ComponentManager         m_componentManager;

    protected DefaultLogManager        m_logManager;
    protected ThreadManager            m_threadManager;
    protected DefaultPolicy            m_policy;
    protected SarClassLoader           m_classLoader;

    //these are the facilities (internal components) of ServerApplication
    protected ComponentBuilder         m_componentBuilder;
    protected LoggerBuilder            m_loggerBuilder;
    protected ComponentManagerBuilder  m_componentManagerBuilder;
    protected ConfigurationRepository  m_configurationRepository;

    public DefaultServerApplication()
    {
        m_entryClass = BlockEntry.class;
    }

    public void contextualize( final Context context )
    {
        //save it to contextualize policy/logManager/etc
        final DefaultContext newContext = new DefaultContext( context );
        newContext.put( "name", context.get( SarContextResources.APP_NAME ) );
        newContext.put( "directory", context.get( SarContextResources.APP_HOME_DIR ) );
        m_context = newContext;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void init()
        throws Exception
    {
        createComponents();

        //setup the component manager
        m_componentManager = createComponentManager();

        setupComponents();

        initPhases();
        setupPhases();
    }

    protected void initPhases()
        throws ApplicationException
    {
        PhaseEntry entry = new PhaseEntry();
        entry.m_visitor = new StartupPhase();
        entry.m_traversal = FORWARD;
        m_phases.put( "startup", entry );
        
        entry = new PhaseEntry();
        entry.m_visitor = new ShutdownPhase();
        entry.m_traversal = REVERSE;
        m_phases.put( "shutdown", entry );
    }

    protected void setupPhases()
        throws Exception
    {
        final Iterator phases = m_phases.values().iterator();
        while( phases.hasNext() )
        {
            final PhaseEntry entry = (PhaseEntry)phases.next();
            setupComponent( entry.m_visitor );
        }
    }

    public void start()
        throws Exception
    {
        // load blocks
        try
        {
            getLogger().info( "Number of blocks to load: " + m_entries.size() );
            final PhaseEntry entry = (PhaseEntry)m_phases.get( "startup" );
            runPhase( entry.m_visitor, entry.m_traversal );
        }
        catch( final ApplicationException ae )
        {
            getLogger().warn( "Error loading blocks: " + ae.getMessage(), ae );
            throw ae;
        }
    }

    public void stop()
        throws Exception
    {
    }

    public void dispose()
        throws Exception
    {
        getLogger().info( "Number of blocks to unload: " + m_entries.size() );

        final PhaseEntry entry = (PhaseEntry)m_phases.get( "shutdown" );
        runPhase( entry.m_visitor, entry.m_traversal );

        m_entries.clear();
    }

    /**
     * Create all required components.
     *
     * @exception Exception if an error occurs
     */
    protected void createComponents()
        throws Exception
    {
        //TODO: Refactor logManager so it does more useful managing
        // possibly including setting up rolling etc
        m_logManager = new DefaultLogManager();

        m_componentManagerBuilder = new DefaultComponentManagerBuilder();
        m_configurationRepository = new DefaultConfigurationRepository();
        m_loggerBuilder = new DefaultLoggerBuilder();
        m_componentBuilder = new DefaultComponentBuilder();

        m_classLoader = new SarClassLoader();
        m_threadManager = new DefaultThreadManager();
        m_policy = new DefaultPolicy();
    }

    /**
     * Setup all the components. (ir run all required lifecycle methods).
     *
     * @exception Exception if an error occurs
     */
    protected void setupComponents()
        throws Exception
    {
        Configuration configuration = null;

        configuration = m_configuration.getChild( "logs" );
        setupComponent( m_logManager, "<core>.logs", configuration );

        configuration = m_configuration.getChild( "threads" );
        setupComponent( m_threadManager, "<core>.threads", configuration );

        configuration = m_configuration.getChild( "policy" );
        setupComponent( (Component)m_policy, "<policy>", configuration );

        setupComponent( m_classLoader );

        setupComponent( m_componentBuilder );
        setupComponent( m_loggerBuilder );
        setupComponent( m_componentManagerBuilder );
        setupComponent( m_configurationRepository );

        setupComponent( m_dag, "<core>.dag", null );
    }

    protected void setupComponent( final Component object )
        throws Exception
    {
        setupComponent( object, null, null );
    }

    protected void setupComponent( final Component object,
                                   final String logName,
                                   final Configuration configuration )
        throws Exception
    {
        setupLogger( object, logName );

        if( object instanceof Contextualizable )
        {
            ((Contextualizable)object).contextualize( m_context );
        }

        if( object instanceof Composer )
        {
            ((Composer)object).compose( m_componentManager );
        }

        if( object instanceof Configurable )
        {
            ((Configurable)object).configure( configuration );
        }

        if( object instanceof Initializable )
        {
            ((Initializable)object).init();
        }
    }

    protected void runPhase( final BlockVisitor visitor, final Traversal traversal )
        throws Exception
    {
        if( FORWARD == traversal )
        {
            final Iterator entries = list();
            while( entries.hasNext() )
            {
                final String name = (String)entries.next();
                m_dag.walkGraph( name, visitor );
            }
        }
        else if( REVERSE == traversal )
        {
            //TODO:
            final Iterator entries = list();
            while( entries.hasNext() )
            {
                final String name = (String)entries.next();
                //m_dag.reverseWalkGraph( name, visitor );
            }
        }
        else
        {
            //TODO: Does this make sense ????
            final Iterator entries = list();
            while( entries.hasNext() )
            {
                final String name = (String)entries.next();
                final BlockEntry entry = (BlockEntry)getEntry( name );
                visitor.visitBlock( name, entry );
            }
        }
    }

    /**
     * This method is called before entry is added to give chance for
     * sub-class to veto addition.
     *
     * @param name the name of entry
     * @param entry the entry
     * @exception ContainerException to stop removal of entry
     */
    protected void preAdd( final String name, final Entry entry )
        throws ContainerException
    {
        final BlockEntry blockEntry = (BlockEntry)entry;
        verifyDependenciesMap( name, blockEntry );
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
    protected void verifyDependenciesMap( final String name, final BlockEntry entry )
        throws ContainerException
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
                throw new ContainerException( message );
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
                throw new ContainerException( message );
            }
        }
    }

    /**
     * Create a ComponentManager containing all components in engine.
     *
     * @return the ComponentManager
     */
    protected ComponentManager createComponentManager()
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        componentManager.put( "org.apache.avalon.camelot.Container", this );
        componentManager.put( "java.security.Policy", m_policy );
        componentManager.put( "java.lang.ClassLoader", m_classLoader );
        componentManager.put( "NOT_DONE_YET", m_logManager );
        componentManager.put( "org.apache.avalon.util.thread.ThreadManager", m_threadManager );
        componentManager.put( "org.apache.phoenix.engine.facilities.LoggerBuilder", m_loggerBuilder );
        componentManager.put( "org.apache.phoenix.engine.facilities.ComponentBuilder",
                              m_componentBuilder );
        componentManager.put( "org.apache.phoenix.engine.facilities.ComponentManagerBuilder",
                              m_componentManagerBuilder );
        componentManager.put( "org.apache.phoenix.engine.facilities.ConfigurationRepository",
                              m_configurationRepository );

        return componentManager;
    }
}
