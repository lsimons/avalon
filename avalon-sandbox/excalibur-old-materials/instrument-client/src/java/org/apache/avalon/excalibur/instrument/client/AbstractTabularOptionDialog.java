/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Creates a dialog which displays a table of labeled components to the user.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/28 17:04:15 $
 * @since 4.1
 */
public abstract class AbstractTabularOptionDialog
    extends AbstractOptionDialog
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractTabularOptionDialog.
     *
     * @param frame Frame which owns the dialog.
     * @param title Title for the dialog.
     * @param buttons List of buttons to display.
     */
    protected AbstractTabularOptionDialog( JFrame frame, String title, int buttons )
    {
        super( frame, title, buttons );
    }
    
    /*---------------------------------------------------------------
     * AbstractOptionDialog Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the main panel which makes up the guts of the dialog.
     *  This implementaton builds a table of labeled components using
     *  arrays returned by getMainPanelLabels() and getMainPanelComponents();
     *
     * @return The main panel.
     */
    protected JPanel getMainPanel()
    {
        String[] labels = getMainPanelLabels();
        Component[] components = getMainPanelComponents();
        
        JPanel panel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setLayout( gbl );
        
        for ( int i = 0; i < labels.length; i++ )
        {
            addRow( panel, labels[i], components[i], gbl, gbc );
        }
        
        return panel;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns an array of labels to use for the components returned from
     *  getMainPanelComponents().
     *
     * @returns An array of labels.
     */
    protected abstract String[] getMainPanelLabels();
    
    /**
     * Returns an array of components to show in the main panel of the dialog.
     *
     * @returns An array of components.
     */
    protected abstract Component[] getMainPanelComponents();
    
    /**
     * Adds a row to the panel consisting of a label and component, separated by
     *  a 5 pixel spacer and followed by a 5 pixel high row between this and the
     *  next row.
     *
     * @param panel Panel to which the row will be added.
     * @param label Text of the label for the component.
     * @param component Component which makes up the row.
     * @param gbl GridBagLayout which must have been set as the layour of the
     *            panel.
     * @param gbc GridBagConstraints to use when laying out the row.
     */
    private void addRow( JPanel panel,
                         String label,
                         Component component,
                         GridBagLayout gbl,
                         GridBagConstraints gbc )
    {
        JLabel jLabel = new JLabel( label );
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints( jLabel, gbc );
        panel.add( jLabel );
        
        // Add a 5 pixel high spacer
        Component spacer = Box.createRigidArea( new Dimension( 5, 5 ) );
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints( spacer, gbc );
        panel.add( spacer );
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints( component, gbc );
        panel.add( component );
        
        // Add a 5 pixel high spacer
        spacer = Box.createRigidArea( new Dimension( 5, 5 ) );
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints( spacer, gbc );
        panel.add( spacer );
    }
}

