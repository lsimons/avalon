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

package org.apache.avalon.logging.logkit;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.format.SyslogFormatter;
import org.apache.log.format.XMLFormatter;

/**
 * Factory for Formatter-s.
 */
public class DefaultFormatterFactory implements FormatterFactory
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultFormatterFactory.class );
    
    //--------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------

   /**
    * Creation of a new formatter using a supplied configuration.
    * @param config the formatter configuration
    * @return the formatter instance
    * @exception IllegalArgumentException if the formatter type is unknown
    */
    public Formatter createFormatter( final Configuration config )
    {
        if( null == config ) return new StandardFormatter( DEFAULT_FORMAT );
        final String type = config.getAttribute( "type", "pattern" );
        final String format = config.getValue( DEFAULT_FORMAT );
        return createFormatter( type, format );
    }

   /**
    * Creation of a new formatter.
    * @param type the formatter type identifier
    * @param format the format specification
    * @return the formatter instance
    * @exception IllegalArgumentException if the formatter type is unknown
    */
    public Formatter createFormatter( String type, String format )
    {
        if( "avalon".equals( type ) )
        {
            return new StandardFormatter( format, true );
        }
        else if( "console".equals( type ) )
        {
            return new StandardFormatter( format, false );
        }
        else if( "extended".equals( type ) )
        {
            //
            // Normally ExtendPatternFormatter would look for callers
            // of Logger.class.  But when Excalibur Logger provides a
            // facade, the user class/method is actually one-level deeper.
            // We therefore create the pattern-formatter with an
            // additional depth-offset of 1.
            //

            return new ExtendedPatternFormatter( format, 1 );
        }
        else if( "raw".equals( type ) )
        {
            return new RawFormatter();
        }
        else if( "xml".equals( type ) )
        {
            return new XMLFormatter();
        }
        else if( "syslog".equals( type ) )
        {
            return new SyslogFormatter();
        }
        else if( "pattern".equals( type ) )
        {
            return new PatternFormatter( format );
        }
        else
        {
            final String error = 
              REZ.getString( "formatter.error.unknown-type", type );
            throw new IllegalArgumentException( error );
        }
    }
}
