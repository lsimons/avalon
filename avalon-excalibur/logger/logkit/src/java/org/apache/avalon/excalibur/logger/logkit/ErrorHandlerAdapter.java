/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.excalibur.logger.logkit;

import org.apache.avalon.framework.logger.Logger;
import org.apache.log.ErrorHandler;
import org.apache.log.Priority;
import org.apache.log.LogEvent;

/**
 * This class adapts o.a.a.f.logger.Logger
 * to the LogKit ErrorHandler interface.
 *
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/10/02 19:18:44 $
 * @since 4.0
 */

public class ErrorHandlerAdapter implements ErrorHandler
{
    private final Logger m_reliableLogger;

    public ErrorHandlerAdapter( final Logger reliableLogger )
    {
       if ( reliableLogger == null )
       {
           throw new NullPointerException( "reliableLogger" );
       }
       m_reliableLogger = reliableLogger;
    }

    public void error( final String message, final Throwable throwable, final LogEvent event )
    {
        // let them know we're not OK
        m_reliableLogger.fatalError( message, throwable );

        // transmit the original error
        final Priority p = event.getPriority();
        final String nestedMessage = "nested log event: " + event.getMessage();

        if ( p == Priority.DEBUG )
        {
            m_reliableLogger.debug( nestedMessage, event.getThrowable() );
        }
        else if ( p == Priority.INFO )
        {
            m_reliableLogger.info( nestedMessage, event.getThrowable() );
        }
        else if ( p == Priority.WARN )
        {
            m_reliableLogger.warn( nestedMessage, event.getThrowable() );
        }
        else if ( p == Priority.ERROR )
        {
            m_reliableLogger.error( nestedMessage, event.getThrowable() );
        }
        else if ( p == Priority.FATAL_ERROR)
        {
            m_reliableLogger.fatalError( nestedMessage, event.getThrowable() );
        }
        else
        {
            /** This just plainly can't happen :-)*/
            m_reliableLogger.error( "unrecognized priority " + nestedMessage, 
                event.getThrowable() );
        }
    }
}
