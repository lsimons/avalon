/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashMap;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/05 02:15:44 $
 * @since 4.1
 */
class InstrumentClientFrame
    extends JFrame
    implements Runnable, InstrumentManagerConnectionListener
{
    private String m_title;

    private JDesktopPane m_desktopPane;
    private MenuBar m_menuBar;

    private File m_desktopFile;
    private File m_desktopFileDir;

    private HashMap m_connections = new HashMap();
    private InstrumentManagerConnection[] m_connectionArray;

    private Thread m_runner;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new InstrumentClient frame.
     *
     * @param title The title for the frame.
     */
    InstrumentClientFrame( String title )
    {
        super();

        m_title = title;

        init();

        m_runner = new Thread( this, "InstrumentClientFrameRunner" );
        m_runner.start();
    }

    /*---------------------------------------------------------------
     * Runnable Methods
     *-------------------------------------------------------------*/
    public void run()
    {
        while( m_runner != null )
        {
            try
            {
                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e )
                {
                    if ( m_runner == null )
                    {
                        return;
                    }
                }

                // Check on the status of all of the connections (Avoid synchronization)
                InstrumentManagerConnection[] connectionArray = m_connectionArray;
                if ( connectionArray == null )
                {
                    connectionArray = updateInstrumentManagerConnectionArray();
                }
                for ( int i = 0; i < connectionArray.length; i++ )
                {
                    InstrumentManagerConnection connection = connectionArray[i];
                    if ( connection.isClosed() )
                    {
                        // Connection is closed, try to open it.
                        connection.tryOpen();
                    }
                    else
                    {
                        // Make sure that the connection is still open
                        connection.ping();
                    }
                }

                // Update each of the ProfileSampleFrames.  This is kind of temporary
                //  to get rid of the one thread per frame issue.
                JInternalFrame[] frames = m_desktopPane.getAllFrames();
                for( int i = 0; i < frames.length; i++ )
                {
                    JInternalFrame frame = frames[ i ];

                    if( frame instanceof InstrumentSampleFrame )
                    {
                        ( (InstrumentSampleFrame)frame ).update();
                    }
                }
            }
            catch( Throwable t )
            {
                // Should not get here, but we want to make sure that this never happens.
                System.out.println( "Unexpected error caught in ProfilerFrame runner:" );
                t.printStackTrace();
            }
        }
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
    }

    /**
     * Called when the connection is closed.  May be called more than once if
     *  the connection to the InstrumentManager is reopened.
     *
     * @param connection Connection which was closed.
     */
    public void closed( InstrumentManagerConnection connection )
    {
    }

    /**
     * Called when the connection is deleted.  All references should be removed.
     *
     * @param connection Connection which was deleted.
     */
    public void deleted( InstrumentManagerConnection connection )
    {
        connection.removeInstrumentManagerConnectionListener( this );
        String key = connection.getHost() + ":" + connection.getPort();
        synchronized (m_connections)
        {
            m_connections.remove( key );
            m_connectionArray = null;
        }
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void init()
    {
        updateTitle();

        getContentPane().setLayout( new BorderLayout() );

        // Create a DesktopPane and place it in a BevelBorder
        m_desktopPane = new DesktopPane();
        JPanel dBorder = new JPanel();
        dBorder.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
        dBorder.setLayout( new BorderLayout() );
        dBorder.add( m_desktopPane, BorderLayout.CENTER );
        getContentPane().add( dBorder, BorderLayout.CENTER );

        // Create a Menu Bar
        m_menuBar = new MenuBar( this );
        setJMenuBar( m_menuBar );

        setLocation( 50, 50 );
        setSize( 640, 480 );
    }

    private void updateTitle()
    {
        if( m_desktopFile == null )
        {
            setTitle( m_title );
        }
        else
        {
            setTitle( m_title + " - " + m_desktopFile.getAbsolutePath() );
        }
    }

    JDesktopPane getDesktopPane()
    {
        return m_desktopPane;
    }

    void closeAllFrames()
    {
        JInternalFrame[] frames = m_desktopPane.getAllFrames();
        for( int i = 0; i < frames.length; i++ )
        {
            frames[ i ].setVisible( false );
            frames[ i ].dispose();
        }
    }

    InstrumentManagerConnection[] getInstrumentManagerConnections()
    {
        // Avoid synchronization when possible.
        InstrumentManagerConnection[] array = m_connectionArray;
        if ( array == null )
        {
            array = updateInstrumentManagerConnectionArray();
        }
        return array;
    }

    private InstrumentManagerConnection[] updateInstrumentManagerConnectionArray()
    {
        synchronized (this)
        {
            InstrumentManagerConnection[] array =
                new InstrumentManagerConnection[ m_connections.size() ];
            m_connections.values().toArray( array );
            m_connectionArray = array;
            return array;
        }
    }

    InstrumentManagerConnection getInstrumentManagerConnection( String host, int port )
    {
        String key = host + ":" + port;
        synchronized (m_connections)
        {
            return (InstrumentManagerConnection)m_connections.get( key );
        }
    }

    void showConnectDialog()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                ConnectDialog dialog = new ConnectDialog( InstrumentClientFrame.this );
                dialog.setHost( "localhost" );
                dialog.setPort( 15555 );
                dialog.show();
                if ( dialog.getAction() == ConnectDialog.BUTTON_OK )
                {
                    openInstrumentManagerConnection( dialog.getHost(), dialog.getPort() );
                }
            }
        } );
    }

    void openInstrumentManagerConnection( final String host, final int port )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                String key = host + ":" + port;
                synchronized (m_connections)
                {
                    InstrumentManagerConnection connection =
                        (InstrumentManagerConnection)m_connections.get( key );
                    if ( connection == null )
                    {
                        connection = new InstrumentManagerConnection( host, port );
                        m_connections.put( key, connection );
                        m_connectionArray = null;

                        connection.addInstrumentManagerConnectionListener(
                            InstrumentClientFrame.this );

                        openInstrumentManagerConnectionFrame( connection );

                        return;
                    }
                }

                // If we get here show an error that the connection alreay exists.
                //  Must be done outside the synchronization block.
                showErrorDialog( "A connection to " + key + " already exists." );
            }
        } );
    }

    void openInstrumentManagerConnectionFrame( final InstrumentManagerConnection connection )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                InstrumentManagerFrame frame =
                    new InstrumentManagerFrame( connection, InstrumentClientFrame.this );

                frame.addToDesktop( m_desktopPane );
                frame.show();
            }
        } );
    }

    void openInstrumentSampleFrame( final InstrumentManagerConnection connection,
                                    final InstrumentSampleDescriptor sampleDescriptor )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                String sampleName = sampleDescriptor.getName();
                InstrumentSampleFrame frame = new InstrumentSampleFrame( connection,
                    sampleName, InstrumentClientFrame.this );

                frame.addToDesktop( m_desktopPane );
                frame.show();
            }
        } );
    }

    private void showErrorDialog( String message )
    {
        JOptionPane.showMessageDialog( this,
                                       "<html><body><font color=\"black\">" + message + "</font>" +
                                       "</body></html>", m_title + " Error",
                                       JOptionPane.ERROR_MESSAGE );
    }

    private void showErrorDialog( String message, Throwable t )
    {
        JOptionPane.showMessageDialog( this,
                                       "<html><body><font color=\"black\">" + message +
                                       "</font><br><br><font color=\"black\">Reason: " +
                                       t.getMessage() + "</font></body></html>",
                                       m_title + " Error", JOptionPane.ERROR_MESSAGE );
    }


    /**
     * Shutdown the InstrumentClient.
     */
    private void shutdown()
    {
        // Stop the runner.
        m_runner.interrupt();
        m_runner = null;

        // Close all connections cleanly.
        InstrumentManagerConnection[] connections = getInstrumentManagerConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i].delete();
        }

        // Kill the JVM.
        System.exit( 1 );
    }

    /*---------------------------------------------------------------
     * Menu Callback Methods
     *-------------------------------------------------------------*/
    /**
     * File-Exit callback.
     */
    void fileExit()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                shutdown();
            }
        } );
    }

    /**
     * Instrument-CreateSample callback.
     */
    void instrumentCreateSample( final InstrumentManagerConnection connection,
                                 final InstrumentDescriptor instrumentDescriptor )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                CreateSampleDialog dialog =
                    new CreateSampleDialog( InstrumentClientFrame.this, instrumentDescriptor );

                dialog.setSampleDescription( "Each Second" );
                dialog.setInterval( 1000 );
                dialog.setSampleCount( 600 );  // 10 minutes of history
                dialog.setLeaseTime( 600 );
                dialog.setMaintainLease( true );
                dialog.show();

                if ( dialog.getAction() == CreateSampleDialog.BUTTON_OK )
                {
                    System.out.println( "New Sample: desc=" + dialog.getSampleDescription() +
                        ", interval=" + dialog.getInterval() + ", size=" + dialog.getSampleCount() +
                        ", lease=" + dialog.getLeaseTime() + ", type=" + dialog.getSampleType() );

                    InstrumentSampleDescriptor sampleDescriptor =
                        instrumentDescriptor.createInstrumentSample(
                            dialog.getSampleDescription(),
                            dialog.getInterval(),
                            dialog.getSampleCount(),
                            dialog.getLeaseTime(),
                            dialog.getSampleType() );

                    // Show a frame for the new sample
                    openInstrumentSampleFrame( connection, sampleDescriptor );
                }
            }
        } );
    }
}

