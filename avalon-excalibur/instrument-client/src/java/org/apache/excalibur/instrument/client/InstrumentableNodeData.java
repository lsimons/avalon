/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.excalibur.instrument.client;

import javax.swing.ImageIcon;

import org.apache.excalibur.instrument.manager.interfaces.InstrumentableDescriptor;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
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
