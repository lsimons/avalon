/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.sockets;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentNotAccessibleException;
import org.apache.avalon.ComponentNotFoundException;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.util.ObjectUtil;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.cornerstone.services.sockets.SocketFactory;
import org.apache.cornerstone.services.sockets.SocketManager;
import org.apache.phoenix.Block;

/**
 * Implementation of SocketManager.
 * 
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultSocketManager 
    extends AbstractLoggable
    implements SocketManager, Block, Contextualizable, Configurable, Initializable
{
    protected final HashMap              m_serverSockets   = new HashMap();
    protected final HashMap              m_sockets         = new HashMap();

    protected Context                    m_context;
    protected Configuration              m_configuration;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    /**
     * Configure the SocketManager.
     *
     * @param configuration the Configuration
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }
    
    public void init() 
        throws Exception 
    {
        final Configuration[] serverSockets = 
            m_configuration.getChild( "server-sockets" ).getChildren( "factory" );

        for( int i = 0; i < serverSockets.length; i++ )
        {
            final Configuration element = serverSockets[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );

            setupServerSocketFactory( name, className, element );
        }

        final Configuration[] clientSockets = 
            m_configuration.getChild( "client-sockets" ).getChildren( "factory" );
        
        for( int i = 0; i < clientSockets.length; i++ )
        {
            final Configuration element = clientSockets[ i ];
            final String name = element.getAttribute( "name" );
            final String className = element.getAttribute( "class" );
            
            setupClientSocketFactory( name, className, element );
        }
    }

    protected void setupServerSocketFactory( final String name, 
                                             final String className,
                                             final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );

        if( !(object instanceof ServerSocketFactory) )
        {
            throw new ComponentNotAccessibleException( "Error creating factory " + name + 
                                                       " with class " + className + " as " +
                                                       "it does not implement the correct " + 
                                                       "interface (ServerSocketFactory)" );
        }

        m_serverSockets.put( name, object );
    }

    
    protected void setupClientSocketFactory( final String name, 
                                             final String className,
                                             final Configuration configuration )
        throws Exception
    {
        final Object object = createFactory( name, className, configuration );
        
        if( !(object instanceof SocketFactory) )
        {
            throw new ComponentNotAccessibleException( "Error creating factory " + name + 
                                                       " with class " + className + " as " +
                                                       "it does not implement the correct " + 
                                                       "interface (SocketFactory)" );
        }
        
        m_sockets.put( name, object );
    }
    
    protected Component createFactory( final String name, 
                                       final String className,
                                       final Configuration configuration )
        throws Exception
    {
        Component factory = null;
        
        try
        {
            final ClassLoader classLoader = 
                (ClassLoader)Thread.currentThread().getContextClassLoader();
            factory = (Component)ObjectUtil.createObject( classLoader, className ); 
        }
        catch( final Exception e )
        {
            throw new ComponentNotAccessibleException( "Error creating factory with class " + 
                                                       className, e );
        }
        
        setupLogger( factory );
        
        if( factory instanceof Contextualizable )
        {
            ((Contextualizable)factory).contextualize( m_context );
        }
        
        if( factory instanceof Configurable )
        {
            ((Configurable)factory).configure( configuration );
        }
        
        if( factory instanceof Initializable )
        {
            ((Initializable)factory).init();
        }

        return factory;
    }

    /**
     * Retrieve a server socket factory by name.
     *
     * @param name the name of server socket factory
     * @return the ServerSocketFactory
     * @exception ComponentNotFoundException if server socket factory is not available
     */
    public ServerSocketFactory getServerSocketFactory( String name )
        throws ComponentNotFoundException
    {
        final ServerSocketFactory factory = (ServerSocketFactory)m_serverSockets.get( name );

        if( null != factory )
        {
            return factory;
        }
        else
        {
            throw new ComponentNotFoundException( "Unable to locate server socket factory " +
                                                  "named " + name );
        }
    }
    
    /**
     * Retrieve a client socket factory by name.
     *
     * @param name the name of client socket factory
     * @return the SocketFactory
     * @exception ComponentNotFoundException if socket factory is not available
     */
    public SocketFactory getSocketFactory( final String name )
        throws ComponentNotFoundException
    {
        final SocketFactory factory = (SocketFactory)m_sockets.get( name );
        
        if( null != factory )
        {
            return factory;
        }
        else
        {
            throw new ComponentNotFoundException( "Unable to locate client socket factory " +
                                                  "named " + name );
        }
    }
}
