/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
        changed |= update( m_descriptor.getName(), m_descriptor.getDescription() );
        
        changed |= ( m_descriptor.isConfigured() == m_configured );
        m_configured = m_descriptor.isConfigured();
        
        changed |= ( m_descriptor.isRegistered() == m_registered );
        m_registered = m_descriptor.isRegistered();
        
        changed |= ( m_descriptor.getType() == m_type );
        m_type = m_descriptor.getType();
        
        return changed;
    }

}