/* 
 * Copyright 2003-2004 The Apache Software Foundation
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

package org.apache.avalon.fortress.impl.role;

import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.role.AbstractRoleManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This role manager implementation is able to read ECM based role files. 
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.1 $ $Date: 2004/04/03 18:10:35 $
 */
public class ECMRoleManager
        extends AbstractRoleManager
        implements Configurable
{
    
    /**
     * Default constructor--this RoleManager has no parent.
     */
    public ECMRoleManager() 
    {
        super( null, null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * classloader.
     *
     * @param loader  The <code>ClassLoader</code> used to resolve class names.
     */
    public ECMRoleManager( final ClassLoader loader ) 
    {
        super( null, loader );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent.
     *
     * @param parent  The parent <code>RoleManager</code>.
     */
    public ECMRoleManager( final RoleManager parent ) 
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
    public ECMRoleManager( final RoleManager parent, 
                            final ClassLoader loader ) 
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

        for ( int i = 0; i < roles.length; i++ ) 
        {
            final String role = roles[i].getAttribute( "name" );
            final String shorthand = roles[i].getAttribute( "shorthand" );
            final String className = roles[i].getAttribute( "default-class", null );
            
            if ( ! addRole( shorthand, role, className, null ) ) 
            {
                
                final String message = "Configuration error on invalid entry:\n\tRole: " + role +
                        "\n\tShorthand: " + shorthand +
                        "\n\tDefault Class: " + className;

                getLogger().warn(message);
            }
        }
    }
}
