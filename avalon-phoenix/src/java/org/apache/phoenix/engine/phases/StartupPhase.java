/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import java.io.File;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.DefaultContext;
import org.apache.avalon.Initializable;
import org.apache.avalon.Loggable;
import org.apache.avalon.Startable;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.camelot.Factory;
import org.apache.avalon.camelot.SimpleFactory;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.util.thread.ThreadContext;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.phoenix.Block;
import org.apache.phoenix.BlockContext;
import org.apache.phoenix.engine.SarContextResources;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.blocks.BlockVisitor;
import org.apache.phoenix.engine.blocks.DefaultBlockContext;
import org.apache.phoenix.engine.facilities.ComponentManagerBuilder;
import org.apache.phoenix.engine.facilities.ConfigurationRepository;
import org.apache.phoenix.engine.facilities.LoggerBuilder;
import org.apache.phoenix.metainfo.BlockUtil;
import org.apache.phoenix.metainfo.ServiceDescriptor;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StartupPhase
    extends AbstractLoggable
    implements BlockVisitor, Contextualizable, Composer
{
    private ClassLoader                 m_classLoader;
    private LoggerBuilder               m_loggerBuilder;
    private ComponentManagerBuilder     m_componentManagerBuilder;
    private ConfigurationRepository     m_repository;
    private ThreadManager               m_threadManager;

    ///Factory used to build instance of Block
    private Factory                  m_factory;

    ///base context used to setup hosted blocks
    private DefaultContext           m_baseBlockContext;

    public void contextualize( final Context context )
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
        throws ComponentManagerException
    {
        m_classLoader = (ClassLoader)componentManager.lookup( "java.lang.ClassLoader" );

        m_factory = new SimpleFactory( m_classLoader );

        m_threadManager = (ThreadManager)componentManager.
            lookup( "org.apache.avalon.util.thread.ThreadManager" );

        m_loggerBuilder = (LoggerBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.LoggerBuilder" );

        m_componentManagerBuilder = (ComponentManagerBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ComponentManagerBuilder" );

        m_repository = (ConfigurationRepository)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ConfigurationRepository" );
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
        if( entry.getState() != Phase.BASE &&
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
                ((Loggable)object).setLogger( m_loggerBuilder.createLogger( name, entry ) );
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
            if( object instanceof Composer )
            {
                getLogger().debug( "Pre-Composition Stage" );
                final ComponentManager componentManager =
                    m_componentManagerBuilder.createComponentManager( name, entry );
                ((Composer)object).compose( componentManager );
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
                ((Initializable)object).init();
                getLogger().debug( "Initializable successful." );
            }

            //Start stage
            if( object instanceof Startable )
            {
                getLogger().debug( "Pre-Start Stage" );
                ((Startable)object).start();
                getLogger().debug( "Start successful." );
            }

            entry.setState( Phase.STARTEDUP );

            getLogger().info( "Ran Startup Phase for " + name );
        }
        catch( final Exception e )
        {
            throw new ApplicationException( "Failed to load block " + name, e );
        }
    }

    private Object createBlock( final String name, final BlockEntry entry )
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
}
