/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.excalibur.altrmi.client.AltrmiHostContext;
import org.apache.excalibur.altrmi.client.AltrmiFactory;
import org.apache.excalibur.altrmi.client.impl.socket.SocketCustomStreamHostContext;
import org.apache.excalibur.altrmi.client.impl.ClientClassAltrmiFactory;
import org.apache.excalibur.altrmi.common.AltrmiConnectionException;
import org.apache.excalibur.altrmi.common.AltrmiInvocationException;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleSnapshot;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleUtils;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/22 16:50:38 $
 * @since 4.1
 */
class InstrumentManagerConnection
    extends JComponent
    implements LogEnabled
{
    private final InstrumentClientFrame m_frame;
    private final String m_host;
    private final int m_port;
    
    private Logger m_logger;
    
    private boolean m_closed;
    private boolean m_deleted;
    private AltrmiHostContext m_altrmiHostContext;
    private AltrmiFactory m_altrmiFactory;
    private InstrumentManagerClient m_manager;
    private InstrumentManagerTreeModel m_treeModel;
    private InstrumentManagerTree m_tree;
    
    private final ArrayList m_listeners = new ArrayList();
    private InstrumentManagerConnectionListener[] m_listenerArray = null;

    private long m_lastLeaseRenewalTime;
    private HashMap m_maintainedSampleLeaseMap = new HashMap();
    private MaintainedSampleLease[] m_maintainedSampleLeaseArray = null;
    
    /** Maintain a list of all sample frames which are viewing data in this connection. */
    private HashMap m_sampleFrames = new HashMap();
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentManagerConnection( InstrumentClientFrame frame, String host, int port )
    {
        m_frame = frame;
        m_host = host;
        m_port = port;
        m_closed = true;
        
        m_treeModel = new InstrumentManagerTreeModel( this );
        addInstrumentManagerConnectionListener( m_treeModel );
        
        setLayout( new BorderLayout() );
        
        // Top Pane
        Box topPane = Box.createVerticalBox();
        
        // Top Labels
        JPanel labels = new JPanel();
        labels.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        
        JLabel hostLabelLabel = new JLabel( "Host: " );
        labels.add( hostLabelLabel );
        JLabel hostLabel = new JLabel( getHost() );
        hostLabel.setForeground( Color.black );
        labels.add( hostLabel );
        
        JLabel portLabelLabel = new JLabel( "  Port: " );
        labels.add( portLabelLabel );
        JLabel portLabel = new JLabel( Integer.toString( getPort() ) );
        portLabel.setForeground( Color.black );
        labels.add( portLabel );
        
        topPane.add( labels );
        
        // Top Buttons
        Action gcAction = new AbstractAction( "Invoke GC" )
        {
            public void actionPerformed( ActionEvent event )
            {
                SwingUtilities.invokeLater( new Runnable()
                    {
                        public void run()
                        {
                            InstrumentManagerConnection.this.invokeGC();
                        }
                    });
            }
        };
        JButton gcButton = new JButton( gcAction );
        
        Action refreshAction = new AbstractAction( "Refresh" )
        {
            public void actionPerformed( ActionEvent event )
            {
                SwingUtilities.invokeLater( new Runnable()
                    {
                        public void run()
                        {
                            InstrumentManagerConnection.this.getTreeModel().refreshModel();
                        }
                    });
            }
        };
        JButton refreshButton = new JButton( refreshAction );
        
        JPanel buttons = new JPanel();
        buttons.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        buttons.add ( gcButton );
        buttons.add ( refreshButton );
        topPane.add( buttons );
        
        add( topPane, BorderLayout.NORTH );
        
        // Tree Pane
        m_tree = new InstrumentManagerTree( this );
        add( m_tree, BorderLayout.CENTER );
    }
    
    /*---------------------------------------------------------------
     * LogEnabled Methods
     *-------------------------------------------------------------*/
    public void enableLogging( Logger logger )
    {
        m_logger = logger.getChildLogger( "conn_" + m_host + "_" + m_port );
    }
    
    Logger getLogger()
    {
        return m_logger;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    String getTabTitle()
    {
        String tabTitle;
        synchronized(this)
        {
            if ( m_manager == null )
            {
                tabTitle = "[Not Connected]";
            }
            else
            {
                tabTitle = m_manager.getDescription();
            }
        }
        return tabTitle;
    }
    
    String getHost()
    {
        return m_host;
    }
    
    int getPort()
    {
        return m_port;
    }
    
    /**
     * Returns a title for the connection which can be used in frame titlesa
     *  and menus.  Reflects the connected status.
     */
    String getTitle()
    {
        return getTabTitle() + " (" + m_host + ":" + m_port + ")";
    }
    
    InstrumentManagerClient getInstrumentManagerClient()
    {
        return m_manager;
    }
    
    InstrumentManagerTreeModel getTreeModel()
    {
        return m_treeModel;
    }
    
    /**
     * Returns a thread save array representation of the MaintainedSampleLeases.
     *
     * @return A thread save array representation of the MaintainedSampleLeases.
     */
    private MaintainedSampleLease[] getMaintainedSampleLeaseArray()
    {
        MaintainedSampleLease[] array = m_maintainedSampleLeaseArray;
        if ( array == null )
        {
            synchronized(this)
            {
                m_maintainedSampleLeaseArray =
                    new MaintainedSampleLease[ m_maintainedSampleLeaseMap.size() ];
                m_maintainedSampleLeaseMap.values().toArray( m_maintainedSampleLeaseArray );
                array = m_maintainedSampleLeaseArray;
            }
        }
        return array;
    }
    
    /**
     * Called once each second by the main worker thread of the client.  This
     *  method is responsible for maintaining and expiring leased samples.
     */
    void handleLeasedSamples()
    {
        synchronized(this)
        {
            // If we are not connected, then there is nothing to be done here.
            
            // Only renew leases once per minute.
            long now = System.currentTimeMillis();
            if ( now - m_lastLeaseRenewalTime > 60000 )
            {
                //System.out.println("Renew Leases:");
                MaintainedSampleLease[] leases = getMaintainedSampleLeaseArray();
                for ( int i = 0; i < leases.length; i++ )
                {
                    MaintainedSampleLease lease = leases[i];
                    //System.out.println(" lease: " + lease.getSampleName());
                    
                    // Look for the Sample Descriptor in the Tree Model
                    DefaultMutableTreeNode sampleTreeNode =
                        m_treeModel.getInstrumentSampleTreeNode( lease.getSampleName() );
                    if ( sampleTreeNode == null )
                    {
                        // A node does not yet exist for the sample.  We need to
                        //  create it on the server to make sure that it exists.
                        //  Then refresh the Instrument in the tree node so that it
                        //  is created.
                        
                        // Loof for the Instrument Descriptor in the Tree Model
                        DefaultMutableTreeNode instrumentTreeNode =
                            m_treeModel.getInstrumentTreeNode( lease.getInstrumentName() );
                        if ( instrumentTreeNode == null )
                        {
                            // Instrument does not exist.  Ignore this for now.
                        }
                        else
                        {
                            // Get the InstrumentDescriptor
                            InstrumentDescriptor instrumentDescriptor = 
                                ((InstrumentNodeData)instrumentTreeNode.getUserObject()).
                                getDescriptor();
                            
                            // Now attempt to create the sample
                            try
                            {
                                instrumentDescriptor.createInstrumentSample(
                                    lease.getDescription(), lease.getInterval(), lease.getSize(),
                                    lease.getLeaseDuration(), lease.getType() );
                                
                                // Refresh the Tree Model
                                m_treeModel.updateInstrument( instrumentDescriptor, instrumentTreeNode );
                            }
                            catch ( AltrmiInvocationException e )
                            {
                                // Means that the connection died.
                                close();
                            }
                        }
                    }
                    else
                    {
                        // A sample descriptor already exists.  Simply extend it.
                        InstrumentSampleNodeData sampleNodeData =
                            (InstrumentSampleNodeData)sampleTreeNode.getUserObject();
                        
                        // Get the InstrumentSampleDescriptor
                        InstrumentSampleDescriptor sampleDescriptor = sampleNodeData.getDescriptor();
                        
                        try
                        {
                            long newExpireTime =
                                sampleDescriptor.extendLease( lease.getLeaseDuration() );
                            //System.out.println("  Extended lease to: " + newExpireTime );
                            
                            sampleNodeData.setLeaseExpireTime( newExpireTime );
                            
                            // Refresh the Tree Model
                            m_treeModel.updateInstrumentSample( sampleDescriptor, sampleTreeNode );
                        }
                        catch ( AltrmiInvocationException e )
                        {
                            // Means that the connection died.
                            close();
                        }
                        
                    }
                }
                
                // Also, take this oportunity to update all of the leased samples in
                //  the model.
                m_treeModel.updateAllLeasedSamples();
            
                m_lastLeaseRenewalTime = now;
            }
            
            // Now have the TreeModel purge any expired samples from the tree.
            m_treeModel.purgeExpiredSamples();
        }
    }
    
    void open() throws AltrmiConnectionException, IOException
    {
        getLogger().debug( "open()" );
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
    void tryOpen()
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
    boolean isClosed()
    {
        return m_closed;
    }
    
    /**
     * Closes the connection, but keeps it around.  If the remote instrument manager
     *  is running the connection will reopen itself.
     */
    void close()
    {
        getLogger().debug( "close()" );
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
     * Returns true if the connection is currently deleted.
     *
     * @return True if the connection is currently deleted.
     */
    boolean isDeleted()
    {
        return m_deleted;
    }
    
    /**
     * Called when the connection should be closed and then deleted along with
     *  any frames and resources that are associated with it.
     */
    void delete()
    {
        getLogger().debug( "delete()" );
        close();
        
        m_deleted = true;
        
        // Notify the listeners outside of synchronization.
        InstrumentManagerConnectionListener[] listenerArray = getListenerArray();
        for ( int i = 0; i < listenerArray.length; i++ )
        {
            listenerArray[i].deleted( this );
        }
    }
        
    boolean ping()
    {
        synchronized(this)
        {
            // Ping the server by requesting the manager's name
            if ( m_manager != null )
            {
                try
                {
                    String name = m_manager.getName();
                    return true;
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
        return false;
    }
    
    /**
     * Adds a InstrumentManagerConnectionListener to the list of listeners
     *  which receive state updates of the connection.
     *
     * @param listener InstrumentManagerConnectionListener to start receiving
     *                 state updates.
     */
    void addInstrumentManagerConnectionListener(
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
    void removeInstrumentManagerConnectionListener(
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
    
    /**
     * Returns a sample frame given a sample name.
     * Caller must synchronize on this connection before calling.
     *
     * @param sampleName Name of the sample requested.
     *
     * @return A sample frame given a sample name.
     */
    private InstrumentSampleFrame getSampleFrame( String sampleName )
    {
        System.out.println("InstrumentManagerConnection.getSampleFrame(" + sampleName + ")");
        // Assumes "this" is synchronized.
        return (InstrumentSampleFrame)m_sampleFrames.get( sampleName );
    }
    
    private void addSampleFrame( String sampleName, InstrumentSampleFrame sampleFrame )
    {
        System.out.println("InstrumentManagerConnection.addSampleFrame(" + sampleName + ", frame)");
        // Assumes "this" is synchronized.
        m_sampleFrames.put( sampleName, sampleFrame );
    }

    private void removeSampleFrame( String sampleName )
    {
        System.out.println("InstrumentManagerConnection.removeSampleFrame(" + sampleName + ")");
        // Assumes "this" is synchronized.
        m_sampleFrames.remove( sampleName );
    }
    
    /**
     * Create a new Sample assigned to the specified instrument descriptor.
     *
     * @param instrumentDescriptor Instrument to add a sample to.
     */
    void instrumentCreateSample( final InstrumentDescriptor instrumentDescriptor )
    {
        //m_frame.instrumentCreateSample( this, instrumentDescriptor );
        
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                CreateSampleDialog dialog =
                    new CreateSampleDialog( m_frame, instrumentDescriptor );
                
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
                    
                    // If the sample already exists on the server, then the existing one
                    //  will be returned.
                    InstrumentSampleDescriptor sampleDescriptor =
                        instrumentDescriptor.createInstrumentSample(
                            dialog.getSampleDescription(),
                            dialog.getInterval(),
                            dialog.getSampleCount(),
                            dialog.getLeaseTime(),
                            dialog.getSampleType() );
                    
                    // Update the model.
                    m_treeModel.updateInstrument( instrumentDescriptor );
                    
                    InstrumentSampleNodeData sampleNodeData = startMaintainingSample(
                        instrumentDescriptor.getName(), dialog.getSampleType(),
                        dialog.getInterval(), dialog.getSampleCount(),
                        dialog.getLeaseTime(), dialog.getSampleDescription() );
                    
                    // We should always have a NodeData for the sample, but it is
                    //  possible that it could be null if there were errors, so
                    //  be careful.
                    if ( sampleNodeData != null )
                    {
                        // Show a frame for the new sample
                        viewSample( sampleNodeData );
                    }
                }
            }
        } );
    }
    
    /**
     * Loads an InstrumentSampleFrame from a saved state.
     *
     * @param sampleFrameState Saved state of the frame to load.
     */
    void loadSampleFrame( Configuration sampleFrameState ) throws ConfigurationException
    {
        // Get the sample name
        String sampleName = sampleFrameState.getAttribute( "sample" );
        getLogger().debug( "Loading sample frame: " + sampleName );
        
        // See if a frame already exists.
        InstrumentSampleFrame sampleFrame;
        synchronized(this)
        {
            sampleFrame = getSampleFrame( sampleName );
            if ( sampleFrame != null )
            {
                // A frame already existed.  It needs to be closed as it will be
                // replaced by the new one.
                sampleFrame.hideFrame();
                sampleFrame = null;
            }
            
            // Now create the frame
            sampleFrame = new InstrumentSampleFrame( sampleFrameState, this, m_frame );
            addSampleFrame( sampleName, sampleFrame );
            sampleFrame.addToDesktop( m_frame.getDesktopPane() );
            sampleFrame.show();
        }
        
        /* Old
        synchronized(m_treeModel)
        {
            // See if a Sample Node Data object exists.
            DefaultMutableTreeNode sampleTreeNode =
                m_treeModel.getInstrumentSampleTreeNode( sampleName );
            InstrumentSampleNodeData sampleNodeData = null;
            if ( sampleTreeNode != null )
            {
                sampleNodeData =
                    (InstrumentSampleNodeData)sampleTreeNode.getUserObject();
                if ( sampleNodeData != null )
                {
                    InstrumentSampleFrame frame = sampleNodeData.getInstrumentSampleFrame();
                    if ( frame != null )
                    {
                        frame.hideFrame();
                    }
                }
            }
            
            // Now create the frame
            InstrumentSampleFrame frame = new InstrumentSampleFrame( sampleFrameState, this, m_frame );
            if ( sampleNodeData != null )
            {
                sampleNodeData.setInstrumentSampleFrame( frame );
            }
            frame.addToDesktop( m_frame.getDesktopPane() );
            frame.show();
        }
        */
    }
    
    /**
     * Displays a frame for the given sample.
     *
     * @param sampleNodeData Instrument sample to display.
     */
    void viewSample( InstrumentSampleNodeData sampleNodeData )
    {
        InstrumentSampleFrame sampleFrame;
        synchronized( this )
        {
            String sampleName = sampleNodeData.getName();
            sampleFrame = getSampleFrame( sampleName );
            if ( sampleFrame == null )
            {
                sampleFrame = new InstrumentSampleFrame( this, sampleName, m_frame );
                addSampleFrame( sampleName, sampleFrame );
                sampleFrame.addToDesktop( m_frame.getDesktopPane() );
            }
        }
        
        /* Old.
        synchronized( sampleNodeData )
        {
        // See if the NodeData already has a frame
        sampleFrame = sampleNodeData.getInstrumentSampleFrame();
        if ( sampleFrame == null )
        {
        sampleFrame = new InstrumentSampleFrame( this, sampleNodeData.getName(), m_frame );
        sampleNodeData.setInstrumentSampleFrame( sampleFrame );
        sampleFrame.addToDesktop( m_frame.getDesktopPane() );
        }
        }
         */
        
        sampleFrame.show();
        if ( sampleFrame.isIcon() )
        {
            // Restore the sample frame.
            try
            {
                sampleFrame.setIcon( false );
            }
            catch ( PropertyVetoException e ) {}
        }
    }
    
    /**
     * Called when a Sample Frame is closed.
     */
    void hideSampleFrame( InstrumentSampleFrame sampleFrame )
    {
        String sampleName = sampleFrame.getInstrumentSampleName();
        synchronized(this)
        {
            removeSampleFrame( sampleName );
        }
    }
    
    /**
     * Start maintaining the lease for an instrument sample which already
     *  exists.
     *
     * @param leaseDuration Length of the lease to maintain in milliseconds.
     *
     * @return The NodeData object of the sample.  May return null if the
     *         NodeData has not yet been created.
     */
    InstrumentSampleNodeData startMaintainingSample( String instrumentName,
                                                     int    type,
                                                     long   interval,
                                                     int    size,
                                                     long   leaseDuration,
                                                     String description )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "startMaintainingSample(" + instrumentName + ", " + type + ", " +
                interval + ", " + size + ", " + leaseDuration + ", " + description + ")" );
        }
        
        synchronized(this)
        {
            MaintainedSampleLease sampleLease = new MaintainedSampleLease(
                instrumentName, type, interval, size, leaseDuration, description );
            String sampleName = sampleLease.getSampleName();
            m_maintainedSampleLeaseMap.put( sampleName, sampleLease );
            m_maintainedSampleLeaseArray = null;
            
            // Reset te last lease renewal time so that the leases along with this
            //  new one will be renewed right away.
            m_lastLeaseRenewalTime = 0;
            
            // Update the appropriate node in the tree model.
            DefaultMutableTreeNode sampleTreeNode =
                m_treeModel.getInstrumentSampleTreeNode( sampleName );
            
            InstrumentSampleNodeData sampleNodeData;
            if ( sampleTreeNode != null )
            {
                sampleNodeData = (InstrumentSampleNodeData)sampleTreeNode.getUserObject();
                
                sampleNodeData.setLeaseDuration( leaseDuration );
                sampleNodeData.setDescription( description );
                m_treeModel.updateInstrumentSample( sampleNodeData.getDescriptor(), sampleTreeNode );
            }
            else
            {
                sampleNodeData = null;
            }
            
            return sampleNodeData;
        }
    }
    
    /**
     * Stop maintaining the lease for an instrument sample which already
     *  exists.
     */
    void stopMaintainingSample( String sampleName )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "stopMaintainingSample(" + sampleName + ")" );
        }
        
        synchronized(this)
        {
            m_maintainedSampleLeaseMap.remove( sampleName );
            m_maintainedSampleLeaseArray = null;
            
            // Update the appropriate node in the tree model.
            DefaultMutableTreeNode sampleTreeNode =
                m_treeModel.getInstrumentSampleTreeNode( sampleName );
            if ( sampleTreeNode != null )
            {
                InstrumentSampleNodeData sampleNodeData =
                    (InstrumentSampleNodeData)sampleTreeNode.getUserObject();
                
                sampleNodeData.setLeaseDuration( 0 );
                m_treeModel.updateInstrumentSample( sampleNodeData.getDescriptor(), sampleTreeNode );
            }
        }
    }
    
    /**
     * Returns a MaintainedSampleLease given a name if the sample is being
     *  maintained. Otherwise returns null.
     *
     * @param sampleName Name of the sample being requested.
     *
     * @return A MaintainedSampleLease given a name.
     */
    MaintainedSampleLease getMaintainedSampleLease( String sampleName )
    {
        synchronized(this)
        {
            return (MaintainedSampleLease)m_maintainedSampleLeaseMap.get( sampleName );
        }
    }

    /**
     * Invokes GC on the JVM running the InstrumentManager.
     */
    private void invokeGC()
    {
        InstrumentManagerClient manager = getInstrumentManagerClient();
        if ( manager != null )
        {
            try
            {
                manager.invokeGarbageCollection();
            }
            catch ( AltrmiInvocationException e )
            {
                System.out.println( "Error executing GC on " + getHost() + ":" + 
                    getPort() + ": " + e.getMessage() );
            }
        }
    }
    
    DefaultMutableTreeNode getInstrumentSampleTreeNode( String sampleName )
    {
        return m_treeModel.getInstrumentSampleTreeNode( sampleName );
    }
    
    /**
     * Returns a snapshot of the specified sample.  If a snapshot can not
     *  be returned for any reason, then return null.
     *
     * @param Returns a snapshot of the specified sample.
     */
    InstrumentSampleSnapshot getInstrumentSampleSnapshot( String sampleName )
    {
        DefaultMutableTreeNode sampleNode = getInstrumentSampleTreeNode( sampleName );
        if ( sampleNode == null )
        {
            return null;
        }
        
        InstrumentSampleNodeData sampleNodeData =
            (InstrumentSampleNodeData)sampleNode.getUserObject();
        InstrumentSampleDescriptor sampleDescriptor = sampleNodeData.getDescriptor();
        if ( sampleDescriptor == null )
        {
            return null;
        }
        
        // Request the actual snapshot.
        try
        {
            return sampleDescriptor.getSnapshot();
        }
        catch ( AltrmiInvocationException e )
        {
            return null;
        }
    }
    
    /*---------------------------------------------------------------
     * State Methods
     *-------------------------------------------------------------*/
    /**
     * Saves the current state into a Configuration.
     *
     * @return The state as a Configuration.
     */
    public final Configuration saveState()
    {
        synchronized(this)
        {
            DefaultConfiguration state = new DefaultConfiguration( "connection", "-" );
            state.setAttribute( "host", m_host );
            state.setAttribute( "port", Integer.toString( m_port ) );
            
            // Save any maintained samples
            MaintainedSampleLease[] samples = getMaintainedSampleLeaseArray();
            for ( int i = 0; i < samples.length; i++ )
            {
                state.addChild( samples[ i ].saveState() );
            }
            return state;
        }
    }
    
    /**
     * Loads the state from a Configuration object.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    public final void loadState( Configuration state ) throws ConfigurationException
    {
        synchronized( this )
        {
            // Host and port will have already been set.
            
            // Load any maintained samples
            Configuration[] sampleConfs = state.getChildren( "maintained-sample" );
            for( int i = 0; i < sampleConfs.length; i++ )
            {
                Configuration sampleConf = sampleConfs[ i ];
                String instrumentName = sampleConf.getAttribute( "instrument-name" );
                int sampleType = InstrumentSampleUtils.resolveInstrumentSampleType(
                    sampleConf.getAttribute( "type" ) );
                long sampleInterval = sampleConf.getAttributeAsLong( "interval" );
                int sampleSize = sampleConf.getAttributeAsInteger( "size" );
                long sampleLeaseDuration = sampleConf.getAttributeAsLong( "lease-duration" );
                String sampleDescription = sampleConf.getAttribute( "description" );
                
                startMaintainingSample( instrumentName, sampleType, sampleInterval, sampleSize,
                    sampleLeaseDuration, sampleDescription );
            }
        }
    }
}



