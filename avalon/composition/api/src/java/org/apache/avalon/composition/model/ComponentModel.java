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

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.Type;

/**
 * Deployment model defintion.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/24 23:25:25 $
 */
public interface ComponentModel extends DeploymentModel
{

   /**
    * Return the deployment type.
    * 
    * @return the type
    */
    Type getType();

   /**
    * Return the activation policy for the model. 
    * @return the activaltion policy
    */
    boolean getActivationPolicy();

   /**
    * Return the collection policy for the model. If a profile
    * does not declare a collection policy, the collection policy 
    * declared by the type will be used.
    *
    * @return the collection policy
    * @see org.apache.avalon.meta.info.InfoDescriptor#WEAK
    * @see org.apache.avalon.meta.info.InfoDescriptor#SOFT
    * @see org.apache.avalon.meta.info.InfoDescriptor#HARD
    */
    int getCollectionPolicy();

   /**
    * Set the collection policy to a supplied value.
    *
    * @param policy the collection policy
    * @see org.apache.avalon.meta.info.InfoDescriptor#WEAK
    * @see org.apache.avalon.meta.info.InfoDescriptor#SOFT
    * @see org.apache.avalon.meta.info.InfoDescriptor#HARD
    * @see org.apache.avalon.meta.info.InfoDescriptor#UNDEFINED
    */
    void setCollectionPolicy( int policy );

   /**
    * Return the logging categories. 
    * @return the logging categories
    */
    CategoriesDirective getCategories();

   /**
    * Set categories. 
    * @param categories the logging categories
    */
    void setCategories( CategoriesDirective categories );

   /**
    * Set the activation policy for the model. 
    * @param policy the activaltion policy
    */
    void setActivationPolicy( boolean policy );

   /**
    * Set the activation policy for the model to the default value. 
    */
    void revertActivationPolicy();

   /**
    * Return the class for the deployable target.
    * @return the class
    */
    Class getDeploymentClass();

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration will replace the existing configuration.
    *
    * @param config the supplied configuration
    */
    void setConfiguration( Configuration config );

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration may suppliment or replace the existing configuration.
    *
    * @param config the supplied configuration
    * @param policy if TRUE the supplied configuration replaces the current
    *   configuration otherwise the resoved configuration shall be layed above
    *   the configuration supplied with the profile which in turn is layer above 
    *   the type default configuration (if any)
    */
    void setConfiguration( Configuration config, boolean policy );

   /**
    * Return the configuration to be applied to the component.
    * The implementation returns the current configuration state.
    * If the the component type does not implementation the 
    * Configurable interface, the implementation returns null. 
    *
    * @return the qualified configuration
    */
    Configuration getConfiguration();

   /**
    * Test if the component type backing the model is 
    * parameterizable.
    *
    * @return TRUE if the component type is parameterizable
    *   otherwise FALSE
    */
    public boolean isParameterizable();

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value will replace the existing parameters value.
    *
    * @param parameters the supplied parameters value
    */
    public void setParameters( Parameters parameters );

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value may suppliment or replace the existing 
    * parameters value.
    *
    * @param parameters the supplied parameters
    * @param policy if TRUE the supplied parameters replaces the current
    *   parameters value otherwise the existing and supplied values
    *   are aggregrated
    */
    void setParameters( Parameters parameters, boolean policy );

   /**
    * Return the configuration to be applied to the component.
    * The implementation returns the current configuration state.
    * If the the component type does not implementation the 
    * Configurable interface, the implementation returns null. 
    *
    * @return the qualified configuration
    */
    Parameters getParameters();

   /**
    * Rest if the component type backing the model requires the 
    * establishment of a runtime context.
    *
    * @return TRUE if the component type requires a runtime
    *   context otherwise FALSE
    */
    boolean isContextDependent();

   /**
    * Return the context model for this deployment model.
    * 
    * @return the context model if this model is context dependent, else
    *   the return value is null
    */
    ContextModel getContextModel();

   /**
    * Return the dependency models for this deployment model.
    *
    * @return the dependency models
    */
    DependencyModel[] getDependencyModels();

   /**
    * Return a dependency model for a supplied descriptor or null
    * if no match found.
    *
    * @return the dependency model
    */
    DependencyModel getDependencyModel( DependencyDescriptor dependency );

   /**
    * Return the stage models for this deployment model.
    *
    * @return the stage models
    */
    StageModel[] getStageModels();

   /**
    * Return a stage model matching the supplied descriptor or null
    * if no match found.
    *
    * @param stage the stage descriptor
    * @return the matching stage model
    */
    StageModel getStageModel( StageDescriptor stage );

   /**
    * Return the set of services produced by the model as a array of classes.
    *
    * @return the service classes
    */
    Class[] getInterfaces();

}
