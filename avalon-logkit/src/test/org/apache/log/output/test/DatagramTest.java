/* 
 * Copyright 1999-2004 The Apache Software Foundation
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
package org.apache.log.output.test;

import java.net.InetAddress;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.format.Formatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.format.SyslogFormatter;
import org.apache.log.output.net.DatagramOutputTarget;

/**
 *
 * @author Peter Donald
 */
public final class DatagramTest
{
    public static void main( final String[] args )
    {
        try
        {
            Formatter formatter = null;
            String message = null;

            if( 0 == args.length )
            {
                formatter = new SyslogFormatter( SyslogFormatter.FACILITY_DAEMON );
                message = "hello!!!";
            }
            else
            {
                //final int facility = 9<<3; //Cron
                //final int priority = 3; //ERROR
                //final String message = "<" + (facility|priority) + "> hello!!!";
                formatter = new RawFormatter();
                message = args[ 0 ];
            }

            final InetAddress address = InetAddress.getByName( "localhost" );
            final DatagramOutputTarget target =
                new DatagramOutputTarget( address, 514, formatter );

            final Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor( "foo" );
            logger.setLogTargets( new LogTarget[]{target} );

            logger.warn( message, new Exception() );
        }
        catch( final Throwable t )
        {
            t.printStackTrace();
        }
    }
}
