/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import java.io.File;
import java.net.URL;
import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.excalibur.container.ContainerException;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
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
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.kapi.BlockEntry;
import org.apache.avalon.phoenix.components.kapi.BlockInvocationHandler;
import org.apache.avalon.phoenix.components.listeners.BlockListenerManager;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StartupPhase
    extends AbstractLoggable
    implements BlockVisitor, Contextualizable, Composable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ShutdownPhase.class );

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    ///Frame in which block executes
    private ApplicationFrame     m_frame;

    ///Listener for when blocks are created
    private BlockListener        m_listener;

    ///Name of application, phase is running in
    private String               m_appName;

    /**
     * The container (ie kernel) which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private Container                   m_container;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_appName = (String)context.get( "app.name" );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_container = (Container)componentManager.lookup( Container.ROLE );
        m_frame = (ApplicationFrame)componentManager.lookup( ApplicationFrame.ROLE );
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
        m_listener = (BlockListenerManager)componentManager.lookup( BlockListenerManager.ROLE );
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param name the name of block
     * @param entry the BlockEntry
     * @exception Exception if walking is to be stopped
     */
    public void visitBlock( final String name, final BlockEntry entry )
        throws Exception
    {
        if( State.VOID != entry.getState() ) return;

        if( getLogger().isInfoEnabled() )
        {
            final String message = REZ.getString( "startup.notice.processing.name", name );
            getLogger().info( message );
        }
        
        //TODO: remove this and place in deployer/application
        if( getLogger().isDebugEnabled() )
        {
            final String message = 
                REZ.getString( "startup.notice.processing.classloader", m_frame.getClassLoader() );
            getLogger().debug( message );
        }

        ThreadContext.setThreadContext( m_frame.getThreadContext() );

        try
        {
            //Creation stage
            getLogger().debug( REZ.getString( "startup.notice.create.pre" ) );
            final Object object = createBlock( name, entry.getMetaData() );
            entry.setInstance( object );
            getLogger().debug( REZ.getString( "startup.notice.create.success" ) );

            //Loggable stage
            if( object instanceof Loggable )
            {
                getLogger().debug( REZ.getString( "startup.notice.log.pre" ) );
                ((Loggable)object).setLogger( m_frame.getLogger( name ) );
                getLogger().debug( REZ.getString( "startup.notice.log.success" ) );
            }

            //Contextualize stage
            if( object instanceof Contextualizable )
            {
                getLogger().debug( REZ.getString( "startup.notice.context.pre" ) );
                final BlockContext context = m_frame.createBlockContext( name );
                ((Contextualizable)object).contextualize( context );
                getLogger().debug( REZ.getString( "startup.notice.context.success" ) );
            }

            //Composition stage
            if( object instanceof Composable )
            {
                getLogger().debug( REZ.getString( "startup.notice.compose.pre" ) );
                final ComponentManager componentManager =
                    createComponentManager( name, entry );
                ((Composable)object).compose( componentManager );
                getLogger().debug( REZ.getString( "startup.notice.compose.success" ) );
            }

            //Configuring stage
            if( object instanceof Configurable )
            {
                getLogger().debug( REZ.getString( "startup.notice.config.pre" ) );
                Configuration configuration = null;
                try 
                {
                    configuration = m_repository.getConfiguration( m_appName, name );
                } 
                catch( final ConfigurationException ce ) 
                {
                    // missing configuration (probably).
                    final String message = REZ.getString( "startup.error.block.noconfiguration", name );
                    throw new ConfigurationException( message, ce );
                }

                ((Configurable)object).configure( configuration );
                getLogger().debug( REZ.getString( "startup.notice.config.success" ) );
            }

            //Initialize stage
            if( object instanceof Initializable )
            {
                getLogger().debug( REZ.getString( "startup.notice.init.pre" ) );
                ((Initializable)object).initialize();
                getLogger().debug( REZ.getString( "startup.notice.init.success" ) );
            }

            //Start stage
            if( object instanceof Startable )
            {
                getLogger().debug( REZ.getString( "startup.notice.start.pre" ) );
                ((Startable)object).start();
                getLogger().debug( REZ.getString( "startup.notice.start.success" ) );
            }

            entry.setState( State.STARTED );

            final Block block = (Block)object;
            final BlockInfo blockInfo = entry.getMetaData().getBlockInfo();
            final Class[] interfaces = getServiceClasses( block, blockInfo.getServices() );

            final BlockInvocationHandler invocationHandler = 
                new BlockInvocationHandler( block, interfaces );
            entry.setBlockInvocationHandler( invocationHandler );

            final BlockEvent event = new BlockEvent( name, (Block)invocationHandler.getProxy(), blockInfo );
            m_listener.blockAdded( event );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "startup.error.load.fail", name );
            throw new CascadingException( message, e );
        }
    }

    private Block createBlock( final String name, final BlockMetaData metaData )
        throws Exception
    {
        final ClassLoader classLoader = m_frame.getClassLoader(); 
        //Thread.currentThread().getContextClassLoader();
        final Class clazz = classLoader.loadClass( metaData.getClassname() );
        final Block block = (Block)clazz.newInstance(); 
        getLogger().debug( REZ.getString( "startup.notice.block.created" ) );

        return block;
    }

    /**
     * Build a ComponentManager for a specific Block.
     *
     * @param name the name of the block
     * @param entry the BlockEntry
     * @return the created ComponentManager
     */
    private ComponentManager createComponentManager( final String name, final BlockEntry entry )
        throws Exception
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        final DependencyMetaData[] roles = entry.getMetaData().getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final BlockEntry dependency = (BlockEntry)m_container.getEntry( role.getName() );

            componentManager.put( role.getRole(),
                                  (Block)dependency.getBlockInvocationHandler().getProxy() );
        }

        return componentManager;
    }

    private Class[] getServiceClasses( final Block block, final ServiceDescriptor[] services )
    {
        final Class[] classes = new Class[ services.length + 1 ];
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            try
            {
                classes[ i ] = classLoader.loadClass( services[ i ].getName() );
            }
            catch( final Throwable throwable ) {}
        }

        classes[ services.length ] = Block.class;
        return classes;
    }
}
