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
