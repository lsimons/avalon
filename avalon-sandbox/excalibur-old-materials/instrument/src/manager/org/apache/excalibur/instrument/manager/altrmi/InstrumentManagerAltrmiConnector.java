/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager.altrmi;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentManagerClientLocalImpl;
import org.apache.excalibur.instrument.manager.InstrumentManagerConnector;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

import org.apache.excalibur.altrmi.server.AltrmiServerException;
import org.apache.excalibur.altrmi.server.PublicationDescription;
import org.apache.excalibur.altrmi.server.PublicationException;
import org.apache.excalibur.altrmi.server.impl.AbstractServer;
import org.apache.excalibur.altrmi.server.impl.socket.CompleteSocketCustomStreamServer;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/04 10:33:33 $
 * @since 4.1
 */
public class InstrumentManagerAltrmiConnector
    implements InstrumentManagerConnector
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15555;
    
    private DefaultInstrumentManager m_manager;
    private int m_port;
    private AbstractServer m_server;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentManagerAltrmiConnector.
     */
    public InstrumentManagerAltrmiConnector()
    {
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerConnector Methods
     *-------------------------------------------------------------*/
    /**
     * Set the InstrumentManager to which the Connecter will provide
     *  access.  This method is called before the new connector is
     *  configured or started.
     */
    public void setInstrumentManager( DefaultInstrumentManager manager )
    {
        m_manager = manager;
    }
    
    /*---------------------------------------------------------------
     * Configurable Methods
     *-------------------------------------------------------------*/
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getAttributeAsInteger( "port", DEFAULT_PORT );
    }
    
    /*---------------------------------------------------------------
     * Startable Methods
     *-------------------------------------------------------------*/
    public void start()
        throws Exception
    {
        InstrumentManagerClientLocalImpl client = new InstrumentManagerClientLocalImpl( m_manager );
        
        // Create the socket server
        m_server = new CompleteSocketCustomStreamServer( m_port );
        
        Class[] additionalFacadeClasses = new Class[]
        {
            InstrumentableDescriptor.class,
            InstrumentDescriptor.class,
            InstrumentSampleDescriptor.class
        };
        
        m_server.publish( client, "InstrumentManagerClient", 
            new PublicationDescription( InstrumentManagerClient.class, additionalFacadeClasses ) );
        
        System.out.println( "Starting CompleteSocketObjectStreamServer..." );
        m_server.start();
        System.out.println( "Started on port: " + m_port );
    }
    
    public void stop()
        throws Exception
    {
        m_server.stop();
        m_server = null;
    }
}

