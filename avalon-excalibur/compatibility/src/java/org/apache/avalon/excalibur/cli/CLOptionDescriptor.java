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

/**
 * Basic class describing an type of option.
 * Typically, one creates a static array of <code>CLOptionDescriptor</code>s,
 * and passes it to {@link CLArgsParser#CLArgsParser(String[], CLOptionDescriptor[])}.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/11/09 15:31:38 $
 * @since 4.0
 * @see CLArgsParser
 * @see CLUtil
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public final class CLOptionDescriptor
{
    /** Flag to say that one argument is required */
    public static final int ARGUMENT_REQUIRED = 1 << 1;
    /** Flag to say that the argument is optional */
    public static final int ARGUMENT_OPTIONAL = 1 << 2;
    /** Flag to say this option does not take arguments */
    public static final int ARGUMENT_DISALLOWED = 1 << 3;
    /** Flag to say this option requires 2 arguments */
    public static final int ARGUMENTS_REQUIRED_2 = 1 << 4;
    /** Flag to say this option may be repeated on the command line */
    public static final int DUPLICATES_ALLOWED = 1 << 5;

    private final int m_id;
    private final int m_flags;
    private final String m_name;
    private final String m_description;
    private final int[] m_incompatible;

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
        this( name, flags, id, description,
              ( ( flags & CLOptionDescriptor.DUPLICATES_ALLOWED ) > 0 )
              ? new int[ 0 ] : new int[]{id} );
    }

    /**
     * Constructor.
     *
     * @param name the name/long option
     * @param flags the flags
     * @param id the id/character option
     * @param description description of option usage
     * @param incompatible an array listing the ids of all incompatible options
     * @deprecated use the version with the array of CLOptionDescriptor's
     */
    public CLOptionDescriptor( final String name,
                               final int flags,
                               final int id,
                               final String description,
                               final int[] incompatible )
    {
        m_id = id;
        m_name = name;
        m_flags = flags;
        m_description = description;
        m_incompatible = incompatible;

        int modeCount = 0;
        if( ( ARGUMENT_REQUIRED & flags ) == ARGUMENT_REQUIRED )
        {
            modeCount++;
        }
        if( ( ARGUMENT_OPTIONAL & flags ) == ARGUMENT_OPTIONAL )
        {
            modeCount++;
        }
        if( ( ARGUMENT_DISALLOWED & flags ) == ARGUMENT_DISALLOWED )
        {
            modeCount++;
        }
        if( ( ARGUMENTS_REQUIRED_2 & flags ) == ARGUMENTS_REQUIRED_2 )
        {
            modeCount++;
        }

        if( 0 == modeCount )
        {
            final String message = "No mode specified for option " + this;
            throw new IllegalStateException( message );
        }
        else if( 1 != modeCount )
        {
            final String message = "Multiple modes specified for option " + this;
            throw new IllegalStateException( message );
        }
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
                               final CLOptionDescriptor[] incompatible )
    {
        m_id = id;
        m_name = name;
        m_flags = flags;
        m_description = description;

        m_incompatible = new int[ incompatible.length ];
        for( int i = 0; i < incompatible.length; i++ )
            m_incompatible[ i ] = incompatible[ i ].getId();
    }

    /**
     * @deprecated Use the correctly spelled {@link #getIncompatible} instead.
     * @return the array of incompatible option ids
     */
    protected final int[] getIncompatble()
    {
        return getIncompatible();
    }

    /**
     * Get the array of incompatible option ids.
     *
     * @return the array of incompatible option ids
     */
    protected final int[] getIncompatible()
    {
        return m_incompatible;
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
    public final String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( "[OptionDescriptor " );
        sb.append( m_name );
        sb.append( "[OptionDescriptor " );
        sb.append( m_name );
        sb.append( ", " );
        sb.append( m_id );
        sb.append( ", " );
        sb.append( m_flags );
        sb.append( ", " );
        sb.append( m_description );
        sb.append( " ]" );
        return sb.toString();
    }
}
