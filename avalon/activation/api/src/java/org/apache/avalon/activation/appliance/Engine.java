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

import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * The Engine interface defines the contract for service that provide
 * appliance resolution.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/01/24 23:25:20 $
 */
public interface Engine
{
   /**
    * Return an appliance relative to a supplied path.
    * @param path the appliance path
    * @return the appliance
    */
    Appliance locate( String path );

   /**
    * Return an appliance relative to a supplied model.
    * @param model the meta model
    * @return the appliance
    */
    Appliance locate( DeploymentModel model );

}
