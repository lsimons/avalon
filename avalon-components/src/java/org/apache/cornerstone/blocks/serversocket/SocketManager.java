/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.serversocket;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.Composer;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.DefaultComponentManager;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Loggable;
import org.apache.phoenix.Block;
import org.apache.cornerstone.services.SocketServer;
import org.apache.avalon.util.ObjectUtil;
import org.apache.avalon.util.StringUtil;
import org.apache.avalon.util.lang.ThreadManager;

/**
 * This is an implementation example of a socket acceptor. A socket acceptor
 * waits on a defined (in its confs) socket for request. On a request it calls
 * the method parseRequest(Socket s) in the SocketHandler interface. The
 * specific implementation of the SocketHandler is defined again in confs.
 * Definitivly this class listen on the specific port and then call the
 * specific handler to parse the request on that socket. You must start an
 * acceptor for each port or other "request generator" you want to listen to.
 *
 * @author Federico Barbieri <fede@apache.org>
 * @deprecated This class is deprecated in favour of org.apache.cornerstone.blocks.sockets.* and org.apache.cornerstone.blocks.connection.*. This still has bugs with respect to closing connections at shutdown time and it also exhibits scalability problems.
 */
public class SocketManager 
    extends AbstractLoggable
    implements Block, SocketServer, Configurable, Disposable
{
    protected final HashMap              m_listeners         = new HashMap();
    protected final ComponentManager     m_componentManager  = new DefaultComponentManager();
    protected Configuration              m_configuration;
    
    public void configure( final Configuration configuration ) 
    {
        m_configuration = configuration;
    }
    
    public void dispose() 
        throws Exception
    {
        final Iterator openListener = m_listeners.values().iterator();
        
        while( openListener.hasNext() )
        {
            shutdownListener( (SocketServer.Listener)openListener.next() );
        }

        m_listeners.clear();
    }

    public void openListener( final String name, 
                              final String type,
                              final int port,
                              final SocketServer.SocketHandler handler ) 
    {
        openListener( name, type, port, null, handler );
    }

    public void openListener( final String name, 
                              final String type, 
                              final int port, 
                              final InetAddress bind, 
                              final SocketServer.SocketHandler handler ) 
    {
        getLogger().info( "Instantiating Listener " + name + " (" + 
                          type + ") on port " + port);

        try 
        {
            final Configuration[] listeners = 
                m_configuration.getChild("listenerDefinition").getChildren("listener");
                
            for( int i = 0; i < listeners.length; i++ )
            {
                final Configuration element = listeners[ i ];
                final String elementType = element.getAttribute("type");
                        
                if( -1 != elementType.indexOf( type ) ) 
                {
                    final ClassLoader classLoader = getClass().getClassLoader();
                    final String classname = element.getAttribute( "class" );
                    final SocketServer.Listener acceptor = (SocketServer.Listener)
                        ObjectUtil.createObject( classLoader, classname );

                    setupLogger( acceptor );

                    if( acceptor instanceof Composer )
                    {
                        ((Composer)acceptor).compose( m_componentManager );
                    }                    
            
                    if( acceptor instanceof Configurable )
                    {
                        ((Configurable)acceptor).configure( element );
                    }

                    if( acceptor instanceof Initializable )
                    {
                        ((Initializable)acceptor).init();
                    }
                                
                    acceptor.listen( port, handler, bind );
                    ThreadManager.getWorkerPool("default").execute( acceptor );
                    m_listeners.put( name, acceptor );
                    return;
                }
            }
                
            throw new RuntimeException( "Cannot find any listener like " + type );
        } 
        catch( final ConfigurationException ce ) 
        {
            throw new RuntimeException( "Cannot open new listener (" + type + ") on port " + 
                                        port + ": " + ce.getMessage());
        } 
        catch( final InstantiationException ie ) 
        {
            throw new RuntimeException( "Cannot open new listener (" + type + ") on port " + 
                                        port + ": " + ie.getMessage());
        } 
        catch( final IllegalAccessException iae ) 
        {
            throw new RuntimeException( "Cannot open new listener (" + type + ") on port " + 
                                        port + ": " + iae.getMessage());
        } 
        catch( final ClassNotFoundException cnfe ) 
        {
            throw new RuntimeException( "Cannot open new listener (" + type + ") on port " + 
                                        port + ": " + cnfe.getMessage());
        } 
        catch( final Exception e ) 
        {
            throw new RuntimeException( "Cannot open new listener (" + type + ") on port " + 
                                        port + ": " + StringUtil.printStackTrace( e, true ) );
        }
    }

    public void closeListener( final String name ) 
    {
        final SocketServer.Listener listener = (SocketServer.Listener) m_listeners.get( name );
        if( null == listener ) return;
        m_listeners.remove( name );
        shutdownListener( listener );
    }

    protected void shutdownListener( final SocketServer.Listener listener ) 
    {
        try
        {
            if( listener instanceof Disposable )
            {
                ((Disposable)listener).dispose();
            }
        } 
        catch( final Exception e ) {}
    }
}
