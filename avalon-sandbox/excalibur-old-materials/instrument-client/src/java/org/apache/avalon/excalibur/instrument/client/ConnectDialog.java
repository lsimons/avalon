/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/28 03:15:40 $
 * @since 4.1
 */
class ConnectDialog
    extends AbstractOptionDialog
{
    private JTextField m_hostField;
    private String m_host;
    private JTextField m_portField;
    private int m_port;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new ConnectDialog.
     *
     * @param frame Frame which owns the dialog.
     */
    ConnectDialog( InstrumentClientFrame frame )
    {
        super( frame, AbstractOptionDialog.BUTTON_OK | AbstractOptionDialog.BUTTON_CANCEL );
    }
    
    /*---------------------------------------------------------------
     * AbstractOptionDialog Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the message to show at the top of the dialog.
     *
     * @return The text of the message.
     */
    protected String getMessage()
    {
        return "Please enter the host and port of the InstrumentManager to connect to.";
    }
    
    /**
     * Returns the main panel which makes up the guts of the dialog.
     *
     * @return The main panel.
     */
    protected JPanel getMainPanel()
    {
        m_hostField = new JTextField();
        m_hostField.setColumns( 20 );
        m_portField = new JTextField();
        m_portField.setColumns( 6 );
        
        JPanel panel = new JPanel();
        
        panel.setLayout( new FlowLayout() );
        Box mainBox = Box.createVerticalBox();
        
        Box hostBox = Box.createHorizontalBox();
        hostBox.add( new JLabel( "Host:" ) );
        hostBox.add( Box.createHorizontalStrut( 5 ) );
        hostBox.add( m_hostField );
        mainBox.add( hostBox );
        mainBox.add( Box.createVerticalStrut( 5 ) );
        
        Box portBox = Box.createHorizontalBox();
        portBox.add( new JLabel( "Post:" ) );
        portBox.add( Box.createHorizontalStrut( 5 ) );
        portBox.add( m_portField );
        mainBox.add( portBox );
        
        panel.add( mainBox );
        
        return panel;
    }
    
    /**
     * Goes through and validates the fields in the dialog.
     *
     * @return True if the fields were Ok.
     */
    protected boolean validateFields()
    {
        // Check the host.
        String host = m_hostField.getText().trim();
        if ( host.length() == 0 )
        {
            JOptionPane.showMessageDialog( this, "Please enter a valid host name or IP address.",
                "Invalid host", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        m_host = host;
        
        // Check the port.
        boolean portOk = true;
        int port = 0;
        try
        {
            port = Integer.parseInt( m_portField.getText().trim() );
        }
        catch ( NumberFormatException e )
        {
            portOk = false;
        }
        if ( ( port < 0 ) || ( port > 65535 ) )
        {
            portOk = false;
        }
        if ( !portOk )
        {
            JOptionPane.showMessageDialog( this, "Please enter a valid port. (1-65535)",
                "Invalid port", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        m_port = port;
        
        return true;
    }
        
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Sets the initial host to be shown in the host TextField.
     *
     * @param host The initial host.
     */
    void setHost( String host )
    {
        m_host = host;
        m_hostField.setText( host );
    }
    
    /**
     * Returns the host set in the dialog.
     *
     * @return The host.
     */
    String getHost()
    {
        return m_host;
    }
    
    /**
     * Sets the initial port to be shown in the port TextField.
     *
     * @param port The initial port.
     */
    void setPort( int port )
    {
        m_port = port;
        m_portField.setText( Integer.toString( port ) );
    }
    
    /**
     * Returns the port set in the dialog.
     *
     * @return The port.
     */
    int getPort()
    {
        return m_port;
    }
}

