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
package org.apache.avalon.excalibur.logger.decorator;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.excalibur.logger.LoggerManager;

/**
 * Overrides the value passed from getDefaultLogger().
 *
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/10/02 19:18:43 $
 * @since 4.0
 */
public class OverrideDefaultDecorator extends LoggerManagerDecorator
{
    /**
     * The override value for getDefaultLogger() and for 
     * getLoggerForCategory(""), getLoggerForCategory( null );
     */
    private final Logger m_defaultLogger;

    /**
     * Creates an <code>OverrideDecorator</code> instance.
     * @param <code>OverrideDecorator</code> is unique in that
     *        it won't tolerate a null extra argument: if this
     *        argument is <code>null</code> a NullPointerException will
     *        be thrown. This ensures that no logging surprises will occur.
     */
    public OverrideDefaultDecorator( 
            final LoggerManager loggerManager, final Logger defaultLogger )
    {
        super( loggerManager );
        if ( defaultLogger == null ) throw new NullPointerException( "defaultLogger" );
        m_defaultLogger = defaultLogger;
    }

    /**
     * Return the Logger for the specified category.
     */
    public Logger getLoggerForCategory( final String categoryName )
    {
        if ( categoryName == null || categoryName.length() == 0 )
        {
            return m_defaultLogger;
        }
        else
        {
            return m_loggerManager.getLoggerForCategory( categoryName );
        }
    }

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    public Logger getDefaultLogger()
    {
        return m_defaultLogger;
    }
}
