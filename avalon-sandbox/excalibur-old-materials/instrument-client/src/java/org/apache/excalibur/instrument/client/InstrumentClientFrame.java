/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.12 $ $Date: 2003/03/22 12:46:36 $
 * @since 4.1
 */
class InstrumentClientFrame
    extends JFrame
    implements Runnable, InstrumentManagerConnectionListener, LogEnabled
{
    static final String SHUTDOWN_HOOK_NAME = "InstrumentClientShutdownHook";
    
    
    private String m_title;
    
    private JTabbedPane m_connectionsPane;
    private JDesktopPane m_desktopPane;
    private JSplitPane m_splitPane;
    private MenuBar m_menuBar;
    private StatusBar m_statusBar;

    private File m_desktopFile;
    private File m_desktopFileDir;
    
    private HashMap m_connections = new HashMap();
    private InstrumentManagerConnection[] m_connectionArray;

    
    /** Shutdown hook */
    private Thread m_hook;
    
    private Thread m_runner;
    private Logger m_logger;
    
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
        
        ClassLoader cl = InstrumentManagerTreeCellRenderer.class.getClassLoader();
        setIconImage( new ImageIcon( cl.getResource(
            NodeData.MEDIA_PATH + "client.gif") ).getImage() );
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
                InstrumentManagerConnection[] connectionArray = getInstrumentManagerConnections();
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
                        if ( connection.ping() )
                        {
                            // Still connected
                            connection.handleLeasedSamples();
                        }
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
                getLogger().error( "Unexpected error caught in InstrumentClientFrame runner:", t );
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
        // Remove the tab
        int tabIndex = m_connectionsPane.indexOfComponent( connection );
        if ( tabIndex >= 0 )
        {
            m_connectionsPane.setTitleAt( tabIndex, connection.getTabTitle() );
            m_connectionsPane.setToolTipTextAt( tabIndex, connection.getTitle() );
        }
    }
    
    /**
     * Called when the connection is closed.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param connection Connection which was closed.
     */
    public void closed( InstrumentManagerConnection connection )
    {
        // Remove the tab
        int tabIndex = m_connectionsPane.indexOfComponent( connection );
        if ( tabIndex >= 0 )
        {
            m_connectionsPane.setTitleAt( tabIndex, connection.getTabTitle() );
            m_connectionsPane.setToolTipTextAt( tabIndex, connection.getTitle() );
        }
    }
    
    /**
     * Called when the connection is deleted.  All references should be removed.
     *
     * @param connection Connection which was deleted.
     */
    public void deleted( InstrumentManagerConnection connection )
    {
        // Remove the tab
        int tabIndex = m_connectionsPane.indexOfComponent( connection );
        if ( tabIndex >= 0 )
        {
            // Doing this within the shutdown hook causes a deadlock. Java bug?
            if ( Thread.currentThread() != m_hook )
            {
                m_connectionsPane.remove( connection );
            }
        }
        
        connection.removeInstrumentManagerConnectionListener( this );
        String key = connection.getHost() + ":" + connection.getPort();
        synchronized (m_connections)
        {
            m_connections.remove( key );
            m_connectionArray = null;
        }
    }
    
    /*---------------------------------------------------------------
     * LogEnabled Methods
     *-------------------------------------------------------------*/
    public void enableLogging( Logger logger )
    {
        m_logger = logger;
    }
    
    Logger getLogger()
    {
        return m_logger;
    }
    
    /*---------------------------------------------------------------
     * State Methods
     *-------------------------------------------------------------*/
    /**
     * Stores the default state file name and attempts to load it if it exists.
     *  Should onl be called at startup.
     *
     * @param defaultStateFile The default statefile which will be loaded on
     *        startup.
     */
    void setDefaultStateFile( File defaultStateFile )
    {
        // See if the directory containing th defaultStateFile exists.  If so set it.
        File defaultStateFileDir = defaultStateFile.getParentFile();
        if ( defaultStateFileDir.exists() )
        {
            m_desktopFileDir = defaultStateFileDir;
        }
        if ( defaultStateFile.exists() )
        {
            try
            {
                m_desktopFile = null;
                loadStateFromFile( defaultStateFile, true );
                m_desktopFile = defaultStateFile;
            }
            catch( Exception e )
            {
                showErrorDialog( "Unable to load desktop file.", e );
            }
            updateTitle();
        }
    }
    
    /**
     * Loads the Instrument Client state from the specified file.
     *
     * @param stateFile File to read the client's state from.
     * @param showErrorDialog Controls whether an error dialog should be
     *                        displayed or not.
     *
     * @throws Exception if there are any problems loading the state.
     */
    void loadStateFromFile( File stateFile, boolean showErrorDialog ) throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Loading Instrument Client state from: " +
            stateFile.getAbsolutePath() );

        FileInputStream is = new FileInputStream( stateFile );
        try
        {
            loadStateFromStream( is, showErrorDialog );
        }
        finally
        {
            is.close();
        }

        getLogger().debug( "Loading Instrument Client state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }

    /**
     * Loads the Instrument Client state from the specified stream.
     *
     * @param is Stream to read the instrument client's state from.
     * @param showErrorDialog Controls whether an error dialog should be
     *                        displayed or not.
     *
     * @throws Exception if there are any problems loading the state.
     */
    void loadStateFromStream( InputStream is, boolean showErrorDialog ) throws Exception
    {
        // Ride on top of the Configuration classes to load the state.
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration stateConfig = builder.build( is );

        loadStateFromConfiguration( stateConfig, showErrorDialog );
    }

    /**
     * Loads the Instrument Client state from the specified Configuration.
     *
     * @param state Configuration object to load the state from.
     * @param showErrorDialog Controls whether an error dialog should be
     *                        displayed or not.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    void loadStateFromConfiguration( Configuration state, boolean showErrorDialog )
        throws ConfigurationException
    {
        // Load the global client state information.
        
        try
        {
            // Load the frame information.  It is in a child node.
            Configuration frameState = state.getChild( "frame" );
            // Window position
            setLocation( frameState.getAttributeAsInteger( "x" ),
                frameState.getAttributeAsInteger( "y" ) );
            setSize( frameState.getAttributeAsInteger( "width" ),
                frameState.getAttributeAsInteger( "height" ) );
            // Window state
            if ( frameState.getAttributeAsBoolean( "iconized", false ) )
            {
                setState( Frame.ICONIFIED );
            }
            // Split Pane state
            m_splitPane.setDividerLocation(
                frameState.getAttributeAsInteger( "divider-location" ) );
            m_splitPane.setLastDividerLocation(
                frameState.getAttributeAsInteger( "last-divider-location" ) );
        }
        catch ( ConfigurationException e )
        {
            String msg = "Unable to fully load the frame state.";
            if ( showErrorDialog )
            {
                showErrorDialog( msg, e );
            }
            else
            {
                getLogger().warn( msg, e );
            }
        }
        
        // Show the frame here so that the rest of this works.
        show();
            
        // Load the state of any connections.
        Configuration[] connConfs = state.getChildren( "connection" );
        for( int i = 0; i < connConfs.length; i++ )
        {
            Configuration connConf = connConfs[ i ];
            String host = connConf.getAttribute( "host" );
            int port = connConf.getAttributeAsInteger( "port" );
            String key = getInstrumentManagerConnectionKey( host, port );
            
            InstrumentManagerConnection connection;
            synchronized (m_connections)
            {
                connection = (InstrumentManagerConnection)m_connections.get( key );
                
                if ( connection == null )
                {
                    // Need to create a new connection.
                    connection = createInstrumentManagerConnection( host, port );
                }
            }
            
            // Load the state into the connection.
            try
            {
                connection.loadState( connConf );
            }
            catch ( ConfigurationException e )
            {
                String msg = "Unable to fully load the state of connection, " + key;
                if ( showErrorDialog )
                {
                    showErrorDialog( msg, e );
                }
                else
                {
                    getLogger().warn( msg, e );
                }
            }
        }
        
        // Load the state of any inner frames.
        Configuration[] frameConfs = state.getChildren( "inner-frame" );
        for( int i = 0; i < frameConfs.length; i++ )
        {
            Configuration frameConf = frameConfs[ i ];
            String type = frameConf.getAttribute( "type" );
            
            if ( type.equals( InstrumentSampleFrame.FRAME_TYPE ) )
            {
                // Figure out which connection the frame will belong to.
                String host = frameConf.getAttribute( "host" );
                int port = frameConf.getAttributeAsInteger( "port" );
                InstrumentManagerConnection connection =
                    getInstrumentManagerConnection( host, port );
                if ( connection == null )
                {
                    // Connection not found.
                    String msg = "Sample frame not being loaded becase no connection to " +
                        host + ":" + port + " exists.";
                    if ( showErrorDialog )
                    {
                        showErrorDialog( msg );
                    }
                    else
                    {
                        getLogger().warn( msg );
                    }
                } else {
                    // Let the connection load the frame.
                    try
                    {
                        connection.loadSampleFrame( frameConf );
                    }
                    catch ( ConfigurationException e )
                    {
                        String msg = "Unable to fully load the state of an inner frame for sample: " +
                            frameConf.getAttribute( "sample", "Sample name missing" );
                        if ( showErrorDialog )
                        {
                            showErrorDialog( msg, e );
                        }
                        else
                        {
                            getLogger().warn( msg, e );
                        }
                    }
                }
            }
            else
            {
                // Ignore unknown types.
                getLogger().warn( "Not loading inner frame due to unknown type: " + type );
            }
        }
    }

    /**
     * Saves the Instrument Client's state to the specified file.  Any
     *  existing file is backed up before the save takes place and replaced
     *  in the event of an error.
     *
     * @param stateFile File to write the Instrument Client's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    void saveStateToFile( File stateFile ) throws Exception
    {
        long now = System.currentTimeMillis();
        getLogger().debug( "Saving Instrument Client state to: " + stateFile.getAbsolutePath() );

        // First save the state to an in memory stream to shorten the
        //  period of time needed to write the data to disk.  This makes it
        //  less likely that the files will be left in a corrupted state if
        //  the JVM dies at the wrong time.
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] data;
        try
        {
            saveStateToStream( os );
            data = os.toByteArray();
        }
        finally
        {
            os.close();
        }
        
        // If the specified file exists, then rename it before we start writing.
        //  This makes it possible to recover from some errors.
        File renameFile = null;
        boolean success = false;
        if( stateFile.exists() )
        {
            renameFile = new File( stateFile.getAbsolutePath() + "." + now + ".backup" );
            stateFile.renameTo( renameFile );
        }
        
        // Write the data to the new file.
        FileOutputStream fos = new FileOutputStream( stateFile );
        try
        {
            fos.write( data );
            success = true;
        }
        finally
        {
            fos.close();
            
            if ( !success )
            {
                // Make sure that part of the file does not exist.
                stateFile.delete();
            }
            
            // Handle the backup file.
            if ( renameFile != null )
            {
                if ( success )
                {
                    // No longer need the backup.
                    renameFile.delete();
                }
                else
                {
                    // Need to replace the backup.
                    renameFile.renameTo( stateFile );
                }
            }
        }
        
        getLogger().debug( "Saving Instrument Client state took " +
                           ( System.currentTimeMillis() - now ) + "ms." );
    }

    /**
     * Saves the Instrument Client's state to the specified output stream.
     *
     * @param os Stream to write the Instrument Client's state to.
     *
     * @throws Exception if there are any problems saving the state.
     */
    void saveStateToStream( OutputStream os ) throws Exception
    {
        Configuration stateConfig = saveStateToConfiguration();

        // Ride on top of the Configuration classes to save the state.
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        serializer.setIndent( true );
        serializer.serialize( os, stateConfig );
    }

    /**
     * Returns the Instrument Manager's state as a Configuration object.
     *
     * @return The Instrument Manager's state as a Configuration object.
     */
    Configuration saveStateToConfiguration()
    {
        DefaultConfiguration state = new DefaultConfiguration( "instrument-client-state", "-" );
        
        // Save the frame information.  Use a seperate element to keep it clean.
        DefaultConfiguration frameState = new DefaultConfiguration( "frame", "-" );
        // Window position
        frameState.setAttribute( "x", Integer.toString( getX() ) );
        frameState.setAttribute( "y", Integer.toString( getY() ) );
        frameState.setAttribute( "width", Integer.toString( getWidth() ) );
        frameState.setAttribute( "height", Integer.toString( getHeight() ) );
        // Window state
        if ( getState() == Frame.ICONIFIED )
        {
            frameState.setAttribute( "iconized", "true" );
        }
        // Split Pane state
        frameState.setAttribute( "divider-location", Integer.toString( m_splitPane.getDividerLocation() ) );
        frameState.setAttribute( "last-divider-location", Integer.toString( m_splitPane.getLastDividerLocation() ) );
        // Add frame state
        state.addChild( frameState );
        
        // Save the state of any connections.
        InstrumentManagerConnection[] connections = getInstrumentManagerConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            state.addChild( connections[ i ].saveState() );
        }
        
        // Save the state of any inner frames.
        JInternalFrame frames[] = m_desktopPane.getAllFrames();
        for ( int i = 0; i < frames.length; i++ )
        {
            if ( frames[i] instanceof AbstractInternalFrame )
            {
                AbstractInternalFrame internalFrame = (AbstractInternalFrame)frames[i];
                state.addChild( internalFrame.getState() );
            }
        }

        return state;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private void init()
    {
        updateTitle();
        
        // Add a shutdown hook to trap CTRL-C events.
        m_hook = new Thread( SHUTDOWN_HOOK_NAME )
        {
            public void run()
            {
                getLogger().debug( "InstrumentClientFrame.shutdownHook start");
                
                shutdown();
                
                getLogger().debug( "InstrumentClientFrame.shutdownHook end");
            }
        };
        Runtime.getRuntime().addShutdownHook( m_hook );
        
        // Add a Window listener to trap when the user hits the close box.
        addWindowListener( new WindowAdapter()
            {
                public void windowClosing( WindowEvent event )
                {
                    fileExit();
                }
            });

        getContentPane().setLayout( new BorderLayout() );
        
        // Create a Tabbed Panel of the connections.
        m_connectionsPane = new JTabbedPane( JTabbedPane.TOP );
        
        // Create a DesktopPane and place it in a BevelBorder
        m_desktopPane = new DesktopPane();
        JPanel dBorder = new JPanel();
        dBorder.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
        dBorder.setLayout( new BorderLayout() );
        dBorder.add( m_desktopPane, BorderLayout.CENTER );

        // Create a SplitPane at the root.
        m_splitPane =
            new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, m_connectionsPane, dBorder );
        m_splitPane.setOneTouchExpandable( true );
        m_splitPane.setDividerLocation( 250 );
        
        getContentPane().add( m_splitPane, BorderLayout.CENTER );

        // Create a Menu Bar
        m_menuBar = new MenuBar( this );
        setJMenuBar( m_menuBar );
        
        m_statusBar = new StatusBar();
        getContentPane().add( m_statusBar, BorderLayout.SOUTH );

        Toolkit toolkit = getToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        
        setLocation( 20, 20 );
        setSize( (int)(screenSize.width * 0.9), (int)(screenSize.height * 0.9) );
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
    
    void setStatusMessage( String message )
    {
        m_statusBar.setStatusMessage( message );
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
    
    /**
     * Tile all open frames
     */
    void tileFrames()
    {
        JInternalFrame[] openFrames = getOpenFrames();
        
        int count = openFrames.length;
        if ( count == 0)
        {
            return;
        }
        
        // Target the frames at the specified maximum aspect ratio.  The
        //  additional constraint that the frames will not be allowed to
        //  be less than 70 pixels in height unless their width is less
        //  than 100.
        float targetRatio = 5.0f;
        
        Dimension size = getDesktopPane().getSize();
        int cols = 1;
        int rows = count;
        int frameWidth = size.width / cols;
        int frameHeight = size.height / rows;
        float ratio = (float)frameWidth / frameHeight;
        while ( ( rows > 1 ) && ( ( ratio > targetRatio ) ||
            ( ( frameHeight < 70 ) && ( frameWidth > 100 ) ) ) )
        {
            cols++;
            rows = (int)Math.ceil( (float)count / cols );
            frameWidth = size.width / cols;
            frameHeight = size.height / rows;
            ratio = (float)frameWidth / frameHeight;
        }
        
        reorganizeFrames( rows, cols, openFrames );
    }
    
    /**
     * Get a list with all open frames. 
     *
     * @return Array of all open internal frames
     */
    JInternalFrame[] getOpenFrames()
    {
        JInternalFrame[] frames = m_desktopPane.getAllFrames();
        int count = frames.length;
        
        // No frames
        if (count == 0) 
        {
            // Array is empty, so it is safe to return.
            return frames;
        }
    
        // add only open frames to the list
        ArrayList openFrames = new ArrayList();
        for ( int i = 0; i < count; i++ )
        {
            JInternalFrame f = frames[i];
            if( ( f.isClosed() == false ) && ( f.isIcon() == false ) )
            {
                openFrames.add( f );
            }
        }
        
        // Create a simple array to be returned
        frames = new JInternalFrame[ openFrames.size() ];
        openFrames.toArray( frames );
        
        return frames;
    }
    
    /**
     * Reorganizes a list of internal frames to a specific
     * number of rows and columns.
     *
     * @param rows number of rows to use
     * @param cols number of columns to use
     * @param frames list with <code>JInternalFrames</code>
     */  
    void reorganizeFrames( int rows, int cols, JInternalFrame[] frames )
    {
        // Determine the size of one windows
        Dimension desktopsize = m_desktopPane.getSize();
        int w = desktopsize.width / cols;
        int h = desktopsize.height / rows;
        int x = 0;
        int y = 0;
        int count = frames.length;

        for ( int i = 0; i < rows; ++i)
        {
            for ( int j = 0; j < cols && ( ( i * cols ) + j < count ); ++j ) 
            {
                JInternalFrame f = frames[ ( i * cols ) + j ];
                m_desktopPane.getDesktopManager().resizeFrame( f, x, y, w, h );
                x += w;
            }
            y += h;
            x = 0;   
        }
    }
    
    /**
     * Tiles all internal frames horizontally
     */
    void tileFramesH()
    {
        JInternalFrame[] openFrames = getOpenFrames();
        
        int count = openFrames.length;
        if ( count == 0 )
        {
            return;
        }
        reorganizeFrames( count, 1, openFrames );
    }
    
    /**
     * Tiles all internal frames vertically
     */
    void tileFramesV()
    {
        JInternalFrame[] openFrames = getOpenFrames();
        
        int count=openFrames.length;
        if ( count == 0)
        {
            return;
        }
        reorganizeFrames( 1, count, openFrames );
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
    
    /**
     * Returns the key used to access connections in the connections map.
     *
     * @return The key used to access connections in the connections map.
     */
    private String getInstrumentManagerConnectionKey( String host, int port )
    {
        return host + ":" + port;
    }
    
    /**
     * Creates an registers a new InstrumentManagerConnection.  This method
     *  should never be called in the connection already exists.  Caller must
     *  ensure that m_connections is synchronized.
     *
     * @param host Host of the connecton.
     * @param port Port of the connecton.
     *
     * @return The new InstrumentManagerConnection
     */
    private InstrumentManagerConnection createInstrumentManagerConnection( String host, int port )
    {
        String key = getInstrumentManagerConnectionKey( host, port );
        InstrumentManagerConnection connection =
            new InstrumentManagerConnection( this, host, port );
        connection.enableLogging( getLogger() );
        m_connections.put( key, connection );
        m_connectionArray = null;
        
        connection.addInstrumentManagerConnectionListener(
            InstrumentClientFrame.this );
        
        m_connectionsPane.add( connection.getTabTitle(), connection );
        
        return connection;
    }
    
    void openInstrumentManagerConnection( final String host, final int port )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                String key = getInstrumentManagerConnectionKey( host, port );
                synchronized (m_connections)
                {
                    InstrumentManagerConnection connection =
                        (InstrumentManagerConnection)m_connections.get( key );
                    if ( connection == null )
                    {
                        createInstrumentManagerConnection( host, port );
                        
                        return;
                    }
                }
                
                // If we get here show an error that the connection alreay exists.
                //  Must be done outside the synchronization block.
                showErrorDialog( "A connection to " + key + " already exists." );
            }
        } );
    }
    
    /*
    void openInstrumentSampleFrame( final InstrumentManagerConnection connection,
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
    */
    
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
        getLogger().debug( "InstrumentClientFrame.shutdown()" );
        boolean fallThrough = false;
        if ( m_hook != null )
        {
            if ( m_hook == Thread.currentThread() )
            {
                // This is the shutdown hook
                fallThrough = true;
            }
            else
            {
                // Unregister the shutdown hook
                Runtime.getRuntime().removeShutdownHook( m_hook );
                m_hook = null;
            }
        }
        
        // Stop the runner.
        m_runner.interrupt();
        m_runner = null;
        
        // Close all connections cleanly.
        InstrumentManagerConnection[] connections = getInstrumentManagerConnections();
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i].delete();
        }
        
        if ( !fallThrough )
        {
            // Kill the JVM.
            System.exit( 1 );
        }
    }
    
    /*---------------------------------------------------------------
     * Menu Callback Methods
     *-------------------------------------------------------------*/
    /**
     * File->New callback.
     */
    void fileNew()
    {
        m_desktopFile = null;
        closeAllFrames();
        updateTitle();
    }
    
    /**
     * File->Open callback.
     */
    void fileOpen()
    {
        JFileChooser chooser = new JFileChooser();
        
        FileFilter filter = new FileFilter()
        {
            public boolean accept( File f )
            {
                if( f.isDirectory() )
                {
                    return true;
                }
                else
                {
                    return f.getName().endsWith( ".desktop" );
                }
            }
            
            public String getDescription()
            {
                return "Desktop state files";
            }
        };
        
        if ( m_desktopFileDir != null )
        {
            chooser.setCurrentDirectory( m_desktopFileDir );
        }
        else
        {
            chooser.setCurrentDirectory( new File( System.getProperty( "user.dir" ) ) );
        }
        
        chooser.setFileFilter( filter );
        
        int returnVal = chooser.showOpenDialog( this );
        if( returnVal == JFileChooser.APPROVE_OPTION )
        {
            try
            {
                m_desktopFile = null;
                File file = chooser.getSelectedFile();
                m_desktopFileDir = file.getParentFile();
                loadStateFromFile( file, true );
                m_desktopFile = file;
            }
            catch( Exception e )
            {
                showErrorDialog( "Unable to load desktop file.", e );
            }
            updateTitle();
        }
    }
    
    void fileSave()
    {
        if( m_desktopFile != null )
        {
            try
            {
                saveStateToFile( m_desktopFile );
            }
            catch( Exception e )
            {
                showErrorDialog( "Unable to save desktop file.", e );
            }
        }
        else
        {
            fileSaveAs();
        }
    }
    
    void fileSaveAs()
    {
        JFileChooser chooser = new JFileChooser();
        
        FileFilter filter = new FileFilter()
        {
            public boolean accept( File f )
            {
                if( f.isDirectory() )
                {
                    return true;
                }
                else
                {
                    return f.getName().endsWith( ".desktop" );
                }
            }
            
            public String getDescription()
            {
                return "Desktop state files";
            }
        };
        
        if ( m_desktopFileDir != null )
        {
            chooser.setCurrentDirectory( m_desktopFileDir );
        }
        else
        {
            chooser.setCurrentDirectory( new File( System.getProperty( "user.dir" ) ) );
        }
        
        chooser.setFileFilter( filter );
        
        int returnVal = chooser.showSaveDialog( this );
        if( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File file = chooser.getSelectedFile();
            if( file.getName().indexOf( '.' ) < 0 )
            {
                // Did not specify an extension.  Add one.
                file = new File( file.getAbsolutePath() + ".desktop" );
            }
            
            try
            {
                saveStateToFile( file );
                
                // If we were able to save the file, then set it as the current
                //  file.
                m_desktopFile = file;
                m_desktopFileDir = m_desktopFile.getParentFile();

            }
            catch( Exception e )
            {
                showErrorDialog( "Unable to save desktop file.", e );
            }
            updateTitle();
        }
    }
    
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
}

