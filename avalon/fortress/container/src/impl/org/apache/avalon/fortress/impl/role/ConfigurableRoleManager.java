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
package org.apache.avalon.fortress.impl.role;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;

/**
 * Configurable RoleManager implementation.  It populates the RoleManager
 * from a configuration hierarchy.  This is based on the DefaultRoleManager
 * in the org.apache.avalon.component package.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/02/07 16:08:12 $
 * @since 4.1
 */
public class ConfigurableRoleManager
    extends org.apache.avalon.fortress.impl.role.AbstractRoleManager
    implements Configurable
{
    /**
     * Default constructor--this RoleManager has no parent.
     */
    public ConfigurableRoleManager()
    {
        super( null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent.
     *
     * @param parent  The parent <code>RoleManager</code>.
     */
    public ConfigurableRoleManager( org.apache.avalon.fortress.RoleManager parent )
    {
        super( parent, null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent and a classloader.
     *
     * @param parent The parent <code>RoleManager</code>.
     * @param loader the classloader
     */
    public ConfigurableRoleManager( org.apache.avalon.fortress.RoleManager parent, ClassLoader loader )
    {
        super( parent, loader );
    }

    /**
     * Reads a configuration object and creates the role, shorthand,
     * and class name mapping.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] roles = configuration.getChildren( "role" );

        for( int i = 0; i < roles.length; i++ )
        {
            final String role = roles[ i ].getAttribute( "name" );
            Configuration[] components = roles[ i ].getChildren( "component" );

            for( int j = 0; j < components.length; j++ )
            {
                final String shorthand = components[ j ].getAttribute( "shorthand" );
                final String className =
                    components[ j ].getAttribute( "class", null );
                final String handlerClassName =
                    components[ j ].getAttribute( "handler",
                                                  org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler.class.getName() );

                addRole( shorthand, role, className, handlerClassName );
            }
        }
    }
}
