/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.logger.factory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.net.DatagramOutputTarget;

/**
 * This factory creates LogTargets with a wrapped DatagramOutputTarget around it.
 * <p>
 * Configuration syntax:
 * <pre>
 * &lt;datagram-target id="target-id"&gt;
 *   &lt;address hostname="hostname" port="4455" /&gt;
 *     &lt;format type="extended"&gt;
 *                %7.7{priority} %23.23{time:yyyy-MM-dd HH:mm:ss:SSS} [%25.25{category}] : %{message}\n%{throwable}
 *     &lt;/format&gt;
 * &lt;/datagram-target&gt;
 * </pre>
 * </p>
 * <p>
 *  This factory creates a DatagramOutputTarget object which will
 *  sends datagrams to the specified address. The name of the target is specified by the hostname attribute
 *  of the &lt;address&gt; element and the port by the port attribute.The &lt;address&gt; element
 *  wraps the format to output the log.
 * </p>
 *
 *
 * @author <a href="mailto:rghorpade@onebridge.de"> Rajendra Ghorpade </a>
 */
public class DatagramTargetFactory
    extends AbstractTargetFactory
{
    /**  Default format */
    private static final String FORMAT =
        "%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}";

    /**
     * Create a LogTarget based on a Configuration
     */
    public LogTarget createTarget( final Configuration conf )
        throws ConfigurationException
    {
        InetAddress address;

        final Configuration configChild = conf.getChild( "address", false );
        if( null == configChild )
        {
            throw new ConfigurationException( "target address not specified in the config" );
        }

        try
        {
            address = InetAddress.getByName( configChild.getAttribute( "hostname" ) );
        }
        catch( UnknownHostException uhex )
        {
            throw new ConfigurationException( "Host specified in datagram target adress is unknown!", uhex );
        }

        int port = configChild.getAttributeAsInteger( "port" );

        final Formatter formatter = getFormatter( conf.getChild( "format", false ) );

        try
        {
            return new DatagramOutputTarget( address, port, formatter );
        }
        catch( IOException ioex )
        {
            throw new ConfigurationException( "Failed to create target!", ioex );
        }
    }

    /**
     * Returns the Formatter
     *
     * @param conf Configuration for the formatter
     */
    protected Formatter getFormatter( final Configuration conf )
    {
        final String type = conf.getAttribute( "type", "pattern" );
        final String format = conf.getValue( FORMAT );

        if( "extended".equals( type ) )
        {
            return new ExtendedPatternFormatter( format );
        }
        else if( "raw".equals( type ) )
        {
            return new RawFormatter();
        }

        /**  default formatter */
        return new PatternFormatter( format );
    }
}

