/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.container.metainfo;

import org.apache.avalon.framework.Version;

/**
 * This class is used to provide explicit information to assembler
 * and administrator about the Component. It includes information
 * such as;
 *
 * <ul>
 *   <li>a symbolic name</li>
 *   <li>a display name</li>
 *   <li>classname</li>
 *   <li>version</li>
 * </ul>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/06/04 04:13:52 $
 */
public class ComponentDescriptor
{
    /**
     * The short name of the Component Type. Useful for displaying
     * human readable strings describing the type in
     * assembly tools or generators.
     */
    private final String m_name;
    private final String m_displayName;
    private final String m_classname;
    private final Version m_version;

    public ComponentDescriptor( final String name,
                                final String displayName,
                                final String classname,
                                final Version version )
    {
        m_name = name;
        m_displayName = displayName;
        m_classname = classname;
        m_version = version;
    }

    /**
     * Return the symbolic name of component.
     *
     * @return the symbolic name of component.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the display name of component.
     *
     * @return the display name of component.
     */
    public String getDisplayName()
    {
        return m_displayName;
    }

    /**
     * Return the classname of component.
     *
     * @return the classname of component.
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Return the version of component.
     *
     * @return the version of component.
     */
    public Version getVersion()
    {
        return m_version;
    }
}

