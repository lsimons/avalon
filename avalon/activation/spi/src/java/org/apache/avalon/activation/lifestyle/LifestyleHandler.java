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

package org.apache.avalon.activation.lifestyle;

/**
 * A lifestyle handler provides support for a particular lifestyle policy.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:25:23 $
 */
public interface LifestyleHandler
{
    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    Object resolve() throws Exception;

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    void release( Object instance );

    /**
     * Release an object
     *
     * @param instance the object to be released
     * @param finalized if TRUE the lifestyle handler cannot reuse the instance
     */
    void release( Object instance, boolean finalized );

}
