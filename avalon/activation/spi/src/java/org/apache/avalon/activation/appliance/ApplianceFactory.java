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

import org.apache.avalon.composition.model.DeploymentModel;

/**
 * An appliance factory is a service that provides support for the creation
 * of new {@link Appliance} instances.  An appliance factory is responsible
 * for the creation of a particular type of appliance, reflecting the
 * component model and deployment strategy of the component that the appliance
 * type is based on.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:25:23 $
 */

public interface ApplianceFactory
{
    /**
     * Create a new appliance.
     * @param model the meta-model describing a deployment scenario
     * @return the appliance for scenario deployment
     * @exception ApplianceException if an appliance creation failure occurs
     */
    Appliance createAppliance( DeploymentModel model )
      throws ApplianceException;

}
