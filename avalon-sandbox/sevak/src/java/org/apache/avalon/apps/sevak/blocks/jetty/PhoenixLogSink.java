/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.apps.sevak.blocks.jetty;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.mortbay.util.Frame;
import org.mortbay.util.Log;
import org.mortbay.util.LogSink;

/**
 * Jetty Log redirection
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Bruno Dumon & Paul Hammant
 * @version 1.0
 */
public class PhoenixLogSink extends AbstractLogEnabled implements LogSink
{
    /**
     * Stop (unimpled)
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException
    {
    }

    /**
     * Is this started (always)
     * @return
     */
    public boolean isStarted()
    {
        return true;
    }

    /**
     * Set Options (un implemented)
     * @param s the options
     */
    public void setOptions( String s )
    {
    }

    /**
     * Get Option (unimplemented)
     * @return the options
     */
    public String getOptions()
    {
        return "";
    }

    /**
     * Log something
     * @param type the type of message
     * @param message the message
     * @param frame the frame
     * @param time the time
     */
    public void log( String type, Object message, Frame frame, long time )
    {
        if( type.equals( Log.DEBUG ) )
        {
            getLogger().info( "time=" + time + " message=" + message + " frame=" + frame );
        }
        else if( type.equals( Log.FAIL ) )
        {
            getLogger().error( "time=" + time + " message=" + message + " frame=" + frame );
        }
        else if( type.equals( Log.WARN ) )
        {
            getLogger().warn( "time=" + time + " message=" + message + " frame=" + frame );
        }
        else if( type.equals( Log.ASSERT ) )
        {
            getLogger().info( "ASSERT time=" + time + " message=" + message + " frame=" + frame );
        }
        else
        {
            getLogger().info( "time=" + time + " message=" + message + " frame=" + frame );
        }
    }

    /**
     * Log a message
     * @param message the Message
     */
    public void log( String message )
    {
        getLogger().info( message );
    }

    /**
     * Start a logger (unimpled)
     * @throws Exception
     */
    public void start() throws Exception
    {
    }
}
