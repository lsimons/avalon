/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.avalon.camelot;

import org.apache.avalon.component.Component;

/**
 * Contains information about a particular instance of contained component. 
 * This would contain name, configuration data, parameters, log entries etc. 
 * Basically instance data.
*
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Entry
    implements Component
{
    private Info          m_info;
    private Object        m_instance;
    private State         m_state;
    private Locator       m_locator;

    public Entry()
    {
    }

    public Entry( final Info info, final Object instance, final State state )
    {
        m_info = info;
        m_instance = instance;
        m_state = state;
    }

    /**
     * Retrieve Info describing instance.
     *
     * @return the info
     */
    public Info getInfo()
    {
        return m_info;
    }

    /**
     * Mutator for info property.
     *
     * @param info the Info
     */
    public void setInfo( final Info info )
    {
        m_info = info;
    }

    /**
     * Retrieve Locator describing access path for instance.
     *
     * @return the locator
     */
    public Locator getLocator()
    {
        return m_locator;
    }

    /**
     * Mutator for locator property.
     *
     * @param locator the Locator
     */
    public void setLocator( final Locator locator )
    {
        m_locator = locator;
    }

    /**
     * Retrieve instance of component.
     *
     * @return the component instance
     */
    public Object getInstance()
    {
        return m_instance;
    }
    
    /**
     * Set instance of component.
     *
     * @return the component instance
     */
    public void setInstance( final Object instance )
    {
        m_instance = instance;
    }
    
    /**
     * Retrieve state of a component.
     *
     * @return the components state
     */
    public State getState()
    {
        return m_state;
    }
    
    /**
     * set state of a component.
     *
     * @param state  the components state
     */
    public void setState( final State state )
    {
        m_state = state;
    }
}
