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

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.excalibur.altrmi.client.AltrmiHostContext;
import org.apache.excalibur.altrmi.client.AltrmiFactory;
import org.apache.excalibur.altrmi.client.impl.socket.SocketCustomStreamHostContext;
import org.apache.excalibur.altrmi.client.impl.ClientClassAltrmiFactory;
import org.apache.excalibur.altrmi.common.AltrmiConnectionException;
import org.apache.excalibur.altrmi.common.AltrmiInvocationException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.7 $ $Date: 2002/04/03 13:48:48 $
 * @since 4.1
 */
class InstrumentManagerConnection
{
    private final String m_host;
    private final int m_port;
    private boolean m_closed;
    private AltrmiHostContext m_altrmiHostContext;
    private AltrmiFactory m_altrmiFactory;
    private InstrumentManagerClient m_manager;
    
    private final ArrayList m_listeners = new ArrayList();
    private InstrumentManagerConnectionListener[] m_listenerArray = null;
    
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
            
            /*
            System.out.println("Listing Published Objects At Server...");
            String[] listOfPublishedObjectsOnServer = m_altrmiFactory.list();
            for ( int i = 0; i < listOfPublishedObjectsOnServer.length; i++ )
            {
                System.out.println( "..[" + i + "]:" + listOfPublishedObjectsOnServer[i] );
            }
            */
            
            m_manager = (InstrumentManagerClient)m_altrmiFactory.lookup(
                "InstrumentManagerClient" );
            
            m_closed = false;
        }
        
        // Notify the listeners outside of synchronization.
        InstrumentManagerConnectionListener[] listenerArray = getListenerArray();
        for ( int i = 0; i < listenerArray.length; i++ )
        {
            listenerArray[i].opened( this );
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
        
        // Notify the listeners outside of synchronization.
        InstrumentManagerConnectionListener[] listenerArray = getListenerArray();
        for ( int i = 0; i < listenerArray.length; i++ )
        {
            listenerArray[i].closed( this );
        }
    }
    
    /**
     * Called when the connection should be closed and then deleted along with
     *  any frames and resources that are associated with it.
     */
    void delete()
    {
        close();
        
        // Notify the listeners outside of synchronization.
        InstrumentManagerConnectionListener[] listenerArray = getListenerArray();
        for ( int i = 0; i < listenerArray.length; i++ )
        {
            listenerArray[i].deleted( this );
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
    
    /**
     * Adds a InstrumentManagerConnectionListener to the list of listeners
     *  which receive state updates of the connection.
     *
     * @param listener InstrumentManagerConnectionListener to start receiving
     *                 state updates.
     */
    public void addInstrumentManagerConnectionListener(
        InstrumentManagerConnectionListener listener )
    {
        synchronized (this)
        {
            m_listeners.add( listener );
            m_listenerArray = null;
        }
    }
    
    /**
     * Removes a InstrumentManagerConnectionListener from the list of listeners
     *  which receive state updates of the connection.
     *
     * @param listener InstrumentManagerConnectionListener to stop receiving
     *                 state updates.
     */
    public void removeInstrumentManagerConnectionListener(
        InstrumentManagerConnectionListener listener )
    {
        synchronized (this)
        {
            m_listeners.remove( listener );
            m_listenerArray = null;
        }
    }
    
    /**
     * Get a threadsafe array of the current listeners avoiding synchronization
     *  when possible.  The contents of the returned array will never change.
     *
     * @return An array of the currently registered listeners
     */
    private InstrumentManagerConnectionListener[] getListenerArray()
    {
        InstrumentManagerConnectionListener[] listenerArray = m_listenerArray;
        if ( listenerArray == null )
        {
            synchronized(this)
            {
                m_listenerArray = new InstrumentManagerConnectionListener[ m_listeners.size() ];
                m_listeners.toArray( m_listenerArray );
                listenerArray = m_listenerArray;
            }
        }
        return listenerArray;
    }
}

