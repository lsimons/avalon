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
