/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.phases;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Loggable;
import org.apache.avalon.Startable;
import org.apache.avalon.atlantis.ApplicationException;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.util.thread.ThreadManager;
import org.apache.avalon.util.thread.ThreadContext;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.facilities.ComponentBuilder;
import org.apache.phoenix.engine.facilities.ComponentManagerBuilder;
import org.apache.phoenix.engine.facilities.ConfigurationRepository;
import org.apache.phoenix.engine.facilities.ContextBuilder;
import org.apache.phoenix.engine.facilities.LoggerBuilder;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StartupPhase
    extends AbstractLoggable
    implements Phase, Composer
{
    private ClassLoader                 m_classLoader;
    private ComponentBuilder            m_componentBuilder;
    private LoggerBuilder               m_loggerBuilder;
    private ContextBuilder              m_contextBuilder;
    private ComponentManagerBuilder     m_componentManagerBuilder;
    private ConfigurationRepository     m_repository;
    private ThreadManager               m_threadManager;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_classLoader = (ClassLoader)componentManager.lookup( "java.lang.ClassLoader" );

        m_threadManager = (ThreadManager)componentManager.
            lookup( "org.apache.avalon.util.thread.ThreadManager" );

        m_componentBuilder = (ComponentBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ComponentBuilder" );

        m_loggerBuilder = (LoggerBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.LoggerBuilder" );

        m_contextBuilder = (ContextBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ContextBuilder" );

        m_componentManagerBuilder = (ComponentManagerBuilder)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ComponentManagerBuilder" );

        m_repository = (ConfigurationRepository)componentManager.
            lookup( "org.apache.phoenix.engine.facilities.ConfigurationRepository" );
    }

    /**
     * Retrieve traversal that should be taken.
     *
     * @return the Traversal
     */
    public Traversal getTraversal()
    {
        return Phase.FORWARD;
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
            final Object object = m_componentBuilder.createComponent( name, entry );
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
                ((Contextualizable)object).contextualize( m_contextBuilder.createContext( name, entry ) );
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
}
