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
import org.apache.metro.transit.Monitor;
import org.apache.metro.transit.DownloadMonitor;

/**
 * Console montor for download messages.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
class LoggingMonitor implements Monitor
{
    private final Logger m_logger;

    public LoggingMonitor( Logger logger )
    {
        m_logger = logger;
    }

    public void debug( String message )
    {
        if( m_logger.isDebugEnabled() )
        {
             m_logger.debug( message );
        }
    }

    public void info( String message )
    {
        if( m_logger.isInfoEnabled() )
        {
             m_logger.info( message );
        }
    }

    public void error( String message, Throwable e )
    {
        if( m_logger.isErrorEnabled() )
        {
             m_logger.error( message, e );
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
        return new LoggingDownloadMonitor( m_logger, message, size );
    }

}

