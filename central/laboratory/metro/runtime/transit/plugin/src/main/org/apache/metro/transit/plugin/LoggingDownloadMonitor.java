/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.metro.transit.plugin;

import org.apache.metro.logging.Logger;
import org.apache.metro.transit.DownloadMonitor;

/**
 * Console montor for download messages.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
class LoggingDownloadMonitor implements DownloadMonitor
{
    private final int m_total;
    private final String m_max;
    private final Logger m_logger;

    public LoggingDownloadMonitor( Logger logger, String message, int total )
    {
        m_logger = logger;
        m_total = total;
        m_max = getFranctionalValue( total );

        if(( message != null ) && m_logger.isInfoEnabled() )
        {
            m_logger.info( message );
        }
    }

    public void notifyUpdate( int count )
    {
        // ignore
    }

    public void notifyCompletion()
    {
        if( m_logger.isInfoEnabled() )
        {
            m_logger.info( "" + m_max + "k downloaded." );
        }
    }

    private static String getFranctionalValue( int total )
    {
        float realTotal = new Float( total ).floatValue();
        float realK = new Float( 1024 ).floatValue();
        float r = (realTotal / realK);

        String value = new Float( r ).toString();
        int j = value.indexOf( "." );
        if( j > -1 )
        {
             int q = value.length();
             int k = q - j;
             if( k > 3 )
             {
                 return value.substring( 0, j + 3 ); 
             }
             else
             {
                 return value;
             }
        }
        else
        { 
             return value; 
        }
    }

    private static boolean isAnt()
    {
         return ( System.getProperty( "java.class.path" ).indexOf( "ant" ) > -1 );
    }

}

