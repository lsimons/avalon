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
package org.apache.excalibur.xfc.modules.ecm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Instance;
import org.apache.excalibur.xfc.model.RoleRef;

import org.apache.excalibur.xfc.modules.Constants;

/**
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: HandlerAnalyzer.java,v 1.1 2002/10/16 16:20:38 crafterm Exp $
 */
public final class HandlerAnalyzer implements Constants
{
    // Avalon Framework and Excalibur Pool markers
    private static final String SINGLETHREADED =
        "org.apache.avalon.framework.thread.SingleThreaded";
    private static final String THREADSAFE =
        "org.apache.avalon.framework.thread.ThreadSafe";
    private static final String POOLABLE =
        "org.apache.avalon.excalibur.pool.Poolable";
    private static final String RECYCLABLE =
        "org.apache.avalon.excalibur.pool.Recyclable";

    private static final String COMPONENT_INSTANCE = "component-instance";

    private static Map m_handlers = new HashMap();

    // Normalized mappings for ECM lifestyles
    static
    {
        // ECM -> Normalized
        m_handlers.put( SINGLETHREADED, TRANSIENT );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( RECYCLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );
    }

    /**
     * Method for extracting a role's ComponentHandler name,
     * ECM style. ECM roles don't define ComponentHandlers explicitly,
     * so some simple class analysis is made in this method to
     * try to ascertain which handler has been chosed by the 
     * implementor.
     *
     * @param classname class name as a <code>String</code> value
     * @return normalized handler name
     * @exception Exception if an error occurs
     */
    public static String getHandler( final String classname )
        throws Exception
    {
        try
        {
            Class clazz = Class.forName( classname );
            String handler = getNormalizedHandlerName( clazz );

            if ( handler != null )
            {
                return handler;
            }

            /*
            if ( getLogger().isInfoEnabled() )
            {
                getLogger().info(
                    "No known component handler discovered on " + clazz.getName() +
                    ", defaulting to 'transient'"
                );
            }
            */

            return TRANSIENT;
        }
        catch ( ClassNotFoundException e )
        {
            /*
            if ( getLogger().isWarnEnabled() )
            {
                getLogger().warn(
                    "Could not load Class " + classname +
                    " for Component Handler analysis, defaulting to 'transient'"
                );
            }
            */

            /* leave out for the moment
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Exception: ", e );
            }
            */

            return TRANSIENT;
        }
    }

    /**
     * Method to find out the normalized handler name for a given Class
     * object. It checks all interfaces implemented by the given Class,
     * and all subinterfaces of those interfaces, recursively, until
     * either a known component marker is found, or all interfaces
     * are exhausted.
     *
     * @param interfaze a <code>Class</code> instance
     * @return normalized handler name
     */
    private static String getNormalizedHandlerName( final Class interfaze )
    {
        // get all interfaces implemented by this Class
        Class[] interfaces = interfaze.getInterfaces();

        for ( int i = 0; i < interfaces.length; ++i )
        {
            // check if this interface is a known component marker
            if ( m_handlers.containsKey( interfaces[i].getName() ) )
            {
                return (String) m_handlers.get( interfaces[i].getName() );
            }

            // if it's unknown, check for any subinterfaces and recurse
            String handler = getNormalizedHandlerName( interfaces[i] );

            // if a subinterface is known, return
            if ( handler != null )
                return handler;
        }

        // all interfaces unknown
        return null;
    }
}
