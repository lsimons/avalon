/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.avalon.excalibur.instrument.manager.client.InstrumentableDescriptor;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentDescriptor;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentManagerClient;
import org.apache.avalon.excalibur.instrument.manager.client.InstrumentSampleDescriptor;

import org.apache.commons.altrmi.common.AltrmiInvocationException;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:32:24 $
 * @since 4.1
 */
public class MenuBar
    extends JMenuBar
{
    private InstrumentClientFrame m_frame;
    //private ProfilerManager m_profilerManager;

    private JMenu m_menuFile;

    private JMenu m_menuInstrumentManagers;

    private JMenu m_menuOptions;
    private JCheckBoxMenuItem m_menuItemShowUnconfigured;

    private JMenu m_menuWindow;

    private boolean m_showUnconfigured;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    MenuBar( InstrumentClientFrame frame/*, ProfilerManager profilerManager*/ )
    {
        m_frame = frame;
        //m_profilerManager = profilerManager;

        add( buildFileMenu() );
        add( buildInstrumentManagerMenu() );
        add( buildOptionsMenu() );
        add( buildWindowMenu() );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    private JMenu buildFileMenu()
    {
        m_menuFile = new JMenu( "File" );
        m_menuFile.setMnemonic( 'F' );

        // Clear
        Action newAction = new AbstractAction( "New" )
        {
            public void actionPerformed( ActionEvent event )
            {
                //m_frame.fileNew();
            }
        };
        JMenuItem newItem = new JMenuItem( newAction );
        newItem.setMnemonic( 'N' );
        m_menuFile.add( newItem );

        // Open
        Action openAction = new AbstractAction( "Open ..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                //m_frame.fileOpen();
            }
        };
        JMenuItem open = new JMenuItem( openAction );
        open.setMnemonic( 'O' );
        m_menuFile.add( open );

        // Save
        Action saveAction = new AbstractAction( "Save" )
        {
            public void actionPerformed( ActionEvent event )
            {
                //m_frame.fileSave();
            }
        };
        JMenuItem save = new JMenuItem( saveAction );
        save.setMnemonic( 'S' );
        m_menuFile.add( save );

        // Save As
        Action saveAsAction = new AbstractAction( "Save As ..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                //m_frame.fileSaveAs();
            }
        };
        JMenuItem saveAs = new JMenuItem( saveAsAction );
        saveAs.setMnemonic( 'A' );
        m_menuFile.add( saveAs );

        return m_menuFile;
    }

    private JMenu buildInstrumentManagerMenu()
    {
        m_menuInstrumentManagers = new JMenu( "Instrument Managers" );
        m_menuInstrumentManagers.setMnemonic( 'I' );

        m_menuInstrumentManagers.addMenuListener( new MenuListener()
        {
            public void menuSelected( MenuEvent event )
            {
                rebuildInstrumentManagersMenu();
            }

            public void menuDeselected( MenuEvent event )
            {
            }

            public void menuCanceled( MenuEvent event )
            {
            }
        } );

        return m_menuInstrumentManagers;
    }
    
    private void rebuildInstrumentManagersMenu()
    {
        m_menuInstrumentManagers.removeAll();
        
        // Add Connect menu item
        Action connectAction = new AbstractAction( "Connect to Instrument Manager..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                // For now, skip the dialog
                m_frame.openInstrumentManagerConnection( "localhost", 15555 );
            }
        };
        
        JMenuItem connectItem = new JMenuItem( connectAction );
        connectItem.setMnemonic( 'C' );
        m_menuInstrumentManagers.add( connectItem );
        
        // Add links to the connections
        InstrumentManagerConnection[] connections = m_frame.getInstrumentManagerConnections();
        if ( connections.length > 0 )
        {
            m_menuInstrumentManagers.addSeparator();
            
            for ( int i = 0; i < connections.length; i++ )
            {
                InstrumentManagerConnection connection = connections[i];
                
                Action action = new AbstractAction( connection.getTitle() )
                {
                    public void actionPerformed( ActionEvent event )
                    {
                    }
                };
                action.putValue( "InstrumentManagerConnection", connection );
                
                JMenu menu = new JMenu( action );

                // Set up a Listener to handle the selected event.
                menu.addMenuListener( new MenuListener()
                {
                    public void menuSelected( MenuEvent event )
                    {
                        JMenu menu = (JMenu)event.getSource();
                        Action action = menu.getAction();
                        
                        rebuildInstrumentManagerMenu(
                            menu, (InstrumentManagerConnection)action.getValue(
                            "InstrumentManagerConnection" ) );
                    }

                    public void menuDeselected( MenuEvent event )
                    {
                    }

                    public void menuCanceled( MenuEvent event )
                    {
                    }
                } );

                m_menuInstrumentManagers.add( menu );
            }
        }
    }

    private void rebuildInstrumentManagerMenu( JMenu managerMenu,
                                               InstrumentManagerConnection connection )
    {
        managerMenu.removeAll();
        
        boolean showAll = m_menuItemShowUnconfigured.getState();
        
        Action detailAction = new AbstractAction( "Details..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                JMenuItem item = (JMenuItem)event.getSource();
                Action action = item.getAction();
                
                m_frame.openInstrumentManagerConnectionFrame(
                    (InstrumentManagerConnection)action.getValue( "connection" ) );
            }
        };
        detailAction.putValue( "connection", connection );
        
        JMenuItem detailItem = new JMenuItem( detailAction );
        detailItem.setMnemonic( 'D' );
        managerMenu.add( detailItem );
        
        try
        {
            InstrumentManagerClient manager = connection.getInstrumentManagerClient();
            
            if ( manager != null )
            {
                managerMenu.addSeparator();
                
                InstrumentableDescriptor[] descriptors = manager.getInstrumentableDescriptors();
                
                for( int i = 0; i < descriptors.length; i++ )
                {
                    InstrumentableDescriptor descriptor = descriptors[ i ];
        
                    if( showAll || descriptor.isConfigured() )
                    {
                        String description = descriptor.getDescription();
        
                        Action action = new AbstractAction( description )
                        {
                            public void actionPerformed( ActionEvent event )
                            {
                            }
                        };
                        action.putValue( "InstrumentManagerConnection", connection );
                        action.putValue( "InstrumentableDescriptor", descriptor );
        
                        JMenu menu = new JMenu( action );
        
                        // Set up a Listener to handle the selected event.
                        menu.addMenuListener( new MenuListener()
                        {
                            public void menuSelected( MenuEvent event )
                            {
                                JMenu menu = (JMenu)event.getSource();
                                Action action = menu.getAction();
                                
                                rebuildInstrumentableMenu(
                                    menu,
                                    (InstrumentManagerConnection)action.getValue(
                                        "InstrumentManagerConnection" ),
                                    (InstrumentableDescriptor)action.getValue(
                                        "InstrumentableDescriptor" ) );
                            }
        
                            public void menuDeselected( MenuEvent event )
                            {
                            }
        
                            public void menuCanceled( MenuEvent event )
                            {
                            }
                        } );
        
                        managerMenu.add( menu );
                    }
                }
            }
        }
        catch ( AltrmiInvocationException e )
        {
            // Something went wrong, so close the connection.
            connection.close();
        }
    }

    private void rebuildInstrumentableMenu( JMenu instrumentableMenu,
                                            InstrumentManagerConnection connection,
                                            InstrumentableDescriptor instrumentableDescriptor )
    {
        instrumentableMenu.removeAll();

        boolean showAll = m_menuItemShowUnconfigured.getState();
        
        try
        {
            InstrumentDescriptor[] descriptors =
                instrumentableDescriptor.getInstrumentDescriptors();
            
            for( int i = 0; i < descriptors.length; i++ )
            {
                InstrumentDescriptor descriptor = descriptors[ i ];
    
                if( showAll || descriptor.isConfigured() )
                {
                    String description = descriptor.getDescription();
    
                    Action action = new AbstractAction( description )
                    {
                        public void actionPerformed( ActionEvent event )
                        {
                        }
                    };
                    action.putValue( "InstrumentManagerConnection", connection );
                    action.putValue( "InstrumentableDescriptor", instrumentableDescriptor );
                    action.putValue( "InstrumentDescriptor", descriptor );
    
                    JMenu menu = new JMenu( action );
    
                    // Set up a Listener to handle the selected event.
                    menu.addMenuListener( new MenuListener()
                    {
                        public void menuSelected( MenuEvent event )
                        {
                            JMenu menu = (JMenu)event.getSource();
                            Action action = menu.getAction();
                            
                            rebuildInstrumentMenu(
                                menu,
                                (InstrumentManagerConnection)action.getValue(
                                    "InstrumentManagerConnection" ),
                                (InstrumentableDescriptor)action.getValue(
                                    "InstrumentableDescriptor" ),
                                (InstrumentDescriptor)action.getValue(
                                    "InstrumentDescriptor" ) );
                        }
    
                        public void menuDeselected( MenuEvent event )
                        {
                        }
    
                        public void menuCanceled( MenuEvent event )
                        {
                        }
                    } );
    
                    instrumentableMenu.add( menu );
                }
            }
        }
        catch ( AltrmiInvocationException e )
        {
            // Something went wrong, so close the connection.
            connection.close();
        }
    }

    private void rebuildInstrumentMenu( JMenu instrumentMenu,
                                        InstrumentManagerConnection connection,
                                        InstrumentableDescriptor instrumentableDescriptor,
                                        InstrumentDescriptor instrumentDescriptor )
    {
        instrumentMenu.removeAll();

        boolean showAll = m_menuItemShowUnconfigured.getState();
        
        try
        {
            InstrumentSampleDescriptor[] descriptors =
                instrumentDescriptor.getInstrumentSampleDescriptors();
            
            for( int i = 0; i < descriptors.length; i++ )
            {
                InstrumentSampleDescriptor descriptor = descriptors[ i ];
    
                if( showAll || descriptor.isConfigured() )
                {
                    String description = descriptor.getDescription();
    
                    Action action = new AbstractAction( description )
                    {
                        public void actionPerformed( ActionEvent event )
                        {
                            JMenuItem menu = (JMenuItem)event.getSource();
                            Action action = menu.getAction();
                            
                            m_frame.openInstrumentSampleFrame(
                                (InstrumentManagerConnection)action.getValue(
                                    "InstrumentManagerConnection" ),
                                (InstrumentableDescriptor)action.getValue(
                                    "InstrumentableDescriptor" ),
                                (InstrumentDescriptor)action.getValue(
                                    "InstrumentDescriptor" ),
                                (InstrumentSampleDescriptor)action.getValue(
                                    "InstrumentSampleDescriptor" ) );
                        }
                    };
                    action.putValue( "InstrumentManagerConnection", connection );
                    action.putValue( "InstrumentableDescriptor", instrumentableDescriptor );
                    action.putValue( "InstrumentDescriptor", instrumentDescriptor );
                    action.putValue( "InstrumentSampleDescriptor", descriptor );
    
                    JMenuItem item = new JMenuItem( action );
    
                    instrumentMenu.add( item );
                }
            }
        }
        catch ( AltrmiInvocationException e )
        {
            // Something went wrong, so close the connection.
            connection.close();
        }
    }

    /*
    private void rebuildProfilePointMenu( JMenu profilePointMenu,
                                          ProfilableDescriptor profilableDescriptor,
                                          ProfilePointDescriptor profilePointDescriptor )
    {
        profilePointMenu.removeAll();

        ProfileSampleDescriptor[] profileSampleDescriptors = profilePointDescriptor.getProfileSampleDescriptors();

        Comparator comp = new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                return ( (ProfileSampleDescriptor)o1 ).getDescription().
                    compareTo( ( (ProfileSampleDescriptor)o2 ).getDescription() );
            }

            public boolean equals( Object obj )
            {
                return false;
            }
        };
        Arrays.sort( profileSampleDescriptors, comp );

        for( int i = 0; i < profileSampleDescriptors.length; i++ )
        {
            ProfileSampleDescriptor profileSampleDescriptor = profileSampleDescriptors[ i ];

            String profileSampleName = profileSampleDescriptor.getDescription();

            Action action = new AbstractAction( profileSampleName )
            {
                public void actionPerformed( ActionEvent event )
                {
                    JMenuItem menu = (JMenuItem)event.getSource();
                    Action action = menu.getAction();

                    m_frame.openProfileSampleFrame(
                        (ProfilableDescriptor)action.getValue( "profilableDescriptor" ),
                        (ProfilePointDescriptor)action.getValue( "profilePointDescriptor" ),
                        (ProfileSampleDescriptor)action.getValue( "profileSampleDescriptor" ) );
                }
            };
            action.putValue( "profilableDescriptor", profilableDescriptor );
            action.putValue( "profilePointDescriptor", profilePointDescriptor );
            action.putValue( "profileSampleDescriptor", profileSampleDescriptor );

            JMenuItem item = new JMenuItem( action );

            profilePointMenu.add( item );
        }
    }
    */

    private JMenu buildOptionsMenu()
    {
        m_menuOptions = new JMenu( "Options" );
        m_menuOptions.setMnemonic( 'O' );

        // Show Unconfigured Profilables option
        m_menuItemShowUnconfigured =
            new JCheckBoxMenuItem( "Show Unconfigured Profilables", false );
        m_menuOptions.add( m_menuItemShowUnconfigured );

        return m_menuOptions;
    }

    private JMenu buildWindowMenu()
    {
        m_menuWindow = new JMenu( "Window" );
        m_menuWindow.setMnemonic( 'W' );

        m_menuWindow.addMenuListener( new MenuListener()
        {
            public void menuSelected( MenuEvent event )
            {
                rebuildWindowMenu();
            }

            public void menuDeselected( MenuEvent event )
            {
            }

            public void menuCanceled( MenuEvent event )
            {
            }
        } );

        return m_menuWindow;
    }

    private void rebuildWindowMenu()
    {
        m_menuWindow.removeAll();

        // Close All menu choice
        Action closeAllAction = new AbstractAction( "Close All" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.closeAllFrames();
            }
        };

        JMenuItem closeAll = new JMenuItem( closeAllAction );
        closeAll.setMnemonic( 'o' );
        m_menuWindow.add( closeAll );


        // List up all of the visible frames.
        JInternalFrame[] frames = m_frame.getDesktopPane().getAllFrames();

        if( frames.length > 0 )
        {
            m_menuWindow.addSeparator();
        }

        for( int i = 0; i < frames.length; i++ )
        {
            String label = ( i + 1 ) + " " + frames[ i ].getTitle();
            Action action = new AbstractAction( label )
            {
                public void actionPerformed( ActionEvent event )
                {
                    JMenuItem menu = (JMenuItem)event.getSource();
                    Action action = menu.getAction();

                    JInternalFrame frame = (JInternalFrame)action.getValue( "frame" );
                    try
                    {
                        if( frame.isIcon() )
                        {
                            // Restore the frame
                            frame.setIcon( false );
                        }
                        frame.setSelected( true );
                        m_frame.getDesktopPane().moveToFront( frame );
                    }
                    catch( java.beans.PropertyVetoException e )
                    {
                    }
                }
            };
            action.putValue( "frame", frames[ i ] );

            JMenuItem item = new JMenuItem( action );
            m_menuWindow.add( item );

            if( i < 10 )
            {
                item.setMnemonic( (char)( '1' + i ) );
            }
        }
    }
}
