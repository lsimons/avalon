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
package org.apache.excalibur.xfc.modules.fortress;

import java.util.HashMap;
import java.util.Map;
import org.apache.excalibur.xfc.modules.Constants;

/**
 * Simple class providing normalized mappings for Fortress Component
 * handler names.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: HandlerMapper.java,v 1.3 2002/11/12 19:55:28 donaldp Exp $
 */
public final class HandlerMapper implements Constants
{
    // ComponentHandler constants
    private static final String FACTORY =
        "org.apache.excalibur.fortress.handler.FactoryComponentHandler";
    private static final String PERTHREAD =
        "org.apache.excalibur.fortress.handler.PerThreadComponentHandler";
    private static final String POOLABLE =
        "org.apache.excalibur.fortress.handler.PoolableComponentHandler";
    private static final String THREADSAFE =
        "org.apache.excalibur.fortress.handler.ThreadSafeComponentHandler";

    // Map of fortress/type handlers
    private static final Map m_handlers = new HashMap();

    // Normalized mappings for Fortress component handlers
    static
    {
        // Fortress -> Normalized
        m_handlers.put( FACTORY, TRANSIENT );
        m_handlers.put( PERTHREAD, THREAD );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );

        // Normalized -> Fortress
        m_handlers.put( TRANSIENT, FACTORY );
        m_handlers.put( THREAD, PERTHREAD );
        m_handlers.put( POOLED, POOLABLE );
        m_handlers.put( SINGLETON, THREADSAFE );
    }

    /**
     * Method for extracting a role's ComponentHandler name.
     *
     * @return the role's normalized handler name
     */
    public static String getHandler( final String handler )
    {
        return getLifestyleType( handler, FACTORY );
    }

    /**
     * Helper method to convert known Fortress ComponentHandler types to meta
     * REVISIT: meta should define transient/thread/pooled/etc as constants.
     *
     * @param handler a <code>String</code> value
     * @param defaultValue a <code>String</code> default value if handler
     *                     type cannot be found
     * @return a <code>String</code> value
     */
    private static String getLifestyleType( String handler, String defaultValue )
    {
        if( handler != null )
        {
            String type = (String)m_handlers.get( handler );

            if( type != null )
                return type;
        }

        /*
        if ( getLogger().isWarnEnabled() )
        {
            getLogger().warn(
                "Custom or unknown handler " + handler +
                " defined, defaulting to " + defaultValue
            );
        }
        */

        return defaultValue;
    }
}
