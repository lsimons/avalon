/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.Dimension;

import javax.swing.event.InternalFrameEvent;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:19 $
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
     * @param connection Connection which was opened.
     */
    public void opened( InstrumentManagerConnection connection )
    {
        // Status changed, so reinitialize the frame.
        init();
    }

    /**
     * Called when the connection is closed.  May be called more than once if
     *  the connection to the InstrumentManager is reopened.
     *
     * @param connection Connection which was closed.
     */
    public void closed( InstrumentManagerConnection connection )
    {
        // Status changed, so reinitialize the frame.
        init();
    }

    /**
     * Called when the connection is deleted.  All references should be removed.
     *
     * @param connection Connection which was deleted.
     */
    public void deleted( InstrumentManagerConnection connection )
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

