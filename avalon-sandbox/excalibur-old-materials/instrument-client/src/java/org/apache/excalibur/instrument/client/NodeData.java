/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class NodeData
{
    protected static final String MEDIA_PATH = "org/apache/excalibur/instrument/client/media/";
    protected static final JMenuItem[] EMPTY_MENU_ITEM_ARRAY = new JMenuItem[0];
    
    private String m_name;
    private String m_description;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    protected NodeData()
    {
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    String getName()
    {
        return m_name;
    }
    
    String getDescription()
    {
        return m_description;
    }
    
    void setDescription( String description )
    {
        m_description = description;
    }
    
    /**
     * Get the icon to display for the node.
     *
     * @return the icon to display for the node.
     */
    abstract ImageIcon getIcon();
    
    /**
     * Return the text to use for a tool tip on this node.
     *
     * @return Tool Tip text.  May be null, for no tool tip.
     */
    abstract String getToolTipText();
    
    /**
     * Return the popup for the node.
     *
     * @return The the popup for the node.
     */
    public JPopupMenu getPopupMenu()
    {
        JPopupMenu popup;
        JMenuItem[] menuItems = getCommonMenuItems();
        if ( menuItems.length == 0 )
        {
            popup = null;
        }
        else
        {
            popup = new JPopupMenu( getDescription() );
            for ( int i = 0; i < menuItems.length; i++ )
            {
                popup.add( menuItems[i] );
            }
        }
        
        return popup;
    }
    
    /**
     * Returns an array of any menu items which will be displayed both
     *  in a popup menu and in the menus.
     *
     * @return An array of the common menu items.
     */
    public JMenuItem[] getCommonMenuItems()
    {
        return EMPTY_MENU_ITEM_ARRAY;
    }
    
    /**
     * Called when the node is selected.
     */
    void select()
    {
    }
    
    
    boolean update( String name, String description )
    {
        boolean changed = false;
        
        changed |= name.equals( m_name );
        m_name = name;
        
        changed |= description.equals( m_description );
        m_description = description;
        
        return changed;
    }
    
    public String toString()
    {
        return m_description;
    }
}