/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * This class encapsulates categories in logging system.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class Category
{
    public static final char             SEPARATOR     = '.';

    protected final String               m_name;
    protected Priority.Enum              m_priority;
    
    /**
     * Constructor taking name of category.
     *
     * @param name the name of category
     */
    public Category( final String name )
    {
        m_name = name;
    }
    
    /**
     * Return the name of the category.
     *
     * @return the name
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Get the priority of categroy.
     * The priority is the priority that log entries must satisfy to be output.
     *
     * @return the priority
     */
    public Priority.Enum getPriority()
    {
        return m_priority;
    }

    /**
     * Set the priority of categroy
     *
     * @param priority the priority
     */
    public void setPriority( final Priority.Enum priority )
    {
        m_priority = priority;
    }
}
