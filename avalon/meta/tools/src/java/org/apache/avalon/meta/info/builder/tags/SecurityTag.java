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

package org.apache.avalon.meta.info.builder.tags;

import java.util.Properties;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.avalon.framework.Version;
import org.apache.avalon.meta.info.SecurityDescriptor;
import org.apache.avalon.meta.info.PermissionDescriptor;
import org.apache.avalon.meta.info.Service;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * A doclet tag the declares a service definition.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/24 22:33:44 $
 */
public class SecurityTag extends AbstractTag
{
   /**
    * Javadoc tag key for the name tag.
    */
    public static final String KEY = "security";

   /**
    * Javadoc tag key for the permission tag.
    */
    public static final String PERMISSION_KEY = KEY + ".permission";

   /**
    * The version parameter
    */
    public static final String CLASSNAME_PARAM = "class";

   /**
    * The version parameter
    */
    public static final String NAME_PARAM = "name";

   /**
    * The version parameter
    */
    public static final String ACTIONS_PARAM = "actions";

   /**
    * Type tag constructor.
    * @param clazz the javadoc class descriptor
    */
    public SecurityTag( final JavaClass clazz )
    {
        super( clazz );
    }

    public SecurityDescriptor getSecurityDescriptor()
    {
        PermissionDescriptor[] permissions = getPermissions();
        return new SecurityDescriptor( permissions, null );
    }

    /**
     * Return the array of dependency descriptors based on the set of
     * 'dependency' tags associated with the components compose or service method.
     * @return the set of dependencies
     */
    public PermissionDescriptor[] getPermissions()
    {
        final ArrayList permissions = new ArrayList();
        final DocletTag[] tags = 
          getJavaClass().getTagsByName( getNS() + Tags.DELIMITER + PERMISSION_KEY );
        for( int i = 0; i < tags.length; i++ )
        {
            permissions.add( getPermission( tags[i] ) );
        }
        return (PermissionDescriptor[])permissions.toArray( 
          new PermissionDescriptor[ permissions.size() ] );
    }

   /**
    * Return the value of the Avalon 'service' tag.
    * @return the service descriptor or null if no service is declared
    */
    public PermissionDescriptor getPermission( DocletTag tag )
    {
        final String classname = getNamedParameter( tag, CLASSNAME_PARAM );
        final String name = getNamedParameter( tag, NAME_PARAM, null );
        final String actionString = getNamedParameter( tag, ACTIONS_PARAM, null );
        final String[] actions = expandActions( actionString );
        return new PermissionDescriptor( classname, name, actions );
    }

    private static String[] expandActions( String arg )
    {
        if( null == arg ) return new String[0];
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( arg, "," );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

}
