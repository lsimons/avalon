/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress;

/**
 * Keeps track of the relationship of all the associated meta data for a
 * component type.  It records the role, short name, component class, and
 * the handler class used to manage it.  The short name is included strictly
 * to enable "self-healing" configuration files.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/19 12:49:31 $
 */
public class RoleEntry
{
    private final String m_shortName;
    private final String m_role;
    private final Class m_componentClass;
    private final Class m_handlerClass;

    /**
     * Create a <code>RoleEntry</code> with all the associated information.
     * All arguments must be supplied.
     *
     * @param  role            Role name for this component type
     * @param  shortName       Short name for this component type
     * @param  componentClass  <code>Class</code> to instantiate the
     *                         component type
     * @param  handlerClass    <code>Class</code> to instantiate the
     *                         component handler
     *
     * @exception <code>IllegalArgumentException</code> if any argument is
     *         <code>null</code>.
     */
    public RoleEntry( final String role,
                      final String shortName,
                      final Class componentClass,
                      final Class handlerClass ) throws IllegalArgumentException
    {
        if( null == role )
        {
            throw new IllegalArgumentException( "\"role\" cannot be null." );
        }
        if( null == shortName )
        {
            throw new IllegalArgumentException( "\"shortname\" cannot be null." );
        }
        if( null == componentClass )
        {
            throw new IllegalArgumentException( "\"componentClass\" cannot be null." );
        }
        if( null == handlerClass )
        {
            throw new IllegalArgumentException( "\"handlerClass\" cannot be null." );
        }

        m_role = role;
        m_shortName = shortName;
        m_componentClass = componentClass;
        m_handlerClass = handlerClass;
    }

    /**
     * Get the role name for the component type.
     *
     * @return the role name
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Get the short name for the component type.  This is used in
     * "self-healing" configuration files.
     *
     * @return the short name
     */
    public String getShortname()
    {
        return m_shortName;
    }

    /**
     * Get the <code>Class</code> for the component type.
     *
     * @return the <code>Class</code>
     */
    public Class getComponentClass()
    {
        return m_componentClass;
    }

    /**
    * Get the <code>Class</code> for the component type's {@link org.apache.avalon.fortress.impl.handler.ComponentHandler}.
    *
    * @return the <code>Class</code>
    */
    public Class getHandlerClass()
    {
        return m_handlerClass;
    }
}
