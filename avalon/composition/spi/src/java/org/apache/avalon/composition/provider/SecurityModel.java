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

package org.apache.avalon.composition.provider;

import java.security.Permission;
import java.security.Permissions;

import org.apache.avalon.composition.data.GrantDirective;


/**
 * <p>Specification of a security model.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/29 22:25:26 $
 */
public interface SecurityModel
{
   /**
    * Return the name of the security model.
    * @return the model name
    */
    String getName();

   /**
    * Return the set of default permissions.
    * 
    * @return the permissions
    */
    Permissions getPermissions();

}