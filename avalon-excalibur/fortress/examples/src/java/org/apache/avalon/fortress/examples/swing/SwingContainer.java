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
package org.apache.avalon.fortress.examples.swing;


/**
 * Simple Fortress based container containing a Swing implementation of Hello World.
 * This container creates a small Swing based GUI displaying a combobox of available
 * languages from the translator component.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 11:29:09 $
 */
public final class SwingContainer extends org.apache.avalon.fortress.impl.DefaultContainer
    implements org.apache.avalon.framework.activity.Startable, java.awt.event.ActionListener, Runnable
{
    // Component references
    private org.apache.avalon.fortress.examples.components.Translator m_translator;

    // GUI references
    private javax.swing.JFrame m_frame;
    private javax.swing.JLabel m_label;

    // Dictionary key
    private String m_key = "hello-world";

    /**
     * Initializes this component. Creates simple Swing GUI containing
     * available translations for the key 'hello-world'.
     *
     * @exception java.lang.Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        super.initialize();

        // obtain translator component
        m_translator = (org.apache.avalon.fortress.examples.components.Translator)m_serviceManager.lookup( org.apache.avalon.fortress.examples.components.Translator.ROLE );

        // create combo box
        javax.swing.JComboBox cb = new javax.swing.JComboBox( m_translator.getSupportedLanguages( m_key ) );
        cb.addActionListener( this );

        // create label
        m_label = new javax.swing.JLabel( "Select your language" );
        m_label.setPreferredSize( new java.awt.Dimension( 150, 30 ) );

        // create panel holding box and label
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add( cb );
        panel.add( m_label );

        // create main frame
        m_frame = new javax.swing.JFrame( "Hello World!" );
        m_frame.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE );
        m_frame.setContentPane( panel );
        m_frame.pack();

        // all done
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Initialized" );
        }
    }

    /**
     * Starts the component, makes GUI visible, ready for use.
     */
    public void start()
    {
        m_frame.setVisible( true );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "GUI Activated" );
        }
    }

    public void run()
    {
        while ( m_frame.isVisible() )
        {
            try
            {
                Thread.sleep(1000);
            }
            catch( InterruptedException ie )
            {
                m_frame.setVisible(false);
            }
        }
    }

    /**
     * Stops component, make GUI invisible, ready for decomissioning.
     */
    public void stop()
    {
        m_frame.setVisible( false );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "GUI Disactivated" );
        }
    }

    /**
     * Method called when the user changes the selected item in the
     * combobox.
     *
     * @param evt an <code>ActionEvent</code> instance
     */
    public void actionPerformed( java.awt.event.ActionEvent evt )
    {
        javax.swing.JComboBox cb = (javax.swing.JComboBox)evt.getSource();
        String selected = (String)cb.getSelectedItem();

        m_label.setText( m_translator.getTranslation( m_key, selected ) );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Language changed to " + selected );
        }
    }

    /**
     * Cleans up references to retrieved components.
     */
    public void dispose()
    {
        if( m_translator != null )
            m_serviceManager.release( m_translator );

        m_frame.dispose();

        super.dispose();
    }
}

