/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import java.io.File;
import java.net.URL;
import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
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
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.avalon.phoenix.components.application.BlockEntry;
import org.apache.avalon.phoenix.components.frame.ApplicationFrame;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metadata.DependencyMetaData;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class StartupPhase
    extends AbstractLoggable
    implements BlockVisitor
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( StartupPhase.class );

    ///Frame in which block executes
    private ApplicationFrame     m_frame;

    /**
     * The Application which this phase is associated with.
     * Required to build a ComponentManager.
     */
    private Application          m_application;

    protected StartupPhase( final Application application, 
                            final ApplicationFrame frame )
    {
        m_application = application;
        m_frame = frame;
    }

    /**
     * This is called when a block is reached whilst walking the tree.
     *
     * @param entry the BlockEntry
     * @exception Exception if walking is to be stopped
     */
    public void visitBlock( final BlockEntry entry )
        throws Exception
    {
        if( State.VOID != entry.getState() ) return;

        final BlockMetaData metaData = entry.getMetaData();
        final String name = metaData.getName();

        //The number of stage currently at
        //(Used in constructing error messages) 
        int stage = 0;
        
        try
        {
            //Creation stage
            stage = 0;
            notice( name, stage );
            final Block block = createBlock( name, metaData );

            //Loggable stage
            stage = 1;
            if( block instanceof Loggable )
            {
                notice( name, stage );
                ((Loggable)block).setLogger( m_frame.getLogger( name ) );
            }

            //Contextualize stage
            stage = 2;
            if( block instanceof Contextualizable )
            {
                notice( name, stage );
                final BlockContext context = m_frame.createBlockContext( name );
                ((Contextualizable)block).contextualize( context );
            }

            //Composition stage
            stage = 3;
            if( block instanceof Composable )
            {
                notice( name, stage );
                final ComponentManager componentManager =
                    createComponentManager( name, metaData );
                ((Composable)block).compose( componentManager );
            }

            //Configuring stage
            stage = 4;
            if( block instanceof Configurable )
            {
                notice( name, stage );
                final Configuration configuration = getConfiguration( name );
                ((Configurable)block).configure( configuration );
            }

            //Initialize stage
            stage = 5;
            if( block instanceof Initializable )
            {
                notice( name, stage );
                ((Initializable)block).initialize();
            }

            //Start stage
            stage = 6;
            if( block instanceof Startable )
            {
                notice( name, stage );
                ((Startable)block).start();
            }

            entry.setState( State.STARTED );
            entry.setBlock( block );

            final BlockEvent event =
                new BlockEvent( name, entry.getProxy(), metaData.getBlockInfo() );
            m_frame.blockAdded( event );
        }
        catch( final Throwable t )
        {
            entry.setState( State.FAILED );
            fail( name, stage, t );
        }
    }

    private Block createBlock( final String name, final BlockMetaData metaData )
        throws Exception
    {
        final ClassLoader classLoader = m_frame.getClassLoader();
        //Thread.currentThread().getContextClassLoader();
        final Class clazz = classLoader.loadClass( metaData.getClassname() );
        final Block block = (Block)clazz.newInstance();

        return block;
    }

    private Configuration getConfiguration( final String name )
        throws ConfigurationException
    {
        try
        {
            return m_frame.getConfiguration( name );
        }
        catch( final ConfigurationException ce )
        {
            final String message = REZ.getString( "missing-block-configuration", name );
            throw new ConfigurationException( message, ce );
        }
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

    private void notice( final String name, final int stage )
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message = 
                REZ.getString( "lifecycle-stage.notice", name, new Integer( stage ) );
            getLogger().debug( message );
        }
    }

    private void fail( final String name, final int stage, final Throwable t )
        throws Exception
    {
        final String reason = t.getMessage();
        final String message = 
            REZ.getString( "lifecycle-fail.error", name, new Integer( stage ), reason );
        getLogger().error( message );
        throw new CascadingException( message, t );
    }
}
