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

package org.apache.avalon.fortress.test.data;

/**
 * @avalon.component
 * @avalon.service type=org.apache.avalon.fortress.test.data.Role2
 * @x-avalon.lifestyle type=pooled
 * @x-avalon.info name=component2
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.13 $ $Date: 2004/02/28 15:16:26 $
 */
public class Component2
    implements Role2
{
    public long getID()
    {
        return hashCode();
    }
}
