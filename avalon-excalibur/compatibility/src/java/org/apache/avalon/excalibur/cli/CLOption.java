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
package org.apache.avalon.excalibur.cli;

import java.util.Arrays;

/**
 * Basic class describing an instance of option.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:leo.sutic at inspireinfrastructure.com">Leo Sutic</a>
 * @version $Revision: 1.1 $ $Date: 2003/11/09 15:31:38 $
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
        new CLOptionDescriptor( null, CLOptionDescriptor.ARGUMENT_OPTIONAL, TEXT_ARGUMENT, null );

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
            return m_arguments[ index ];
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
            final String[] arguments = new String[ m_arguments.length + 1 ];
            System.arraycopy( m_arguments, 0, arguments, 0, m_arguments.length );
            arguments[ m_arguments.length ] = argument;
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
