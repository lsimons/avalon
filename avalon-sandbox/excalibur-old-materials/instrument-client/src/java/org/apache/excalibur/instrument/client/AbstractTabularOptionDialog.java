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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Creates a dialog which displays a table of labeled components to the user.
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/03/22 12:46:36 $
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

