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

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:48 $
 * @since 4.1
 */
class ConnectDialog
    extends AbstractTabularOptionDialog
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
        super( frame, "Connect to Remote Instrument Manager",
            AbstractOptionDialog.BUTTON_OK | AbstractOptionDialog.BUTTON_CANCEL );
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
     * AbstractTabularOptionDialog Methods
     *-------------------------------------------------------------*/
    /**
     * Returns an array of labels to use for the components returned from
     *  getMainPanelComponents().
     *
     * @returns An array of labels.
     */
    protected String[] getMainPanelLabels()
    {
        return new String[]
        {
            "Host:",
            "Port:"
        };
    }
    
    /**
     * Returns an array of components to show in the main panel of the dialog.
     *
     * @returns An array of components.
     */
    protected Component[] getMainPanelComponents()
    {
        m_hostField = new JTextField();
        m_hostField.setColumns( 20 );
        m_portField = new JTextField();
        m_portField.setColumns( 6 );
        
        return new Component[]
        {
            m_hostField,
            m_portField
        };
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

