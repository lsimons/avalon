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

import org.apache.avalon.activation.lifecycle.LifecycleException;
import org.apache.avalon.composition.model.ComponentModel;

/**
 * The Factory interface exposes an operation though which a 
 * new component instance may be accessed.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:25:23 $
 */
public interface Factory
{
   /**
    * Return the component deployment model. 
    *
    * @exception LifecycleException
    */
    ComponentModel getComponentModel();

   /**
    * Create a new instance of a component. 
    *
    * @exception LifecycleException
    */
    Object newInstance() throws LifecycleException;

   /**
    * Decommission and dispose of the supplied component. 
    *
    * @param instance the object to decommission
    */
    void destroy( Object instance );

}
