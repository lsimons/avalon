/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.manager.altrmi;

import org.apache.avalon.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.avalon.excalibur.instrument.manager.InstrumentManagerClientImpl;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentManagerClient;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentSampleDescriptor;

import org.apache.avalon.framework.activity.Disposable;

import org.apache.commons.altrmi.server.AltrmiServerException;
import org.apache.commons.altrmi.server.PublicationDescription;
import org.apache.commons.altrmi.server.PublicationException;
import org.apache.commons.altrmi.server.impl.AbstractServer;
import org.apache.commons.altrmi.server.impl.socket.CompleteSocketCustomStreamServer;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/03/27 16:01:52 $
 * @since 4.1
 */
public class InstrumentManagerAltrmiServer
    implements Disposable
{
    /** The default port. */
    public static final int DEFAULT_PORT = 15555;
    
    private int m_port;
    private AbstractServer m_server;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    public InstrumentManagerAltrmiServer( DefaultInstrumentManager manager )
        throws AltrmiServerException, PublicationException
    {
        this( manager, DEFAULT_PORT );
    }
    
    public InstrumentManagerAltrmiServer( DefaultInstrumentManager manager, int port )
        throws AltrmiServerException, PublicationException
    {
        m_port = port;
        
        InstrumentManagerClientImpl client = new InstrumentManagerClientImpl( manager );
        
        System.out.println( "Creating CompleteSocketCustomStreamServer..." );
        m_server = new CompleteSocketCustomStreamServer( port );
        
        System.out.println( "Publishing InstrumentManagerClient..." );
        
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
        System.out.println( "Started on port: " + port );
    }
    
    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    public void dispose()
    {
        m_server.stop();
        m_server = null;
    }
}

