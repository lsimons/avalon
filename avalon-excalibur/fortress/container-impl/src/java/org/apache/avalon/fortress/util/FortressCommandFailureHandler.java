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
package org.apache.avalon.fortress.util;

import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.fortress.impl.handler.PrepareHandlerCommand;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.excalibur.event.command.Command;
import org.apache.excalibur.event.command.CommandFailureHandler;

/**
 * The default CommandFailureHandler used by Fortress to log any
 *  failures encountered while executing commands.
 *
 * @author <a href="leif.at.apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/06/11 19:14:42 $
 * @since 4.1
 */
public class FortressCommandFailureHandler
    extends AbstractLogEnabled
    implements CommandFailureHandler
{
    /**
     * Handle a command failure.  If a command throws an exception, it has failed.  The
     * CommandManager will call this method so that we can handle the problem effectively.
     *
     * @param command    The original Command object that failed
     * @param throwable  The throwable that caused the failure
     *
     * @return <code>true</code> if the CommandManager should cease to process commands.
     */
    public boolean handleCommandFailure( final Command command, final Throwable throwable )
    {
        if ( command instanceof PrepareHandlerCommand )
        {
            PrepareHandlerCommand phc = (PrepareHandlerCommand)command;
            ComponentHandler handler = phc.getHandler();
            
            if ( getLogger().isErrorEnabled() )
            {
                getLogger().error( "Could not prepare ComponentHandler for: "
                    + handler.getComponentClass().getName(), throwable );
            }
        }
        else
        {
            if ( getLogger().isErrorEnabled() )
            {
                getLogger().error( "Command failed: " + command, throwable );
            }
        }
        
        // This handler never requests that commands cease to be processed.
        return false;
    }
}

