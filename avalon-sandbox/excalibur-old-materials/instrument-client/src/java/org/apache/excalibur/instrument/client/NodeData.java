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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class NodeData
{
    protected static final String MEDIA_PATH = "org/apache/excalibur/instrument/client/media/";
    protected static final JMenuItem[] EMPTY_MENU_ITEM_ARRAY = new JMenuItem[0];
    
    private String m_name;
    private String m_description;
    private int m_stateVersion;
    
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
    
    int getStateVersion()
    {
        return m_stateVersion;
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
    
    
    boolean update( String name, String description, int stateVersion )
    {
        boolean changed = false;
        
        changed |= name.equals( m_name );
        m_name = name;
        
        changed |= description.equals( m_description );
        m_description = description;
        
        changed |= stateVersion == m_stateVersion;
        m_stateVersion = stateVersion;
        
        return changed;
    }
    
    public String toString()
    {
        return m_description;
    }
}