/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class defines the status bar at the bottom of the main frame.
 *  It is used to display information to the user.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/10/25 19:07:58 $
 * @since 4.1
 */
class StatusBar extends JPanel
{
    private JLabel m_statusLabel;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    StatusBar()
    {
        setLayout( new BorderLayout() );
        m_statusLabel = new JLabel( " " );
        add( m_statusLabel, BorderLayout.CENTER );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    void setStatusMessage( String message )
    {
        // If the message is of 0 length, then the status bar will collapse.
        if ( ( message == null ) || ( message.length() < 1 ) )
        {
            message = " ";
        }
        
        if ( !message.equals( m_statusLabel.getText() ) )
        {
            m_statusLabel.setText( message );
            m_statusLabel.invalidate();
            validate();
        }
    }
}
