/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.log.output.test;

import java.net.InetAddress;
import org.apache.log.Category; 
import org.apache.log.LogKit; 
import org.apache.log.LogTarget; 
import org.apache.log.Logger; 
import org.apache.log.Priority; 
import org.apache.log.format.SyslogFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.output.DatagramOutputTarget;
 
/** 
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a> 
 */ 
public final class DatagramTest
{
    public static void main( final String[] args )
    {
        try
        {
            final InetAddress address = InetAddress.getByName( "localhost" );
            final DatagramOutputTarget target = new DatagramOutputTarget( address, 514 );

            String message = null;

            if( 0 == args.length )
            {
                target.setFormatter( new SyslogFormatter( SyslogFormatter.FACILITY_DAEMON ) );               
                message = "hello!!!";
            }
            else
            {
                //final int facility = 9<<3; //Cron
                //final int priority = 3; //ERROR
                //final String message = "<" + (facility|priority) + "> hello!!!";
                target.setFormatter( new RawFormatter() );
                message = args[ 0 ];
            }

            final Category category = LogKit.createCategory( "foo", Priority.DEBUG );
            final Logger logger = LogKit.createLogger( category, new LogTarget[] { target } );

            logger.warn( message, new Exception() );
        }
        catch( final Throwable t )
        {
            t.printStackTrace();
        }
    }
}
