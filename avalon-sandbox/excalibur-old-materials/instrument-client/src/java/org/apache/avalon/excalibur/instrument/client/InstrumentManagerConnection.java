/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.commons.altrmi.client.AltrmiHostContext;
import org.apache.commons.altrmi.client.AltrmiFactory;
import org.apache.commons.altrmi.client.impl.socket.SocketCustomStreamHostContext;
import org.apache.commons.altrmi.client.impl.ClientClassAltrmiFactory;
import org.apache.commons.altrmi.common.AltrmiConnectionException;
import org.apache.commons.altrmi.common.AltrmiInvocationException;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/03/30 01:30:49 $
 * @since 4.1
 */
class InstrumentManagerConnection
{
    private String m_host;
    private int m_port;
    private boolean m_closed;
    private AltrmiHostContext m_altrmiHostContext;
    private AltrmiFactory m_altrmiFactory;
    private InstrumentManagerClient m_manager;
    
    private ArrayList m_listeners = new ArrayList();
    private InstrumentManagerConnectionListener[] m_listenerArray;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentManagerConnection( String host, int port )
    {
        m_host = host;
        m_port = port;
        m_closed = true;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    public String getHost()
    {
        return m_host;
    }
    
    public int getPort()
    {
        return m_port;
    }
    
    /**
     * Returns a title for the connection which can be used in frame titlesa
     *  and menus.  Reflects the connected status.
     */
    public String getTitle()
    {
        String description;
        synchronized(this)
        {
            if ( m_manager == null )
            {
                description = "[Not Connected]";
            }
            else
            {
                description = m_manager.getDescription();
            }
        }
        
        return description + " (" + m_host + ":" + m_port + ")";
    }
    
    public InstrumentManagerClient getInstrumentManagerClient()
    {
        return m_manager;
    }
    
    public void open() throws AltrmiConnectionException, IOException
    {
        synchronized (this)
        {
            m_altrmiHostContext = new SocketCustomStreamHostContext( m_host, m_port );
            m_altrmiFactory = new ClientClassAltrmiFactory( false );
            m_altrmiFactory.setHostContext( m_altrmiHostContext );
            
            System.out.println("Listing Published Objects At Server...");
            String[] listOfPublishedObjectsOnServer = m_altrmiFactory.list();
            for (int i = 0; i < listOfPublishedObjectsOnServer.length; i++) {
                System.out.println("..[" + i + "]:" + listOfPublishedObjectsOnServer[i]);
            }
            
            m_manager = (InstrumentManagerClient)m_altrmiFactory.lookup(
                "InstrumentManagerClient" );
            
            m_closed = false;
        }
        
        // Notify the listeners.
        InstrumentManagerConnectionListener[] listeners = m_listenerArray;
        if ( listeners == null )
        {
            listeners = updateListenerArray();
        }
        
        for ( int i = 0; i < listeners.length; i++ )
        {
            listeners[i].opened( this );
        }
    }
    
    /**
     * Attempts to open the connection.  If it fails, it just leaves it closed.
     */
    public void tryOpen()
    {
        try
        {
            open();
        }
        catch ( AltrmiConnectionException e )
        {
        }
        catch ( IOException e )
        {
        }
    }
    
    /**
     * Returns true if the connection is currently closed.
     *
     * @return True if the connection is currently closed.
     */
    public boolean isClosed()
    {
        return m_closed;
    }
    
    /**
     * Closes the connection, but keeps it around.  If the remote instrument manager
     *  is running the connection will reopen itself.
     */
    public void close()
    {
        System.out.println("InstrumentManagerConnection.close()");
        synchronized (this)
        {
            if ( !m_closed )
            {
                m_closed = true;
                m_manager = null;
                m_altrmiFactory.close();
                m_altrmiFactory = null;
                // Uncomment this when it gets implemented.
                // m_altrmiHostContext.close();
                m_altrmiHostContext = null;
            }
        }
        
        // Notify the listeners.
        InstrumentManagerConnectionListener[] listeners = m_listenerArray;
        if ( listeners == null )
        {
            listeners = updateListenerArray();
        }
        
        for ( int i = 0; i < listeners.length; i++ )
        {
            listeners[i].closed( this );
        }
    }
    
    /**
     * Called when the connection should be closed and then deleted along with
     *  any frames and resources that are associated with it.
     */
    void delete()
    {
        close();
        
        // Notify the listeners.
        InstrumentManagerConnectionListener[] listeners = m_listenerArray;
        if ( listeners == null )
        {
            listeners = updateListenerArray();
        }
        
        for ( int i = 0; i < listeners.length; i++ )
        {
            listeners[i].deleted( this );
        }
    }
        
    /**
     * Updates the cached array of listeners so that it can be used without synchronization.
     *
     * @returns An array of listeners.  Will never be null and is thread safe.
     */
    private InstrumentManagerConnectionListener[] updateListenerArray()
    {
        synchronized(this)
        {
            m_listenerArray = new InstrumentManagerConnectionListener[ m_listeners.size() ];
            m_listeners.toArray( m_listenerArray );
            return m_listenerArray;
        }
    }
    
    public void ping()
    {
        synchronized(this)
        {
            // Ping the server by requesting the manager's name
            if ( m_manager != null )
            {
                try
                {
                    String name = m_manager.getName();
                }
                catch ( AltrmiInvocationException e )
                {
                    System.out.println("Ping Failed.");
                    e.printStackTrace();
                    // Socket was closed.
                    close();
                }
            }
        }
    }
    
    public void addInstrumentManagerConnectionListener( InstrumentManagerConnectionListener listener )
    {
        synchronized (this)
        {
            m_listeners.add( listener );
            m_listenerArray = null;
        }
    }
    
    public void removeInstrumentManagerConnectionListener( InstrumentManagerConnectionListener listener )
    {
        synchronized (this)
        {
            m_listeners.remove( listener );
            m_listenerArray = null;
        }
    }
}

