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

package org.apache.avalon.finder.ecm;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.finder.ecm.info.Role;
import org.apache.avalon.finder.ecm.info.Hint;

/**
 * The default implementation of an ECM RoleManager.
 *
 * @avalon.component name="roles" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.finder.ecm.RoleManager"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:55 $
 */
public class DefaultRoleManager implements RoleManager
{
    //-----------------------------------------------------------
    // immutable state
    //-----------------------------------------------------------

    private final Role[] m_roles;

    //-----------------------------------------------------------
    // constructor
    //-----------------------------------------------------------

   /**
    * Creationn of a new role manager.
    *
    * @param config a configuration containing a set of &lt;role&dt;
    *    elements from which a roles model may be constructed
    * @exception ConfigurationException if an error occurs while 
    *    parsing configuration content relative to the role format.
    */
    public DefaultRoleManager( Configuration config ) 
      throws ConfigurationException
    {
        Configuration[] children = config.getChildren( "role" );
        Role[] roles = new Role[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
             Configuration child = children[i];
             Role role = buildRole( child );
             roles[i] = role;
        }
        m_roles = roles;
    }

    //-----------------------------------------------------------
    // RoleManager
    //-----------------------------------------------------------

    public Role getNamedRole( String name )
    {
        for( int i=0; i<m_roles.length; i++ )
        {
            Role role = m_roles[i];
            if( role.getName().equals( name ) )
            {
                return role;
            }
        }
        return null;
    }

    //-----------------------------------------------------------
    // internals
    //-----------------------------------------------------------

    private Role buildRole( Configuration config )
      throws ConfigurationException
    {
        String name = config.getAttribute( "name" );
        String shorthand = config.getAttribute( "shorthand" );
        String defaultClassname = config.getAttribute( "default-class" );
        Configuration[] children = config.getChildren( "hint" );
        Hint[] hints = new Hint[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
             Configuration child = children[i];
             Hint hint = buildHint( child );
             hints[i] = hint;
        }
        return new Role( name, shorthand, defaultClassname, hints );
    }

    private Hint buildHint( Configuration config )
      throws ConfigurationException
    {
        String shorthand = config.getAttribute( "shorthand" );
        String classname = config.getAttribute( "class" );
        return new Hint( shorthand, classname );
    }
}
