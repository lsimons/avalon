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

import org.apache.avalon.finder.ecm.info.Role;

/**
 * A service interface that defines a set of management operations
 * that can be performed relative to the information expressed in 
 * an ECM style roles.xml file.
 *
 * @avalon.component name="roles" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.finder.ecm.RoleManager"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/04 15:00:55 $
 */
public interface RoleManager
{
    Role getNamedRole( String name );
}
