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
import org.apache.avalon.excalibur.logger.util.LoggerUtil;

/**
 * This class implements LoggerManager interface by
 * prepending a prefix to all requests and letting the
 * wrapped LoggerManager actually create the loggers.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/10/02 19:18:44 $
 * @since 4.0
 */
public class PrefixDecorator extends LoggerManagerDecorator implements LoggerManager
{
    private final String m_prefix;

    /**
     * Creates a PrefixDecorator instance.
     * @param prefix the prefix to prepend; 
     *         can be neither null nor empty. 
     *         This is done to avoid ambiguity 
     *         in the getDefaultLogger() method - what would we call
     *         in such case getDefaultLogger() or getLoggerForCategory("") ?
     *
     */
    public PrefixDecorator( final LoggerManager loggerManager, final String prefix )
    {
        super( loggerManager );
        if ( prefix == null ) throw new NullPointerException( "prefix" );
        if ( "".equals( prefix ) ) throw new IllegalArgumentException( "prefix can't be empty" );
        m_prefix = prefix;
    }

    /**
     * Return the Logger for the specified category.
     */
    public Logger getLoggerForCategory( final String categoryName )
    {
        final String fullCategoryName = LoggerUtil.getFullCategoryName( m_prefix, categoryName );
        return m_loggerManager.getLoggerForCategory( fullCategoryName );
    }

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category. 
     */
    public Logger getDefaultLogger()
    {
        final String fullCategoryName = LoggerUtil.getFullCategoryName( m_prefix, null );
        return m_loggerManager.getLoggerForCategory( fullCategoryName );
    }
}
