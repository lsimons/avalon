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
class ConsoleMonitor implements Monitor
{
    private boolean m_debug;
    private String m_category;

    public ConsoleMonitor( boolean debug )
    {
         this( debug, "" );
    }

    public ConsoleMonitor( boolean debug, String category )
    {
         m_debug = debug;
         m_category = category;
    }

   /**
    * Return TRUE is debug level logging is enabled.
    * @return the enabled state of debug logging
    */
    public boolean isDebugEnabled()
    {
        return m_debug;
    }

   /**
    * Return TRUE is info level logging is enabled.
    * @return the enabled state of info logging
    */
    public boolean isInfoEnabled()
    {
        return true;
    }

   /**
    * Return TRUE is error level logging is enabled.
    * @return the enabled state of error logging
    */
    public boolean isErrorEnabled()
    {
        return true;
    }

    public void debug( String message )
    {
        if( !m_debug ) return; 
        System.out.println( "[DEBUG  ] (" + m_category + ") : " + message );
    }

    public void info( String message )
    {
        System.out.println( "[INFO   ] (" + m_category + ") : " + message );
    }

    public void error( String message, Throwable e )
    {
        System.out.println( "[ERROR  ] (" + m_category + ") : " + message );
        if(( null != e ) && m_debug )
        {
            e.printStackTrace();
        }
    }

    /**
     * Create a return a new download monitor.
     * @param message the initial download or update message
     * @param size the estimated download size
     * @return the new download monitor
     */
    public DownloadMonitor createDownloadMonitor( String message, int size )
    {
         return new ConsoleDownloadMonitor( this, message, size );
    }
}

