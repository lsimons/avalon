/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

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

import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/03/28 04:06:18 $
 * @since 4.1
 */
class InstrumentClientFrame
    extends JFrame
    implements Runnable
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
    
    void openInstrumentManagerConnection( String host, int port )
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
                
                openInstrumentManagerConnectionFrame( connection );
                
                return;
            }
        }
        
        // If we get here show an error that the connection alreay exists.
        //  Must be done outside the synchronization block.
        showErrorDialog( "A connection to " + key + " already exists." );
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
                                    final InstrumentableDescriptor instrumentableDescriptor,
                                    final InstrumentDescriptor instrumentDescriptor,
                                    final InstrumentSampleDescriptor instrumentSampleDescriptor )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                String sampleName = instrumentSampleDescriptor.getName();
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
}

