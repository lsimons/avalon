/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.cli;

import java.util.Arrays;

/**
 * Basic class describing an instance of option.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public final class CLOption
{
    /**
     * Value of {@link #getId} when the option is a text argument.
     */
    public static final int TEXT_ARGUMENT = 0;

    /**
     * Default descriptor. Required, since code assumes that getDescriptor will never return null.
     */
    private static final CLOptionDescriptor TEXT_ARGUMENT_DESCRIPTOR =
            new CLOptionDescriptor( null, CLOptionDescriptor.ARGUMENT_OPTIONAL, TEXT_ARGUMENT,
                    null );

    private String[] m_arguments;
    private CLOptionDescriptor m_descriptor = TEXT_ARGUMENT_DESCRIPTOR;

    /**
     * Retrieve argument to option if it takes arguments.
     *
     * @return the (first) argument
     */
    public final String getArgument()
    {
        return getArgument( 0 );
    }

    /**
     * Retrieve indexed argument to option if it takes arguments.
     *
     * @param index The argument index, from 0 to
     * {@link #getArgumentCount()}-1.
     * @return the argument
     */
    public final String getArgument( final int index )
    {
        if( null == m_arguments || index < 0 || index >= m_arguments.length )
        {
            return null;
        }
        else
        {
            return m_arguments[index];
        }
    }

    /**
     * Retrieve id of option.
     *
     * The id is eqivalent to character code if it can be a single letter option.
     *
     * @return the id
     * @deprecated use <code>getDescriptor().getId()</code> instead
     */
    public final int getId()
    {
        return m_descriptor == null ? TEXT_ARGUMENT : m_descriptor.getId();
    }

    public final CLOptionDescriptor getDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Constructor taking an descriptor
     *
     * @param descriptor the descriptor iff null, will default to a "text argument" descriptor.
     */
    public CLOption( final CLOptionDescriptor descriptor )
    {
        if( descriptor != null )
        {
            m_descriptor = descriptor;
        }
    }

    /**
     * Constructor taking argument for option.
     *
     * @param argument the argument
     */
    public CLOption( final String argument )
    {
        this( (CLOptionDescriptor)null );
        addArgument( argument );
    }

    /**
     * Mutator of Argument property.
     *
     * @param argument the argument
     */
    public final void addArgument( final String argument )
    {
        if( null == m_arguments )
        {
            m_arguments = new String[]{argument};
        }
        else
        {
            final String[] arguments = new String[m_arguments.length + 1];
            System.arraycopy( m_arguments, 0, arguments, 0, m_arguments.length );
            arguments[m_arguments.length] = argument;
            m_arguments = arguments;
        }
    }

    /**
     * Get number of arguments.
     *
     * @return the number of arguments
     */
    public final int getArgumentCount()
    {
        if( null == m_arguments )
        {
            return 0;
        }
        else
        {
            return m_arguments.length;
        }
    }

    /**
     * Convert to String.
     *
     * @return the string value
     */
    public final String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[Option " );
        sb.append( (char)m_descriptor.getId() );

        if( null != m_arguments )
        {
            sb.append( ", " );
            sb.append( Arrays.asList( m_arguments ) );
        }

        sb.append( " ]" );

        return sb.toString();
    }
}
