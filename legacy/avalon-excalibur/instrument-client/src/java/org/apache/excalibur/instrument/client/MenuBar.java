/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.client;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:23 $
 * @since 4.1
 */
public class MenuBar
    extends JMenuBar
{
    protected InstrumentClientFrame m_frame;
    //private ProfilerManager m_profilerManager;

    private JMenu m_menuFile;

    private JMenu m_menuInstrumentManagers;

    private JMenu m_menuOptions;
    private JCheckBoxMenuItem m_menuItemShowUnconfigured;

    private JMenu m_menuWindow;

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
        m_menuFile = (JMenu)new LargeMenu( "File" );
        m_menuFile.setMnemonic( 'F' );


        // Clear
        Action newAction = new AbstractAction( "New" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.fileNew();
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
                m_frame.fileOpen();
            }
        };
        JMenuItem open = new JMenuItem( openAction );
        open.setMnemonic( 'O' );
        m_menuFile.add( open );


        // Seperator
        m_menuFile.addSeparator();


        // Save
        Action saveAction = new AbstractAction( "Save" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.fileSave();
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
                m_frame.fileSaveAs();
            }
        };
        JMenuItem saveAs = new JMenuItem( saveAsAction );
        saveAs.setMnemonic( 'A' );
        m_menuFile.add( saveAs );


        // Seperator
        m_menuFile.addSeparator();


        // Exit
        Action exitAction = new AbstractAction( "Exit" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.fileExit();
            }
        };
        JMenuItem exit = new JMenuItem( exitAction );
        exit.setMnemonic( 'X' );
        m_menuFile.add( exit );

        return m_menuFile;
    }

    private JMenu buildInstrumentManagerMenu()
    {
        m_menuInstrumentManagers = new LargeMenu( "Instrument Managers" );
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

    protected void rebuildInstrumentManagersMenu()
    {
        m_menuInstrumentManagers.removeAll();

        // Add Connect menu item
        Action connectAction = new AbstractAction( "Connect to Instrument Manager..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                // For now, skip the dialog
                m_frame.showConnectDialog();
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

                JMenu menu = new LargeMenu( action );

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

    protected void rebuildInstrumentManagerMenu( JMenu managerMenu,
                                               InstrumentManagerConnection connection )
    {
        managerMenu.removeAll();

        boolean showAll = m_menuItemShowUnconfigured.getState();

        // Delete
        Action deleteAction = new AbstractAction( "Delete" )
        {
            public void actionPerformed( ActionEvent event )
            {
                JMenuItem item = (JMenuItem)event.getSource();
                Action action = item.getAction();

                InstrumentManagerConnection connection =
                    (InstrumentManagerConnection)action.getValue( "InstrumentManagerConnection" );

                connection.delete();
            }
        };
        deleteAction.putValue( "InstrumentManagerConnection", connection );

        JMenuItem deleteItem = new JMenuItem( deleteAction );
        deleteItem.setMnemonic( 'I' );
        managerMenu.add( deleteItem );


        // Instrument menu items
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

                        JMenu menu = new LargeMenu( action );

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
        catch ( org.apache.altrmi.client.InvocationException e )
        {
            // Something went wrong, so close the connection.
            connection.close();
        }
    }

    protected void rebuildInstrumentableMenu( JMenu instrumentableMenu,
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

                    JMenu menu = new LargeMenu( action );

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
        catch ( org.apache.altrmi.client.InvocationException e )
        {
            // Something went wrong, so close the connection.
            connection.close();
        }
    }

    protected void rebuildInstrumentMenu( JMenu instrumentMenu,
                                        InstrumentManagerConnection connection,
                                        InstrumentableDescriptor instrumentableDescriptor,
                                        InstrumentDescriptor instrumentDescriptor )
    {
        instrumentMenu.removeAll();

        boolean showAll = m_menuItemShowUnconfigured.getState();

        // Create Sample
        Action createAction = new AbstractAction( "Create Sample..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                /*  Not implemented.

                JMenuItem item = (JMenuItem)event.getSource();
                Action action = item.getAction();

                m_frame.instrumentCreateSample(
                    (InstrumentManagerConnection)action.getValue( "InstrumentManagerConnection" ),
                    (InstrumentDescriptor)action.getValue( "InstrumentDescriptor" ) );
                */
            }
        };
        createAction.putValue( "InstrumentManagerConnection", connection );
        createAction.putValue( "InstrumentableDescriptor", instrumentableDescriptor );
        createAction.putValue( "InstrumentDescriptor", instrumentDescriptor );

        JMenuItem createItem = new JMenuItem( createAction );
        createItem.setMnemonic( 'C' );
        instrumentMenu.add( createItem );

        try
        {
            InstrumentSampleDescriptor[] descriptors =
                instrumentDescriptor.getInstrumentSampleDescriptors();

            if ( descriptors.length > 0 )
            {
                instrumentMenu.addSeparator();

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
                                /* Not implemented

                                JMenuItem menu = (JMenuItem)event.getSource();
                                Action action = menu.getAction();

                                m_frame.openInstrumentSampleFrame(
                                    (InstrumentManagerConnection)action.getValue(
                                        "InstrumentManagerConnection" ),
                                    (InstrumentSampleDescriptor)action.getValue(
                                        "InstrumentSampleDescriptor" ) );
                                */
                            }
                        };
                        action.putValue( "InstrumentManagerConnection", connection );
                        action.putValue( "InstrumentSampleDescriptor", descriptor );

                        JMenuItem item = new JMenuItem( action );

                        instrumentMenu.add( item );
                    }
                }
            }
        }
        catch ( org.apache.altrmi.client.InvocationException e )
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
        m_menuOptions = new LargeMenu( "Options" );
        m_menuOptions.setMnemonic( 'O' );

        // Show Unconfigured Profilables option
        m_menuItemShowUnconfigured =
            new JCheckBoxMenuItem( "Show Unconfigured Profilables", false );
        m_menuOptions.add( m_menuItemShowUnconfigured );

        return m_menuOptions;
    }

    private JMenu buildWindowMenu()
    {
        m_menuWindow = new LargeMenu( "Window" );
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

    protected void rebuildWindowMenu()
    {
        m_menuWindow.removeAll();

        // Tile window menu choice
        Action tileFramesAction = new AbstractAction( "Tile frames" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.tileFrames();
            }
        };

        JMenuItem tileFrames = new JMenuItem( tileFramesAction );
        tileFrames.setMnemonic( 't' );
        m_menuWindow.add( tileFrames );


        // Tile window vertically menu choice
        Action tileFramesVAction = new AbstractAction( "Tile frames vertically" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.tileFramesV();
            }
        };

        JMenuItem tileFramesV = new JMenuItem( tileFramesVAction );
        tileFramesV.setMnemonic( 'v' );
        m_menuWindow.add( tileFramesV );

        // Tile window horizontally menu choice
        Action tileFramesHAction = new AbstractAction( "Tile frames horizontally" )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_frame.tileFramesH();
            }
        };

        JMenuItem tileFramesH = new JMenuItem( tileFramesHAction );
        tileFramesH.setMnemonic( 'h' );
        m_menuWindow.add( tileFramesH );

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
