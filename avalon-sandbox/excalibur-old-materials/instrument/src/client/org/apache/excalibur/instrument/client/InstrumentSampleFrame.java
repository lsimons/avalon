/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameEvent;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentableException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentException;
import org.apache.excalibur.instrument.manager.interfaces.NoSuchInstrumentSampleException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.altrmi.common.AltrmiInvocationException;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/07/29 16:05:19 $
 * @since 4.1
 */
class InstrumentSampleFrame
    extends AbstractInternalFrame
    implements InstrumentManagerConnectionListener
{
    public static final String FRAME_TYPE = "sample-frame";

    private InstrumentManagerConnection m_connection;
    private InstrumentableDescriptor m_instrumentableDescriptor;
    private InstrumentDescriptor m_instrumentDescriptor;
    private InstrumentSampleDescriptor m_instrumentSampleDescriptor;
    private String m_instrumentSampleName;
    private LineChart m_lineChart;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentSampleFrame( Configuration stateConfig,
                           InstrumentClientFrame frame )
        throws ConfigurationException
    {
        super( stateConfig, true, true, true, true, frame );

        String host = stateConfig.getAttribute( "host" );
        int port = stateConfig.getAttributeAsInteger( "port" );
        m_instrumentSampleName = stateConfig.getAttribute( "sample" );

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

    InstrumentSampleFrame( InstrumentManagerConnection connection,
                           String sampleName,
                           InstrumentClientFrame frame )
    {
        super( "", true, true, true, true, frame );

        m_connection = connection;
        m_instrumentSampleName = sampleName;

        connection.addInstrumentManagerConnectionListener( this );

        init();

        setSize( new Dimension( 600, 120 ) );
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
        stateConfig.setAttribute( "sample", m_instrumentSampleDescriptor.getName() );
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
    /**
     * Sets the title of the frame.
     * <p>
     * Only called when synchronized.
     */
    private void setTitle()
    {
        String title;
        InstrumentManagerClient manager = m_connection.getInstrumentManagerClient();
        if ( manager == null )
        {
            title = m_connection.getTitle() + " : " + m_instrumentSampleName;
        }
        else
        {
            title = m_connection.getTitle() + " : " +
                m_instrumentableDescriptor.getDescription() + " : " +
                m_instrumentDescriptor.getDescription() + " : " +
                m_instrumentSampleDescriptor.getDescription();
        }

        setTitle( title );
    }

    private void init()
    {
        synchronized (this)
        {
            // Clean out the content pane
            getContentPane().removeAll();

            InstrumentManagerClient manager = m_connection.getInstrumentManagerClient();
            if ( manager == null )
            {
                // Not connected.
                m_instrumentableDescriptor = null;
                m_instrumentDescriptor = null;
                m_instrumentSampleDescriptor = null;

                JLabel label = new JLabel( "Not Connected" );
                label.setForeground( Color.red );
                label.setHorizontalAlignment( SwingConstants.CENTER );
                label.setVerticalAlignment( SwingConstants.CENTER );

                getContentPane().add( label );
            }
            else
            {
                try
                {
                    // Look up the descriptors.
                    try
                    {
                        m_instrumentableDescriptor =
                            manager.getInstrumentableDescriptor( m_instrumentSampleName );
                    }
                    catch ( NoSuchInstrumentableException e )
                    {
                        // Frame no longer valid.
                        hideFrame();
                        return;
                    }
                    try
                    {
                        m_instrumentDescriptor = m_instrumentableDescriptor.
                            getInstrumentDescriptor( m_instrumentSampleName );
                    }
                    catch ( NoSuchInstrumentException e )
                    {
                        // Frame no longer valid.
                        hideFrame();
                        return;
                    }
                    try
                    {
                        m_instrumentSampleDescriptor = m_instrumentDescriptor.
                            getInstrumentSampleDescriptor( m_instrumentSampleName );
                    }
                    catch ( NoSuchInstrumentSampleException e )
                    {
                        // Frame no longer valid.
                        hideFrame();
                        return;
                    }

                    // Decide on a line interval based on the interval of the sample.
                    long interval = m_instrumentSampleDescriptor.getInterval();
                    int hInterval;
                    String format;
                    String detailFormat;
                    if( interval < 1000 )
                    {
                        // Once per 10 seconds.
                        hInterval = (int)( 10000 / interval );
                        format = "{2}:{3}:{4}";
                        detailFormat = "{0}/{1} {2}:{3}:{4}.{5}";
                    }
                    else if( interval < 60000 )
                    {
                        // Once per minute.
                        hInterval = (int)( 60000 / interval );
                        format = "{2}:{3}:{4}";
                        detailFormat = "{0}/{1} {2}:{3}:{4}";
                    }
                    else if( interval < 600000 )
                    {
                        // Once per 10 minutes
                        hInterval = (int)( 600000 / interval );
                        format = "{0}/{1} {2}:{3}";
                        detailFormat = "{0}/{1} {2}:{3}";
                    }
                    else if( interval < 3600000 )
                    {
                        // Once per hour.
                        hInterval = (int)( 3600000 / interval );
                        format = "{0}/{1} {2}:{3}";
                        detailFormat = "{0}/{1} {2}:{3}";
                    }
                    else if( interval < 86400000 )
                    {
                        // Once per day.
                        hInterval = (int)( 86400000 / interval );
                        format = "{0}/{1}";
                        detailFormat = "{0}/{1} {2}:{3}";
                    }
                    else
                    {
                        // Default to every 10 points.
                        hInterval = 10;
                        format = "{0}/{1} {2}:{3}";
                        detailFormat = "{0}/{1} {2}:{3}";
                    }

                    m_lineChart = new LineChart( hInterval,
                        m_instrumentSampleDescriptor.getInterval(), format, detailFormat, 20 );

                    getContentPane().add( m_lineChart );
                }
                catch ( AltrmiInvocationException e )
                {
                    // Server went away, close the connection
                    m_connection.close();
                }
            }

            // Set the title last so that the descriptors can be reloaded if necessary.
            setTitle();
        }

        update();
    }

    void update()
    {
        synchronized (this)
        {
            if (  m_instrumentSampleDescriptor != null )
            {
                try
                {
                    InstrumentSampleSnapshot snapshot = m_instrumentSampleDescriptor.getSnapshot();

                    int[] samples = snapshot.getSamples();

                    m_lineChart.setValues( samples, snapshot.getTime() );
                }
                catch ( AltrmiInvocationException e )
                {
                    // Server went away, close the connection
                    m_connection.close();
                }
            }
        }
    }
}

