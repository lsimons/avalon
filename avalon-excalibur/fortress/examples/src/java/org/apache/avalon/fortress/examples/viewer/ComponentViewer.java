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
package org.apache.avalon.fortress.examples.viewer;


/**
 * Simple Fortress container containing a Swing based viewer for performing
 * lookups on registered components.
 *
 * <p>
 * The intention of the viewer is to allow you to perform a lookup of a component
 * manually, (currently) so you can check the see the effect of lazy vs startup
 * initialization.
 * </p>
 *
 * <p>
 * REVISIT: add a text component which tracks the log file to make it easier to
 * see a component being initialized upon first lookup.
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/03/22 12:46:32 $
 */
public final class ComponentViewer
    extends org.apache.avalon.fortress.impl.DefaultContainer
    implements org.apache.avalon.framework.activity.Startable, java.awt.event.ActionListener, Runnable
{
    // GUI references
    private javax.swing.JFrame m_frame;
    private javax.swing.JComboBox m_components;

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

        // create main frame
        m_frame = new javax.swing.JFrame( "Component Viewer" );
        m_frame.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE );

        m_components = new javax.swing.JComboBox( getRoles() );

        javax.swing.JButton button = new javax.swing.JButton( "Lookup!" );
        button.addActionListener( this );

        /*
        // can we output the log data into this text area somehow ?
        JTextArea logData = new JTextArea();
        logData.setEditable( false );
        */

        javax.swing.JPanel selectionPanel = new javax.swing.JPanel();
        selectionPanel.add( m_components );
        selectionPanel.add( button );

        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout( new javax.swing.BoxLayout( mainPanel, javax.swing.BoxLayout.Y_AXIS ) );
        mainPanel.add( selectionPanel );

        m_frame.setContentPane( mainPanel );
        m_frame.pack();

        // all done
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Initialized" );
        }
    }

    /**
     * Helper method to obtain a list of all Roles registered with this container
     *
     * @return an array of roles
     */
    private Object[] getRoles()
    {
        java.util.Set keys = m_mapper.keySet();
        Object[] roles = new Object[ keys.size() ];
        int j = 0;

        for( java.util.Iterator i = keys.iterator(); i.hasNext(); )
        {
            roles[ j++ ] = i.next();
        }

        return roles;
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
     * Handles the <i>lookup</i> event. Finds out which component
     * was selected and performs a lookup and release on this component.
     *
     * @param evt an <code>ActionEvent</code> value
     */
    public void actionPerformed( java.awt.event.ActionEvent evt )
    {
        String selected = (String)m_components.getSelectedItem();

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Looking up component " + selected );
        }

        Object component = null;

        try
        {
            component = m_serviceManager.lookup( selected );
        }
        catch( org.apache.avalon.framework.service.ServiceException e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error looking up component: " + e.getKey(), e );
            }
        }
        finally
        {
            if( component != null ) m_serviceManager.release( component );
        }
    }

    public void run()
    {
        while (m_frame.isVisible())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ie)
            {
                m_frame.setVisible(false);
            }
        }
    }
}

