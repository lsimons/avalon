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

/**
 * The Deployable interface defines the contract for an object 
 * that can be deployed.  Deployment at this level of abstract 
 * concerns the handling of actions proceeding component 
 * resolution for a particula appliance instance.  
 * Deployment is equivalent to the notion of initialization.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:20 $
 */
public interface Deployable
{
   /**
    * Commission the appliance. 
    *
    * @exception Exception if a deployment error occurs
    */
    void deploy() throws Exception;

   /**
    * Invokes the decommissioning phase.  Once a appliance is 
    * decommissioned it may be re-commissioned.
    */
    void decommission();

}
