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

package org.apache.avalon.activation;

import org.apache.avalon.composition.model.DeploymentModel;

/**
 * A factory that provides object instantiation. A instance factory will
 * typically encapsulate component constuction semantic, lifecycle processing
 * and end-of-life processing.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:18:42 $
 */
public interface ComponentFactory 
{
   /**
    * Creation of a new instance including all deployment stage handling.
    * @return the new instance
    */
    Object incarnate() throws Exception;

   /**
    * Termination of the instance including all end-of-life processing.
    * @param instance the component instance
    */
    void etherialize( Object instance );

}
