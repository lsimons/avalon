/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/28 03:15:40 $
 * @since 4.1
 */
public abstract class AbstractOptionDialog
    extends JDialog
{
    public static final int BUTTON_OK = 1;
    public static final int BUTTON_CANCEL = 2;
    
    private int m_action = BUTTON_CANCEL;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractOptionDialog.
     *
     * @param frame Frame which owns the dialog.
     * @param buttons List of buttons to display.
     */
    protected AbstractOptionDialog( JFrame frame, int buttons )
    {
        super( frame, true );
        
        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setLayout( new BorderLayout() );
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        
        // Build the message
        contentPane.add( new JLabel( getMessage(), SwingConstants.LEFT ), BorderLayout.NORTH );
        
        // Build the main panel
        JPanel mainPanel = getMainPanel();
        mainPanel.setBorder( new EmptyBorder( 5, 0, 5, 0 ) );
        contentPane.add( mainPanel, BorderLayout.CENTER );
        
        
        // Build the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new FlowLayout( FlowLayout.CENTER ) );
        Box buttonBox = Box.createHorizontalBox();
        if ( ( buttons & BUTTON_OK ) != 0 )
        {
            Action action = new AbstractAction( "OK" )
            {
                public void actionPerformed( ActionEvent event )
                {
                    if ( validateFields() )
                    {
                        m_action = BUTTON_OK;
                        AbstractOptionDialog.this.hide();
                    }
                }
            };
            JButton button = new JButton( action );
            buttonBox.add( button );
            buttonBox.add( Box.createHorizontalStrut( 5 ) );
        }
        if ( ( buttons & BUTTON_CANCEL ) != 0 )
        {
            Action action = new AbstractAction( "Cancel" )
            {
                public void actionPerformed( ActionEvent event )
                {
                    m_action = BUTTON_CANCEL;
                    AbstractOptionDialog.this.hide();
                }
            };
            JButton button = new JButton( action );
            buttonBox.add( button );
            buttonBox.add( Box.createHorizontalStrut( 5 ) );
        }
        buttonPanel.add( buttonBox );
        contentPane.add( buttonPanel, BorderLayout.SOUTH );
        
        pack();
        
        // Position the dialog.
        Point frameLocation = frame.getLocation();
        Dimension frameSize = frame.getSize();
        Dimension size = getSize();
        
        setLocation(
            (int)( frameLocation.getX() + (frameSize.getWidth() - size.getWidth() ) / 2 ),
            (int)( frameLocation.getY() + (frameSize.getHeight() - size.getHeight() ) / 2 ) );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the message to show at the top of the dialog.
     *
     * @return The text of the message.
     */
    protected abstract String getMessage();
    
    /**
     * Returns the main panel which makes up the guts of the dialog.
     *
     * @return The main panel.
     */
    protected abstract JPanel getMainPanel();
    
    /**
     * Goes through and validates the fields in the dialog.
     *
     * @return True if the fields were Ok.
     */
    protected boolean validateFields()
    {
        return true;
    }
    
    /**
     * Returns the button which the user selected.
     */
    public int getAction()
    {
        return m_action;
    }
}

