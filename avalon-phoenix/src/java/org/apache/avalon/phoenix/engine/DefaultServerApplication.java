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
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Application;
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
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.frame.DefaultApplicationFrame;
import org.apache.avalon.phoenix.engine.blocks.BlockDAG;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.BlockVisitor;
import org.apache.avalon.phoenix.engine.blocks.RoleEntry;
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
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultServerApplication.class );

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
    private DefaultComponentManager  m_componentManager;

    //these are the facilities (internal components) of ServerApplication
    private ApplicationFrame         m_frame;


    public void contextualize( final Context context )
        throws ContextException
    {
        //save it to contextualize facilities
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = new DefaultComponentManager( componentManager );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
        throws Exception
    {
        m_frame = new DefaultApplicationFrame();

        //setup the component manager
        m_componentManager.put( ApplicationFrame.ROLE, m_frame );
        m_componentManager.put( Container.ROLE, this );
        m_componentManager.makeReadOnly();

        setupComponent( m_frame, "frame" );
        setupComponent( m_dag, "dag" );

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
            final String message = REZ.getString( "app.error.bad.entry-type" );
            throw new ContainerException( message );
        }
    }

    protected void setupPhases()
        throws Exception
    {
        PhaseEntry entry = new PhaseEntry();
        entry.m_visitor = new StartupPhase();
        entry.m_traversal = BlockDAG.FORWARD;
        m_phases.put( "startup", entry );
        setupComponent( entry.m_visitor, "StartupPhase" );

        entry = new PhaseEntry();
        entry.m_visitor = new ShutdownPhase();
        entry.m_traversal = BlockDAG.REVERSE;
        m_phases.put( "shutdown", entry );
        setupComponent( entry.m_visitor, "ShutdownPhase" );
    }

    public void start()
        throws Exception
    {
        // load block info
        loadBlockInfos();

        // load blocks
        final String message = REZ.format( "app.notice.block.loading-count",
                                           new Integer( getEntryCount() ) );
        getLogger().info( message );
        final PhaseEntry entry = (PhaseEntry)m_phases.get( "startup" );
        runPhase( "startup", entry );
    }

    public void stop()
        throws Exception
    {
    }

    public void dispose()
    {
        final String message = REZ.format( "app.notice.block.unloading-count",
                                           new Integer( getEntryCount() ) );
        getLogger().info( message );

        final PhaseEntry entry = (PhaseEntry)m_phases.get( "shutdown" );
        try { runPhase( "shutdown", entry ); }
        catch( final Exception e )
        {
            //Already been logged so we can ignore exception
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

        final String notice = REZ.format( "app.notice.blockinfo.resource", resourceName );
        getLogger().info( notice );

        final ClassLoader classLoader = m_frame.getClassLoader();
        final URL resource = classLoader.getResource( resourceName );

        if( null == resource )
        {
            final String message = REZ.format( "app.error.blockinfo.missing", name, resourceName );
            getLogger().error( message );
            throw new Exception( message );
        }

        try { return m_builder.build( resource.toString() ); }
        catch( final Exception e )
        {
            final String message = REZ.format( "app.error.blockinfo.nocreate", name, resourceName );
            getLogger().error( message, e );
            throw e;
        }
    }

    protected final void setupComponent( final Component object, final String logName )
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
            ((Configurable)object).configure( m_configuration );
        }

        if( object instanceof Initializable )
        {
            ((Initializable)object).initialize();
        }
    }

    protected final void runPhase( final String name, final PhaseEntry phase )
        throws Exception
    {
        try
        {
            m_dag.walkGraph( phase.m_visitor, phase.m_traversal );
        }
        catch( final Exception e )
        {
            final String message = REZ.format( "app.error.phase.run", name );
            getLogger().error( message, e );
            throw e;
        }
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
                final String message = REZ.format( "app.error.depends.unknown",
                                                   roleEntrys[ i ].getName(),
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
            final RoleEntry roleEntry =
                entry.getRoleEntry( dependencies[ i ].getRole() );

            if( null == roleEntry )
            {
                final String message = REZ.format( "app.error.depends.noprovide",
                                                   dependencies[ i ].getRole(),
                                                   name );
                getLogger().warn( message );
                throw new Exception( message );
            }
        }
    }
}
