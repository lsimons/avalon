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

package org.apache.avalon.activation.lifecycle;

import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.composition.model.ComponentModel;

public interface CreationPhase
{
    /**
     * Invocation of the deployment creation cycle.
     * @param model the model representing the object under deployment
     * @param engine the engine that drives the lifecycle
     * @param object the object under deployment
     * @exception if a deployment error occurs
     */
     void create( Engine engine, ComponentModel model, Object object)
       throws CreationException;

}
