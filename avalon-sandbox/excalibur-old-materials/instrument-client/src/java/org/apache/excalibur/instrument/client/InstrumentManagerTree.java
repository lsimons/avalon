/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

import org.apache.altrmi.common.InvocationException;

class InstrumentManagerTree
    extends JComponent
{
    private final InstrumentManagerConnection m_connection;

    private final TreeModel m_treeModel;
    private final JTree m_tree;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentManagerTree( InstrumentManagerConnection connection )
    {
        m_connection = connection;

        m_treeModel = m_connection.getTreeModel();


        m_tree = new JTree( m_treeModel );
        //m_tree.setEditable( true ); // Makes it possible to edit the node names in line.
        m_tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        m_tree.setRootVisible( false );     // Hide the root node.
        m_tree.setShowsRootHandles( true ); // The root's children become "roots"
        m_tree.setCellRenderer( new InstrumentManagerTreeCellRenderer() );
        m_tree.putClientProperty( "JTree.lineStyle", "Angled" );

        m_tree.addMouseListener( new MouseAdapter()
        {
            public void mouseClicked( MouseEvent event )
            {
                if ( event.isPopupTrigger() )
                {
                    int row = m_tree.getRowForLocation( event.getX(), event.getY() );
                    if ( row >= 0 )
                    {
                        showNodePopup( row, event.getX(), event.getY() );
                    }
                }
                if ( event.getClickCount() == 2 )
                {
                    int row = m_tree.getRowForLocation( event.getX(), event.getY() );
                    if ( row >= 0 )
                    {
                        nodeSelected( row );
                    }
                }
            }
            public void mousePressed( MouseEvent event )
            {
                if ( event.isPopupTrigger() )
                {
                    int row = m_tree.getRowForLocation( event.getX(), event.getY() );
                    if ( row >= 0 )
                    {
                        showNodePopup( row, event.getX(), event.getY() );
                    }
                }
            }
            public void mouseReleased( MouseEvent event )
            {
                if ( event.isPopupTrigger() )
                {
                    int row = m_tree.getRowForLocation( event.getX(), event.getY() );
                    if ( row >= 0 )
                    {
                        showNodePopup( row, event.getX(), event.getY() );
                    }
                }
            }
        });

        // Register the tree to work with tooltips.
        ToolTipManager.sharedInstance().registerComponent( m_tree );

        JScrollPane scrollPane = new JScrollPane( m_tree );

        setLayout( new BorderLayout() );
        add( scrollPane, BorderLayout.CENTER );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    void dispose()
    {
        ToolTipManager.sharedInstance().unregisterComponent( m_tree );
        m_tree.setModel( null );
    }

    private void nodeSelected( int row )
    {
        TreePath treePath = m_tree.getPathForRow( row );
        if ( treePath.getLastPathComponent() instanceof DefaultMutableTreeNode )
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            if ( ( treeNode.isLeaf() ) && ( treeNode.getUserObject() instanceof NodeData ) )
            {
                NodeData nodeData = (NodeData)treeNode.getUserObject();
                nodeData.select();
            }
        }
    }

    private void showNodePopup( int row, int mouseX, int mouseY )
    {
        TreePath treePath = m_tree.getPathForRow( row );

        if ( treePath.getLastPathComponent() instanceof DefaultMutableTreeNode )
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            if ( treeNode.getUserObject() instanceof NodeData )
            {
                NodeData nodeData = (NodeData)treeNode.getUserObject();
                JPopupMenu popup = nodeData.getPopupMenu();
                if ( popup != null )
                {
                    // Need to figure out where to display the popup.
                    Rectangle bounds = m_tree.getRowBounds( row );

                    /*
                    // Anchor the popup menu at the location of the node.
                    int x = bounds.x + 24;
                    int y = bounds.y + bounds.height;
                    */

                    // Anchor the popup menu where the user clicked.
                    int x = mouseX;
                    int y = mouseY;

                    popup.show( m_tree, x, y );
                }
            }
        }
    }
}