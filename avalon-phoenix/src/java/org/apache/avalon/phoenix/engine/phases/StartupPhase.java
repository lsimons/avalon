/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.phases;

import java.io.File;
import java.net.URL;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.ApplicationException;
import org.apache.avalon.framework.camelot.Container;
import org.apache.avalon.framework.camelot.ContainerException;
import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.camelot.Factory;
import org.apache.avalon.framework.camelot.SimpleFactory;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.excalibur.thread.ThreadContext;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.engine.SarContextResources;
import org.apache.avalon.phoenix.engine.blocks.BlockEntry;
import org.apache.avalon.phoenix.engine.blocks.BlockVisitor;
import org.apache.avalon.phoenix.engine.blocks.DefaultBlockContext;
import org.apache.avalon.phoenix.engine.blocks.RoleEntry;
import org.apache.avalon.phoenix.engine.facilities.ClassLoaderManager;
import org.apache.avalon.phoenix.engine.facilities.ConfigurationRepository;
import org.apache.avalon.phoenix.engine.facilities.LogManager;
import org.apache.avalon.phoenix.engine.facilities.ThreadManager;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.BlockUtil;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StartupPhase
    extends AbstractLoggable
    implements BlockVisitor, Contextualizable, Composable
{
    private ClassLoader                 m_classLoader;
    private ConfigurationRepository     m_repository;
    private ThreadManager               m_threadManager;
    private LogManager                  m_logManager;

    ///Factory used to build instance of Block
    private Factory                     m_factory;

    ///base context used to setup hosted blocks
    private DefaultContext              m_baseBlockContext;

    /**
     * The container (ie kernel) which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private Container                   m_container;

    public void contextualize( final Context context )
        throws ContextException
    {
        final File baseDirectory = (File)context.get( SarContextResources.APP_HOME_DIR );
        final String name = (String)context.get( SarContextResources.APP_NAME );

        //base contxt that all block contexts inherit from
        final DefaultContext blockContext = new DefaultContext();
        blockContext.put( BlockContext.APP_NAME, name );
        blockContext.put( BlockContext.APP_HOME_DIR, baseDirectory );

        m_baseBlockContext = blockContext;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        final ClassLoaderManager classLoaderManager = 
            (ClassLoaderManager)componentManager.lookup( ClassLoaderManager.ROLE );

        m_classLoader = classLoaderManager.getClassLoader();

        m_factory = new SimpleFactory( m_classLoader );

        m_container = (Container)componentManager.lookup( Container.ROLE );
        m_threadManager = (ThreadManager)componentManager.lookup( ThreadManager.ROLE );
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
        m_logManager = (LogManager)componentManager.lookup( LogManager.ROLE );
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception ApplicationException if walking is to be stopped
     */
    public void visitBlock( final String name, final BlockEntry entry )
        throws ApplicationException
    {
        if( entry.getState() != BlockEntry.BASE &&
            null != entry.getState() ) return;

        getLogger().info( "Processing Block: " + name );
        getLogger().debug( "Processing with classloader " + m_classLoader );

        //HACK: Hack-o-mania here - Fix when each Application is
        //run in a separate thread group
        Thread.currentThread().setContextClassLoader( m_classLoader );
        ThreadContext.setCurrentThreadPool( m_threadManager.getDefaultThreadPool() );

        try
        {
            //Creation stage
            getLogger().debug( "Pre-Creation Stage" );
            final Object object = createBlock( name, entry );
            entry.setInstance( object );
            getLogger().debug( "Creation successful." );

            //Loggable stage
            if( object instanceof Loggable )
            {
                getLogger().debug( "Pre-Loggable Stage" );
                ((Loggable)object).setLogger( m_logManager.getLogger( name ) );
                getLogger().debug( "Loggable successful." );
            }

            //Contextualize stage
            if( object instanceof Contextualizable )
            {
                getLogger().debug( "Pre-Contextualize Stage" );
                ((Contextualizable)object).contextualize( createContext( name ) );
                getLogger().debug( "Contextualize successful." );
            }

            //Composition stage
            if( object instanceof Composable )
            {
                getLogger().debug( "Pre-Composition Stage" );
                final ComponentManager componentManager =
                    createComponentManager( name, entry );
                ((Composable)object).compose( componentManager );
                getLogger().debug( "Composition successful." );
            }

            //Configuring stage
            if( object instanceof Configurable )
            {
                getLogger().debug( "Pre-Configure Stage" );
                final Configuration configuration = entry.getConfiguration();
                ((Configurable)object).configure( configuration );
                getLogger().debug( "Configure successful." );
            }

            //Initialize stage
            if( object instanceof Initializable )
            {
                getLogger().debug( "Pre-Initializable Stage" );
                ((Initializable)object).initialize();
                getLogger().debug( "Initializable successful." );
            }

            //Start stage
            if( object instanceof Startable )
            {
                getLogger().debug( "Pre-Start Stage" );
                ((Startable)object).start();
                getLogger().debug( "Start successful." );
            }

            entry.setState( BlockEntry.STARTEDUP );

            getLogger().info( "Ran Startup Phase for " + name );
        }
        catch( final Exception e )
        {
            throw new ApplicationException( "Failed to load block " + name, e );
        }
    }

    private Block createBlock( final String name, final BlockEntry entry )
        throws Exception
    {
        getLogger().info( "Creating block " + name );

        final Block block = (Block)m_factory.create( entry.getLocator(), Block.class );

        getLogger().debug( "Created block" );

        verifyBlockServices( name, entry, block );

        getLogger().debug( "Verified block services" );

        return block;
    }

    /**
     * Verify that all the services that a block
     * declares it provides are actually provided.
     *
     * @param name the name of block
     * @param blockEntry the blockEntry
     * @param block the Block
     * @exception ApplicationException if verification fails
     */
    private void verifyBlockServices( final String name,
                                      final BlockEntry entry,
                                      final Block block )
        throws ApplicationException
    {
        final ServiceDescriptor[] services = entry.getBlockInfo().getServices();
        for( int i = 0; i < services.length; i++ )
        {
            if( false == BlockUtil.implementsService( block, services[ i ] ) )
            {
                final String message = "Block " + name + " fails to implement " +
                    "advertised service " + services[ i ];
                getLogger().warn( message );
                throw new ApplicationException( message );
            }
        }
    }

    private Context createContext( final String name )
    {
        final DefaultBlockContext context =
            new DefaultBlockContext( getLogger(), m_threadManager, m_baseBlockContext );
        context.put( BlockContext.NAME, name );
        return context;
    }

    /**
     * Build a ComponentManager for a specific Block.
     *
     * @param name the name of the block
     * @param entry the BlockEntry
     * @return the created ComponentManager
     */
    private ComponentManager createComponentManager( final String name, final BlockEntry entry )
        throws ComponentException
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        final BlockInfo info = entry.getBlockInfo();
        final RoleEntry[] roleEntrys = entry.getRoleEntrys();

        for( int i = 0; i < roleEntrys.length; i++ )
        {
            final String dependencyName = roleEntrys[ i ].getName();
            final ServiceDescriptor serviceDescriptor =
                info.getDependency( roleEntrys[ i ].getRole() ).getService();

            try
            {
                //dependency should NEVER throw ContainerException here as it
                //is validated at entry time
                final BlockEntry dependency =
                    (BlockEntry)m_container.getEntry( dependencyName );

                //make sure that the block offers service it supposed to be providing
                final ServiceDescriptor[] services = dependency.getBlockInfo().getServices();
                if( !BlockUtil.hasMatchingService( services, serviceDescriptor ) )
                {
                    throw new ComponentException( "Dependency " + dependencyName +
                                                  " does not offer service required: " +
                                                  serviceDescriptor );
                }

                componentManager.put( roleEntrys[ i ].getRole(), (Block)dependency.getInstance() );
            }
            catch( final ContainerException ce ) {}
        }

        return componentManager;
    }
}
