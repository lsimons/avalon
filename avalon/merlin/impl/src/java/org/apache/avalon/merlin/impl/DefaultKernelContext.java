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

package org.apache.avalon.merlin.impl;

import java.net.URL;
import java.io.File;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.composition.model.ContainmentModel;

import org.apache.avalon.merlin.KernelContext;

/**
 * The context argument supplied to a new kernel instance.
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:31 $
 */
public class DefaultKernelContext implements KernelContext
{
    private final Logger m_logger;
    private final ContainmentModel m_facilities;
    private final ContainmentModel m_application;

   /**
    * Creation of a new default kernel context.
    * @param logger the logging channel to be assigned to the kernel
    * @param facilities the internal facilities model
    * @param application the application model
    */
    public DefaultKernelContext( 
      final Logger logger, final ContainmentModel facilities, 
      final ContainmentModel application )
    {
        m_logger = logger;
        m_facilities = facilities;
        m_application = application;
    }
    
   /**
    * Return the assigned logging channel.
    * @return the loggging channel
    */
    public Logger getLogger()
    {
        return m_logger;
    }

   /**
    * Return the facilities model.
    * @return the internal container facilities
    */
    public ContainmentModel getFacilitiesModel()
    {
        return m_facilities;
    }

   /**
    * Return the application model.
    * @return the root application model 
    */
    public ContainmentModel getApplicationModel()
    {
        return m_application;
    }
}
