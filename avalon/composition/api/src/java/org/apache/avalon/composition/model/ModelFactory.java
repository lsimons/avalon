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

package org.apache.avalon.composition.model;

import java.net.URL;

import org.apache.avalon.composition.data.ContainmentProfile;

/**
 * A factory enabling the establishment of new containment model instances.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.5 $ $Date: 2004/02/07 14:03:42 $
 */
public interface ModelFactory 
{
   /**
    * Creation of a new root containment model.
    *
    * @param url a url of a containment profile 
    * @return the containment model
    * @exception ModelException if an error occurs during model establishment
    */
    ContainmentModel createContainmentModel( URL url ) 
      throws ModelException;

   /**
    * Creation of a new root containment model.
    *
    * @param profile a containment profile 
    * @return the containment model
    * @exception ModelException if an error occurs during model establishment
    */
    ContainmentModel createContainmentModel( ContainmentProfile profile ) 
      throws ModelException;

   /**
    * Creation of a new root containment context.
    *
    * @param profile a composition profile 
    * @return the containment model
    */
    //ContainmentContext createContainmentContext( ContainmentProfile profile ) 
    //  throws ModelException;

   /**
    * Creation of a new nested containment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign containment context
    * @return the containment model
    */
    //ContainmentModel createContainmentModel( ContainmentContext context )
    //  throws ModelException;

   /**
    * Creation of a new nested deployment model.  This method is called
    * by a container implementation when constructing model instances.  The 
    * factory is identified by its implementation classname.
    *
    * @param context a potentially foreign deployment context
    * @return the deployment model
    */
    //ComponentModel createComponentModel( ComponentContext context )
    //  throws ModelException;

}
