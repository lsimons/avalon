/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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