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
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.components.application.BlockEntry;
import org.apache.avalon.phoenix.components.listeners.BlockListenerManager;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;

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

    ///Frame in which block executes
    private ApplicationFrame     m_frame;

    ///Listener for when blocks are created
    private BlockListener        m_listener;

    ///Name of application, phase is running in
    private String               m_appName;

    /**
     * The Application which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private Application          m_application;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_appName = (String)context.get( "app.name" );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_application = (Application)componentManager.lookup( Application.ROLE );
        m_frame = (ApplicationFrame)componentManager.lookup( ApplicationFrame.ROLE );
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

        final BlockMetaData metaData = entry.getMetaData();
        try
        {
            //Creation stage
            getLogger().debug( REZ.getString( "startup.notice.create.pre" ) );
            final Block block = createBlock( name, metaData );
            getLogger().debug( REZ.getString( "startup.notice.create.success" ) );

            //Loggable stage
            if( block instanceof Loggable )
            {
                getLogger().debug( REZ.getString( "startup.notice.log.pre" ) );
                ((Loggable)block).setLogger( m_frame.getLogger( name ) );
                getLogger().debug( REZ.getString( "startup.notice.log.success" ) );
            }

            //Contextualize stage
            if( block instanceof Contextualizable )
            {
                getLogger().debug( REZ.getString( "startup.notice.context.pre" ) );
                final BlockContext context = m_frame.createBlockContext( name );
                ((Contextualizable)block).contextualize( context );
                getLogger().debug( REZ.getString( "startup.notice.context.success" ) );
            }

            //Composition stage
            if( block instanceof Composable )
            {
                getLogger().debug( REZ.getString( "startup.notice.compose.pre" ) );
                final ComponentManager componentManager =
                    createComponentManager( name, metaData );
                ((Composable)block).compose( componentManager );
                getLogger().debug( REZ.getString( "startup.notice.compose.success" ) );
            }

            //Configuring stage
            if( block instanceof Configurable )
            {
                getLogger().debug( REZ.getString( "startup.notice.config.pre" ) );
                Configuration configuration = null;
                try
                {
                    configuration = m_frame.getConfiguration( name );
                }
                catch( final ConfigurationException ce )
                {
                    // missing configuration (probably).
                    final String message = REZ.getString( "startup.error.block.noconfiguration", name );
                    throw new ConfigurationException( message, ce );
                }

                ((Configurable)block).configure( configuration );
                getLogger().debug( REZ.getString( "startup.notice.config.success" ) );
            }

            //Initialize stage
            if( block instanceof Initializable )
            {
                getLogger().debug( REZ.getString( "startup.notice.init.pre" ) );
                ((Initializable)block).initialize();
                getLogger().debug( REZ.getString( "startup.notice.init.success" ) );
            }

            //Start stage
            if( block instanceof Startable )
            {
                getLogger().debug( REZ.getString( "startup.notice.start.pre" ) );
                ((Startable)block).start();
                getLogger().debug( REZ.getString( "startup.notice.start.success" ) );
            }

            entry.setState( State.STARTED );
            entry.setBlock( block );

            final BlockEvent event = 
                new BlockEvent( name, entry.getProxy(), metaData.getBlockInfo() );
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
    private ComponentManager createComponentManager( final String name, final BlockMetaData metaData )
        throws Exception
    {
        final DefaultComponentManager componentManager = new DefaultComponentManager();
        final DependencyMetaData[] roles = metaData.getDependencies();

        for( int i = 0; i < roles.length; i++ )
        {
            final DependencyMetaData role = roles[ i ];
            final Block dependency = m_application.getBlock( role.getName() );
            componentManager.put( role.getRole(), dependency );
        }

        return componentManager;
    }
}
