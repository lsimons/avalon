/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.awt.Dimension;

import javax.swing.event.InternalFrameEvent;

import org.apache.avalon.excalibur.instrument.manager.client.InstrumentManagerClient;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:32:24 $
 * @since 4.1
 */
class InstrumentManagerFrame
    extends AbstractInternalFrame
    implements InstrumentManagerConnectionListener
{
    public static final String FRAME_TYPE = "instrument-manager-frame";
    
    private InstrumentManagerConnection m_connection;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentManagerFrame( Configuration stateConfig,
                            InstrumentClientFrame frame )
        throws ConfigurationException
    {
        super( stateConfig, true, true, true, true, frame );
        
        String host = stateConfig.getAttribute( "host" );
        int port = stateConfig.getAttributeAsInteger( "port" );
        
        // Obtain the specified connection.  It should have already been created from the state.
        m_connection = getFrame().getInstrumentManagerConnection( host, port );
        if ( m_connection == null )
        {
            throw new ConfigurationException( 
                "Could not locate an Instrument Manager Connection at " + host + ":" + port );
        }
        
        m_connection.addInstrumentManagerConnectionListener( this );
        
        init();
    }

    InstrumentManagerFrame( InstrumentManagerConnection connection,
                            InstrumentClientFrame frame )
    {
        super( "", true, true, true, true, frame );
        
        m_connection = connection;
        
        connection.addInstrumentManagerConnectionListener( this );
        
        init();

        setSize( new Dimension( 400, 400 ) );
    }

    /*---------------------------------------------------------------
     * AbstractInternalFrame Methods
     *-------------------------------------------------------------*/
    /**
     * Allows subclasses to fill in configuration information.  At the least, they must set
     *  a type attribute.
     */
    protected void getState( DefaultConfiguration stateConfig )
    {
        stateConfig.setAttribute( "type", FRAME_TYPE );
        stateConfig.setAttribute( "host", m_connection.getHost() );
        stateConfig.setAttribute( "port", Integer.toString( m_connection.getPort() ) );
    }

    /*---------------------------------------------------------------
     * InternalFrameListener Methods
     *-------------------------------------------------------------*/
    public void internalFrameClosed( InternalFrameEvent event )
    {
        m_connection.removeInstrumentManagerConnectionListener( this );

        super.internalFrameClosed( event );
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerConnectionListener Methods
     *-------------------------------------------------------------*/
    /**
     * Called when the connection is opened.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    public void opened( String host, int port )
    {
        // Status changed, so reinitialize the frame.
        init();
    }
    
    /**
     * Called when the connection is closed.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    public void closed( String host, int port )
    {
        // Status changed, so reinitialize the frame.
        init();
    }
    
    /**
     * Called when the connection is disposed.  All references should be removed.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    public void disposed( String host, int port )
    {
        hideFrame();
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void setTitle()
    {
        setTitle( m_connection.getTitle() );
    }

    private void init()
    {
        setTitle();

        //getContentPane().add( m_lineChart );
    }
}

