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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.interfaces.InstrumentManagerClient;

class InstrumentNodeData
    extends NodeData
{
    /** A counter Instrument which exists because of configuration. */
    private static final ImageIcon m_iconInstrumentCtrConf;
    
    /** A counter Instrument which exists because of registration. */
    private static final ImageIcon m_iconInstrumentCtrReg;
    
    /** A counter Instrument which exists because of registration and
     *   configuration. */
    private static final ImageIcon m_iconInstrumentCtrRegConf;
    
    /** A counter Instrument which exists because it was loaded from the state
     *   file but is no longer used. */
    private static final ImageIcon m_iconInstrumentCtrOld;
    
    /** A value Instrument which exists because of configuration. */
    private static final ImageIcon m_iconInstrumentValConf;
    
    /** A value Instrument which exists because of registration. */
    private static final ImageIcon m_iconInstrumentValReg;
    
    /** A value Instrument which exists because of registration and
     *   configuration. */
    private static final ImageIcon m_iconInstrumentValRegConf;
    
    /** A value Instrument which exists because it was loaded from the state
     *   file but is no longer used. */
    private static final ImageIcon m_iconInstrumentValOld;
    
    private InstrumentDescriptor m_descriptor;
    private InstrumentManagerConnection m_connection;
    
    private boolean m_configured;
    private boolean m_registered;
    private int m_type;
    
    /*---------------------------------------------------------------
     * Class Initializer
     *-------------------------------------------------------------*/
    static
    {
        // Load the images.
        ClassLoader cl = InstrumentManagerTreeCellRenderer.class.getClassLoader();
        m_iconInstrumentCtrConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_ctr_conf.gif") );
        m_iconInstrumentCtrReg =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_ctr_reg.gif") );
        m_iconInstrumentCtrRegConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_ctr_reg_conf.gif") );
        m_iconInstrumentCtrOld =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_ctr_old.gif") );
        
        m_iconInstrumentValConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_val_conf.gif") );
        m_iconInstrumentValReg =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_val_reg.gif") );
        m_iconInstrumentValRegConf =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_val_reg_conf.gif") );
        m_iconInstrumentValOld =
            new ImageIcon( cl.getResource( MEDIA_PATH + "instrument_val_old.gif") );
    }
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    InstrumentNodeData( InstrumentDescriptor descriptor,
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
        switch ( getType() )
        {
        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
            if ( isConfigured() && isRegistered() )
            {
                icon = m_iconInstrumentCtrRegConf;
            }
            else if ( isConfigured() )
            {
                icon = m_iconInstrumentCtrConf;
            }
            else if ( isRegistered() )
            {
                icon = m_iconInstrumentCtrReg;
            }
            else
            {
                icon = m_iconInstrumentCtrOld;
            }
            break;
            
        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
            if ( isConfigured() && isRegistered() )
            {
                icon = m_iconInstrumentValRegConf;
            }
            else if ( isConfigured() )
            {
                icon = m_iconInstrumentValConf;
            }
            else if ( isRegistered() )
            {
                icon = m_iconInstrumentValReg;
            }
            else
            {
                icon = m_iconInstrumentValOld;
            }
            break;
            
        default:
            throw new IllegalStateException( "Encountered an unknown instrument type: " +
                getType() );
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
        switch ( getType() )
        {
        case InstrumentManagerClient.INSTRUMENT_TYPE_COUNTER:
            if ( isConfigured() && isRegistered() )
            {
                text = "Registered and Configured Counter Instrument";
            }
            else if ( isConfigured() )
            {
                text = "Configured but unregistered Counter Instrument";
            }
            else if ( isRegistered() )
            {
                text = "Registered Counter Instrument";
            }
            else
            {
                text = "Old Counter Instrument loaded from state file";
            }
            break;
            
        case InstrumentManagerClient.INSTRUMENT_TYPE_VALUE:
            if ( isConfigured() && isRegistered() )
            {
                text = "Registered and Configured Value Instrument";
            }
            else if ( isConfigured() )
            {
                text = "Configured but unregistered Value Instrument";
            }
            else if ( isRegistered() )
            {
                text = "Registered Value Instrument";
            }
            else
            {
                text = "Old Value Instrument loaded from state file";
            }
            break;
            
        default:
            throw new IllegalStateException( "Encountered an unknown instrument type: " +
                getType() );
        }
        
        return text;
    }
    
    /**
     * Returns an array of any menu items which will be displayed both
     *  in a popup menu and in the menus.
     *
     * @return An array of the common menu items.
     */
    public JMenuItem[] getCommonMenuItems()
    {
        JMenuItem[] menuItems = new JMenuItem[1];
        
        Action createSampleAction = new AbstractAction( "Create Sample..." )
        {
            public void actionPerformed( ActionEvent event )
            {
                m_connection.instrumentCreateSample( InstrumentNodeData.this.getDescriptor() );
            }
        };
        JMenuItem createSampleItem = new JMenuItem( createSampleAction );
        createSampleItem.setMnemonic( 'C' );
        menuItems[0] = createSampleItem;
        
        return menuItems;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    InstrumentDescriptor getDescriptor()
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
    
    int getType()
    {
        return m_type;
    }
    
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
        
        int newType = m_descriptor.getType();
        if ( newType != m_type )
        {
            changed = true;
            m_type = newType;
        }
        
        return changed;
    }

}
