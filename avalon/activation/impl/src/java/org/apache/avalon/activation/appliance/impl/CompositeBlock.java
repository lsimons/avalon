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

package org.apache.avalon.activation.appliance.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRuntimeException;
import org.apache.avalon.activation.appliance.Home;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;

/**
 * The CompositeBlock is responsible for the management 
 * of the assembly of the subsidiary appliances, the coordination
 * of the deployment, decommissioning and eventual dissassembly of 
 * contained appliances, and the overall management of a containment 
 * context.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.8 $ $Date: 2004/01/24 23:25:21 $
 */
public class CompositeBlock extends DefaultBlock
{
    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new block.
    *
    * @param context the block context
    * @exception ApplianceException if a block creation error occurs
    */
    CompositeBlock( ContainmentModel model )
    {
        super( model );
    }

}
