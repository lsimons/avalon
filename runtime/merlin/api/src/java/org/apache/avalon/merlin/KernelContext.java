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

package org.apache.avalon.merlin;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.composition.model.ContainmentModel;

/**
 * The context argument supplied to a new kernel instance.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/03/17 10:39:11 $
 */
public interface KernelContext
{
   /**
    * Return the assigned logging channel.
    * @return the loggging channel
    */
    Logger getLogger();

   /**
    * Return the application model.
    * @return the root application model 
    */
    ContainmentModel getApplicationModel();

}
