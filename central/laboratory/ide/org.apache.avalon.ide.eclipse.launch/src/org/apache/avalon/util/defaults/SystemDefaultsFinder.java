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

package org.apache.avalon.util.defaults;


import java.util.Properties;


/**
 * Finds default property values within the system properties.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: oberhack $
 * @version $Revision: 1.1 $
 */
public class SystemDefaultsFinder extends SimpleDefaultsFinder
{
    /**
     * Finds default property values within the system properties.
     */
    public SystemDefaultsFinder()
    {
        super( new Properties [] { System.getProperties() }, true ) ;
    }
}
