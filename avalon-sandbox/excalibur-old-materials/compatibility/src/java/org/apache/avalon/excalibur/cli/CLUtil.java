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
 * CLUtil offers basic utility operations for use both internal and external to package.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:46:22 $
 * @since 4.0
 * @see CLOptionDescriptor
 */
public final class CLUtil
{
    private static final int MAX_DESCRIPTION_COLUMN_LENGTH = 60;

    /**
     * Format options into StringBuffer and return. This is typically used to
     * print "Usage" text in response to a "--help" or invalid option.
     *
     * @param options the option descriptors
     * @return the formatted description/help for options
     */
    public static final StringBuffer describeOptions( final CLOptionDescriptor[] options )
    {
        final String lSep = System.getProperty( "line.separator" );
        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < options.length; i++ )
        {
            final char ch = (char)options[ i ].getId();
            final String name = options[ i ].getName();
            String description = options[ i ].getDescription();
            int flags = options[ i ].getFlags();
            boolean argumentRequired =
                ( ( flags & CLOptionDescriptor.ARGUMENT_REQUIRED ) ==
                CLOptionDescriptor.ARGUMENT_REQUIRED );
            boolean twoArgumentsRequired =
                ( ( flags & CLOptionDescriptor.ARGUMENTS_REQUIRED_2 ) ==
                CLOptionDescriptor.ARGUMENTS_REQUIRED_2 );
            boolean needComma = false;
            if( twoArgumentsRequired )
            {
                argumentRequired = true;
            }

            sb.append( '\t' );

            if( Character.isLetter( ch ) )
            {
                sb.append( "-" );
                sb.append( ch );
                needComma = true;
            }

            if( null != name )
            {
                if( needComma )
                {
                    sb.append( ", " );
                }

                sb.append( "--" );
                sb.append( name );
            }

            if( argumentRequired )
            {
                sb.append( " <argument>" );
            }
            if( twoArgumentsRequired )
            {
                sb.append( "=<value>" );
            }
            sb.append( lSep );

            if( null != description )
            {
                while( description.length() > MAX_DESCRIPTION_COLUMN_LENGTH )
                {
                    final String descriptionPart =
                        description.substring( 0, MAX_DESCRIPTION_COLUMN_LENGTH );
                    description =
                        description.substring( MAX_DESCRIPTION_COLUMN_LENGTH );
                    sb.append( "\t\t" );
                    sb.append( descriptionPart );
                    sb.append( lSep );
                }

                sb.append( "\t\t" );
                sb.append( description );
                sb.append( lSep );
            }
        }
        return sb;
    }

    /**
     * Private Constructor so that no instance can ever be created.
     *
     */
    private CLUtil()
    {
    }
}
