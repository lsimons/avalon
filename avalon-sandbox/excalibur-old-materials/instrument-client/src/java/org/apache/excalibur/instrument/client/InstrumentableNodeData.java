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

import javax.swing.ImageIcon;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;

class InstrumentableNodeData
    extends NodeData
{
    /** An Instrumentable which exists because of configuration. */
    private static final ImageIcon m_iconInstrumentableConf;
    
    /** An Instrumentable which exists because of registration. */
    private static final ImageIcon m_iconInstrumentableReg;
    
    /** An Instrumentable which exists because of registration and
     *   configuration. */
    private static final ImageIcon m_iconInstrumentableRegConf;
    
    /** An Instrumentable which exists because it was loaded from the state
     *   file but is no longer used. */
    private static final ImageIcon m_iconInstrumentableOld;
    
    private InstrumentableDescriptor m_descriptor;
    private InstrumentManagerConnection m_connection;
    
    private boolean m_configured;
    private boolean m_registered;
    
    /*---------------------------------------------------------------
     * Class Initializer
     *-------------------------------------------------------------*/
    static
    {
        // Load the images.
        ClassLoader cl = InstrumentManagerTreeCellRenderer.class.getClassLoader();
        m_iconInstrumentableConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrumentable_conf.gif") );
        m_iconInstrumentableReg =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrumentable_reg.gif") );
        m_iconInstrumentableRegConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrumentable_reg_conf.gif") );
        m_iconInstrumentableOld =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrumentable_old.gif") );
    }
        
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentableNodeData( InstrumentableDescriptor descriptor,
                            InstrumentManagerConnection connection )
    {
        m_descriptor = descriptor;
        m_connection = connection;
        
        update();
    }
    
    /*---------------------------------------------------------------
     * NodeData Methods
     *-------------------------------------------------------------*/
    /**
     * Get the icon to display for the node.
     *
     * @return the icon to display for the node.
     */
    ImageIcon getIcon()
    {
        ImageIcon icon;
        if ( isConfigured() && isRegistered() )
        {
            icon = m_iconInstrumentableRegConf;
        }
        else if ( isConfigured() )
        {
            icon = m_iconInstrumentableConf;
        }
        else if ( isRegistered() )
        {
            icon = m_iconInstrumentableReg;
        }
        else
        {
            icon = m_iconInstrumentableOld;
        }
        
        return icon;
    }
    
    /**
     * Return the text to use for a tool tip on this node.
     *
     * @return Tool Tip text.  May be null, for no tool tip.
     */
    String getToolTipText()
    {
        String text;
        if ( isConfigured() && isRegistered() )
        {
            text = "Registered and Configured Instrumentable";
        }
        else if ( isConfigured() )
        {
            text = "Configured but unregistered Instrumentable";
        }
        else if ( isRegistered() )
        {
            text = "Registered Instrumentable";
        }
        else
        {
            text = "Old Instrumentable loaded from state file";
        }
        
        return text;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    InstrumentableDescriptor getDescriptor()
    {
        return m_descriptor;
    }
    
    boolean isConfigured()
    {
        return m_configured;
    }
    
    boolean isRegistered()
    {
        return m_registered;
    }
    
    /**
     * Collect latest property values from the server.  Each call is remote so this
     *  allows us to use cached values locally to speed things up.
     */
    boolean update()
    {
        boolean changed = false;
        changed |= update( m_descriptor.getName(), m_descriptor.getDescription(),
            m_descriptor.getStateVersion() );
        
        boolean newConfigured = m_descriptor.isConfigured();
        if ( newConfigured != m_configured )
        {
            changed = true;
            m_configured = newConfigured;
        }
        
        boolean newRegistered = m_descriptor.isRegistered();
        if ( newRegistered != m_registered )
        {
            changed = true;
            m_registered = newRegistered;
        }
        
        return changed;
    }

}