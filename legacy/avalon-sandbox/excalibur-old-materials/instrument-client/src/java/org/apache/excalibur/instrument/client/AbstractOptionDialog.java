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
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/03/29 18:53:25 $
 * @since 4.1
 */
public abstract class AbstractOptionDialog
    extends JDialog
{
    public static final int BUTTON_OK = 1;
    public static final int BUTTON_CANCEL = 2;
    
    protected int m_action = BUTTON_CANCEL;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new AbstractOptionDialog.
     *
     * @param frame Frame which owns the dialog.
     * @param title Title for the dialog.
     * @param buttons List of buttons to display.
     */
    protected AbstractOptionDialog( JFrame frame, String title, int buttons )
    {
        super( frame, title, true );
        
        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setLayout( new BorderLayout() );
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        
        JPanel backPane = new JPanel();
        backPane.setLayout( new BorderLayout() );
        backPane.setBorder(
            new CompoundBorder(
                new EmptyBorder( 0, 0, 5, 0 ),
                new CompoundBorder(
                    new EtchedBorder( EtchedBorder.LOWERED ),
                    new EmptyBorder( 5, 5, 5, 5 )
                )
            )
        );
        contentPane.add( backPane, BorderLayout.CENTER );
        
        // Build the message
        backPane.add( new JLabel( getMessage(), SwingConstants.LEFT ), BorderLayout.NORTH );
        
        // Build the main panel
        JPanel mainPanel = getMainPanel();
        mainPanel.setBorder( new EmptyBorder( 5, 0, 0, 0 ) );
        backPane.add( mainPanel, BorderLayout.CENTER );
        
        
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
        
        // Make the dialog a fixed size.
        setResizable( false );
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

