/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli;

/**
 * Basic class describing an type of option.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class CLOptionDescriptor
{
    public final static int                  ARGUMENT_REQUIRED         = 1 << 1;
    public final static int                  ARGUMENT_OPTIONAL         = 1 << 2;
    public final static int                  ARGUMENT_DISALLOWED       = 1 << 3;
    public final static int                  ARGUMENTS_REQUIRED_2      = 1 << 4;

    protected final int                      m_id;
    protected final int                      m_flags;
    protected final String                   m_name;
    protected final String                   m_description;
    protected final int[]                    m_incompatable;

    /**
     * Constructor.
     *
     * @param name the name/long option
     * @param flags the flags
     * @param id the id/character option
     * @param description description of option usage
     */
    public CLOptionDescriptor( final String name,
                               final int flags,
                               final int id,
                               final String description )
    {
        this( name, flags, id, description, new int[] { id } );
    }

    /**
     * Constructor.
     *
     * @param name the name/long option
     * @param flags the flags
     * @param id the id/character option
     * @param description description of option usage
     */
    public CLOptionDescriptor( final String name,
                               final int flags,
                               final int id,
                               final String description,
                               final int[] incompatable )
    {
        m_id = id;
        m_name = name;
        m_flags = flags;
        m_description = description;
        m_incompatable = incompatable;
    }

    protected int[] getIncompatble()
    {
        return m_incompatable;
    }

    /**
     * Retrieve textual description.
     *
     * @return the description
     */
    public final String getDescription()
    {
        return m_description;
    }

    /**
     * Retrieve flags about option.
     * Flags include details such as whether it allows parameters etc.
     *
     * @return the flags
     */
    public final int getFlags()
    {
        return m_flags;
    }

    /**
     * Retrieve the id for option.
     * The id is also the character if using single character options.
     *
     * @return the id
     */
    public final int getId()
    {
        return m_id;
    }

    /**
     * Retrieve name of option which is also text for long option.
     *
     * @return name/long option
     */
    public final String getName()
    {
        return m_name;
    }

    /**
     * Convert to String.
     *
     * @return the converted value to string.
     */
    public String toString()
    {
        return
            "[OptionDescriptor " + m_name +
            ", " + m_id + ", " + m_flags +
            ", " + m_description + " ]";
    }
}
