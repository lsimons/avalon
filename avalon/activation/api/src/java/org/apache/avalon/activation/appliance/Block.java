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

package org.apache.avalon.activation.appliance;

import org.apache.avalon.composition.model.ContainmentModel;

/**
 * A block is an appliance that manages a set of subsidiary 
 * appliance instances.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/01/24 23:25:20 $
 */
public interface Block extends Appliance, Engine
{
   /**
    * Return the containment metamodel associated with the block.
    * @return the containment model
    */
    ContainmentModel getContainmentModel();

}
