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
 * Contract defining a repository monitor.  The contract defines a set of operations
 * dealing with the notification of debug, info and error messages, together with 
 * and operation supporting the construction of a monitor dedicated to a dowload
 * action.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public interface Monitor
{

   /**
    * Record a debug level message.
    * @param message the debug message to record
    */
    void debug( String message );

   /**
    * Record a informative message.
    * @param message the info message to record
    */
    void info( String message );

   /**
    * Record a error level message.
    * @param message the error message to record
    * @param e the error
    */
    void error( String message, Throwable e );

    /**
     * Create a return a new download monitor.
     * @param message the initial download or update message
     * @param size the estimated download size
     * @return the new download monitor
     */
    DownloadMonitor createDownloadMonitor( String message, int size );
}


