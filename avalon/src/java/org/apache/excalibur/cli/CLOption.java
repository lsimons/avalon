/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli;

import java.util.Arrays;

/**
 * Basic class describing an instance of option.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class CLOption
{
    protected final int                      m_id;
    protected String[]                       m_arguments;

    /**
     * Retrieve argument to option if it takes arguments.
     *
     * @return the argument
     */
    public final String getArgument()
    {
        return getArgument( 0 );
    }

    /**
     * Retrieve argument to option if it takes arguments.
     *
     * @return the argument
     */
    public final String getArgument( final int index )
    {
        if( null == m_arguments || index < 0 || index >= m_arguments.length )
        {
            return null;
        }
        else return m_arguments[ index ];
    }

    /**
     * Retrieve id of option.
     *
     * The id is eqivelent to character code if it can be a single letter option.
     *
     * @return the id
     */
    public final int getId()
    {
        return m_id;
    }

    /**
     * Constructor taking an id (that must be a proper character code)
     *
     * @param id the new id
     */
    public CLOption( final int id )
    {
        m_id = id;
    }

    /**
     * Constructor taking argument for option.
     *
     * @param argument the argument
     */
    public CLOption( final String argument )
    {
        this( 0 );
        addArgument( argument );
    }

    /**
     * Mutator fo Argument property.
     *
     * @param argument the argument
     */
    public final void addArgument( final String argument )
    {
        if( null == m_arguments ) m_arguments = new String[] { argument };
        else
        {
            final String[] arguments = new String[ m_arguments.length + 1 ];
            System.arraycopy( m_arguments, 0, arguments, 0, m_arguments.length );
            arguments[ m_arguments.length ] = argument;
            m_arguments = arguments;
        }
    }

    public int getArgumentCount()
    {
        if( null == m_arguments ) return 0;
        else return m_arguments.length;
    }

    /**
     * Convert to String.
     *
     * @return the string value
     */
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[Option " );
        sb.append( (char)m_id );

        if( null != m_arguments )
        {
            sb.append( ", " );
            sb.append( Arrays.asList( m_arguments ) );
        }

        sb.append( " ]" );

        return sb.toString();
    }
}
