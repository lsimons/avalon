/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
//import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentSampleDescriptor;

class InstrumentManagerTreeCellRenderer
    extends DefaultTreeCellRenderer
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentManagerTreeCellRenderer()
    {
    }
    
    /*---------------------------------------------------------------
     * DefaultTreeCellRenderer Methods
     *-------------------------------------------------------------*/
    public Component getTreeCellRendererComponent( JTree tree,
                                                   Object value,
                                                   boolean sel,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
        
        /*
        System.out.println("InstrumentManagerTreeCellRenderer.getTreeCellRendererComponent(tree, " +
                            "value=" + value + ", sel=" + sel + ", expanded=" + expanded + ", leaf=" +
                            leaf + ", row=" + row + ", focus=" + hasFocus + ") " +
                            value.getClass().getName() );
        */
        if ( value instanceof DefaultMutableTreeNode )
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
            Object userObject = treeNode.getUserObject();
            
            if ( userObject instanceof NodeData )
            {
                NodeData nodeData = (NodeData)userObject;
                setIcon( nodeData.getIcon() );
                setToolTipText( nodeData.getToolTipText() );
            }
        }
        
        return this;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}