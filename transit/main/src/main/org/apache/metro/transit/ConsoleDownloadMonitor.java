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

package org.apache.metro.transit;

/**
 * Console montor for download messages.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
class ConsoleDownloadMonitor implements DownloadMonitor
{
    private static final int KBYTE = 1024;

    private final int m_total;
    private final String m_max;
    private final Monitor m_monitor;

   /**
    * Creation of a new download monitor.
    * @param message the inital downloading message
    * @param total the estimated total bytes to download
    */
    public ConsoleDownloadMonitor( Monitor monitor, String message, int total )
    {
        m_total = total;
        m_max = getFranctionalValue( total );
        m_monitor = monitor;

        if( message != null )
        {
            if( isAnt() )
            {
                System.out.print( message );
            }
            else
            {
                System.out.println( message );
            }
        }
    }

   /**
    * Notify the monitor that an download increment has occured.
    * @param count the number of bytes downloaded
    */
    public void notifyUpdate( int count )
    {
        if( isAnt() )
        {
            if( count > ( KBYTE * 100 ) )
            {
                System.out.print( "." );
            }
        }
        else
        {
            String value = getFranctionalValue( count );
            int pad = m_max.length() - value.length();
            StringBuffer buffer = new StringBuffer( "Progress: " );
            for( int i=0; i<pad; i++ )
            {
                buffer.append( " " );
            }
            buffer.append( value );
            buffer.append( "k/" );
            if( m_total == 0 )
            {
                buffer.append( "?" );
            }
            else
            {
                buffer.append( m_max );
                buffer.append( "k\r" );
            }
            System.out.print( buffer.toString() );
        }
    }

   /**
    * Notification to the monitor that the download has comepleted.
    */
    public void notifyCompletion()
    {
        if( isAnt() )
        {
            System.out.println( "\n" + m_max + "k downloaded." );
        }
        else
        {
            System.out.println( "Downloaded " + m_max + "k.                        " );
        }
    }

    private static String getFranctionalValue( int total )
    {
        float realTotal = new Float( total ).floatValue();
        float realK = new Float( KBYTE ).floatValue();
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

