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

package org.apache.avalon.composition.data;

import java.io.Serializable;

/**
 * Description of classpath.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:24 $
 */
public final class GrantDirective implements Serializable
{
     private static final PermissionDirective[] EMPTY_PERMISSIONSETS = new PermissionDirective[0]; 

    /**
     * The permission directives
     */
    private PermissionDirective[] m_permissions;


    public GrantDirective()
    {
        this( null );
    }
    
    /**
     * Create a GrantDirective instance.
     *
     * @param permissions the permissions to be included in the grant
     */
    public GrantDirective( final PermissionDirective[] permissions )
    {
        if( permissions == null )
        {
            m_permissions = EMPTY_PERMISSIONSETS;
        }
        else
        {
            m_permissions = permissions;
        }
    }

   /**
    * Return the default status of this directive.  
    * 
    * If TRUE the enclosed permission directives are empty.
    */
    public boolean isEmpty()
    {
        return m_permissions.length == 0;
    }

    /**
     * Return the set of permission directives.
     *
     * @return the permission directives
     */
    public PermissionDirective[] getPermissionDirectives()
    {
        return m_permissions;
    }
}
