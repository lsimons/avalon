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
 * @version CVS $Revision: 1.3 $ $Date: 2002/03/28 04:06:18 $
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
            
            // Notify the listeners.
            for ( Iterator iter = m_listeners.iterator(); iter.hasNext(); )
            {
                ((InstrumentManagerConnectionListener)iter.next()).opened( m_host, m_port);
            }
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
    
    public boolean isClosed()
    {
        return m_closed;
    }
    
    public void close()
    {
        synchronized (this)
        {
            if ( !m_closed )
            {
                m_closed = true;
                m_manager = null;
                m_altrmiFactory.close();
                m_altrmiFactory = null;
                m_altrmiHostContext = null;
                
                // Notify the listeners.
                for ( Iterator iter = m_listeners.iterator(); iter.hasNext(); )
                {
                    ((InstrumentManagerConnectionListener)iter.next()).closed( m_host, m_port);
                }
            }
        }
    }
    
    public void dispose()
    {
        synchronized (this)
        {
            if ( !isClosed() )
            {
                close();
            }
            
            // Notify the listeners.
            for ( Iterator iter = m_listeners.iterator(); iter.hasNext(); )
            {
                ((InstrumentManagerConnectionListener)iter.next()).disposed( m_host, m_port);
            }
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
        }
    }
    
    public void removeInstrumentManagerConnectionListener( InstrumentManagerConnectionListener listener )
    {
        synchronized (this)
        {
            m_listeners.remove( listener );
        }
    }
}

