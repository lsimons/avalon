/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.kernel;

import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;

/**
 * This is the structure that components are contained within when
 * loaded into a container.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
 */
public class ComponentEntry
{
    /**
     * The {@link ComponentProfile} that describes
     * this component.
     */
    private final ComponentProfile m_profile;
    /**
     * The instance of this component.
     */
    private Object m_object;

    /**
     * Creation of a new <code>ComponentEntry</code> instance.
     *
     * @param profile the {@link ComponentProfile} instance defining the component.
     */
    public ComponentEntry( final ComponentProfile profile )
    {
        if( null == profile )
        {
            throw new NullPointerException( "profile" );
        }
        m_profile = profile;
    }

    /**
     * Returns the {@link ComponentProfile} for this component.
     *
     * @return the {@link ComponentProfile} for this component.
     */
    public ComponentProfile getProfile()
    {
        return m_profile;
    }

    /**
     * Returns the the object associated with this entry.
     * @return the entry object
     */
    public Object getObject()
    {
        return m_object;
    }

    /**
     * Set the object assoaiated to this entry.
     * @param object the object to associate with the entry
     */
    public void setObject( final Object object )
    {
        m_object = object;
    }

    /**
     * Returns TRUE is the object for this entry has been set.
     * @return the active status of this entry
     */
    public boolean isActive()
    {
        return ( null != getObject() );
    }
}
