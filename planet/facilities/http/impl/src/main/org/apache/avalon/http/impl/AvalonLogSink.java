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

package org.apache.avalon.http.impl;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.mortbay.util.Frame;
import org.mortbay.util.Log;
import org.mortbay.util.LogSink;

/**
 * Jetty Log redirection to the Avalon framework logging channel.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version 1.0
 */
public class AvalonLogSink extends AbstractLogEnabled 
    implements LogSink
{
    /**
     * Stop the logging channel.  This implementation does nothing 
     * as avalon channels cannot be started or stopped.
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException
    {
    }

    /**
     * Returns the started state of the loging channel.  The 
     * implementation always returns TRUE.
     * @return the started state of the channel
     */
    public boolean isStarted()
    {
        return true;
    }

    /**
     * Set a loging option.  Not implemented.
     * @param s the options
     */
    public void setOptions( String s )
    {
    }

    /**
     * Get a logging channel option. Not implemented.
     * @return an zero length string
     */
    public String getOptions()
    {
        return "";
    }

    /**
     * Log a message to the channel.
     * @param type the type of message
     * @param message the message
     * @param frame the frame
     * @param time the time
     */
    public void log( String type, Object message, Frame frame, long time )
    {
        if( type.equals( Log.DEBUG ) )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( 
                  message 
                  + "\n\ttime: " + time
                  + "\n\tframe: " + frame );
            }
        }
        else if( type.equals( Log.FAIL ) )
        {
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( 
                  message 
                  + "\n\ttime: " + time
                  + "\n\tframe: " + frame );
            }
        }
        else if( type.equals( Log.WARN ) )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( 
                  message 
                  + "\n\ttime: " + time
                  + "\n\tframe: " + frame );
            }
        }
        else
        {
            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( "" + message );
            }
        }
    }

    /**
     * Log a message to the logging channel.
     * @param message the message to log
     */
    public void log( String message )
    {
        getLogger().info( message );
    }

    /**
     * Start the logging channel.  
     * The default implementation does nothing.
     * @throws Exception
     */
    public void start() throws Exception
    {
    }
}
