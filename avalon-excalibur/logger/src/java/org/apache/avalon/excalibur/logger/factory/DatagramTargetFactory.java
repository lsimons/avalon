/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
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

