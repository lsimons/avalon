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
import org.apache.log.output.net.SocketOutputTarget;

/**
 * This factory creates LogTargets with a wrapped SocketOutputTarget around it.
 * <p>
 * Configuration syntax:
 * <pre>
 * &lt;socket-target id="target-id"&gt;
 *   &lt;address hostname="hostname" port="4455" /&gt;
 * &lt;/socket-target&gt;
 * </pre>
 * </p>
 * <p>
 *  This factory creates a SocketOutputTarget object which will
 *  TCP/IP socket to communicate with the server. The name of the target is specified by the
 *  hostname attribute of the &lt;address&gt; element and the port by the port attribute.
 *  In the config file above the formatting for the log messages is not embedded as it should
 *  be specified on the server side
 * </p>
 *
 *
 * @author <a href="mailto:rghorpade@onebridge.de"> Rajendra Ghorpade </a>
 */
public class SocketTargetFactory extends AbstractTargetFactory
{

    /**
     * Creates a log target based on Configuration
     *
     *@param conf Configuration requied for creating the log target
     *@throws ConfigurationException if something goes wrong while reading from
     *          configuration
     */
    public LogTarget createTarget( final Configuration conf )
        throws ConfigurationException
    {
        final InetAddress address;

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
            throw new ConfigurationException( "Host specified in socket target adress is unknown!", uhex );
        }

        final int port = configChild.getAttributeAsInteger( "port" );

        try
        {
            return new SocketOutputTarget( address, port );
        }
        catch( final IOException ioex )
        {
            throw new ConfigurationException( "Failed to create target!", ioex.fillInStackTrace() );
        }
    }
}
