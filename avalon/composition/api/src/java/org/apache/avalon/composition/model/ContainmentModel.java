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

import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.event.CompositionEventListener;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * Containment model is an extended deployment model that aggregates 
 * a set of models.  A containment model describes a logical containment 
 * context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.14 $ $Date: 2004/02/06 15:27:14 $
 */
public interface ContainmentModel extends DeploymentModel
{
    String KEY = "urn:composition:containment.model";
    
    String SECURE_EXECUTION_KEY = "urn:composition:security.enabled";

    /**
     * Get the startup sequence for the model.
     */
    DeploymentModel[] getStartupGraph();

    /**
     * Get the shutdown sequence for the model.
     */
    DeploymentModel[] getShutdownGraph();

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
    * Return the partition established by the containment model.
    *
    * @return the partition name
    */
    String getPartition();

   /**
    * Return the classloader model.
    *
    * @return the classloader model
    */
    ClassLoaderModel getClassLoaderModel();
    
   /**
    * Returns true if Secure Execution mode has been enabled in the kernel.
    * 
    * Secure Execution mode enables the deployer to restrict the exection
    * environment, and this flag allows for developers to quickly switch
    * between the secure and non-secure execution modes.
    * 
    * @return true if Secure Execution mode has been enabled in the kernel.
    **/ 
    boolean isSecureExecutionEnabled();
    
   /** 
    * Return the default deployment timeout value declared in the 
    * kernel configuration.  The implementation looks for a value
    * assigned under the property key "urn:composition:deployment.timeout"
    * and defaults to 1000 msec if undefined.
    *
    * @return the default deployment timeout value
    */
   long getDeploymentTimeout();

   /**
    * Return the set of models nested within this model.
    * @return the classloader model
    */
    DeploymentModel[] getModels();

   /**
    * Return a model relative to a supplied name.
    * @return the named model or null if the name is unknown
    */
    DeploymentModel getModel( String name );

   /**
    * Addition of a new subsidiary model within
    * the containment context using a supplied profile url.
    *
    * @param url a containment profile url
    * @return the model based on the derived profile
    * @exception ModelException if an error occurs during model establishment
    */
    DeploymentModel addModel( URL url ) throws ModelException;

   /**
    * Addition of a new subsidiary containment model within
    * the containment context using a supplied url.
    *
    * @param block a url referencing a containment profile
    * @param config containment configuration targets
    * @return the model created using the derived profile and configuration
    * @exception ModelException if an error occurs during model establishment
    */
    ContainmentModel addContainmentModel( URL block, URL config ) 
      throws ModelException;

   /**
    * Addition of a new subsidiary model within
    * the containment context using a supplied profile.
    *
    * @param profile a containment or deployment profile 
    * @return the model based on the supplied profile
    * @exception ModelException if an error occurs during model establishment
    */
    DeploymentModel addModel( DeploymentProfile profile ) throws ModelException;

   /**
    * Removal of a named model for the containment model.
    *
    * @param name the name of the subsidiary model to be removed
    */
    void removeModel( String name );


   /**
    * Return the set of service export models.
    * @return t he export directives
    */
    ServiceModel[] getServiceModels();

   /**
    * Return a service exoport model matching a supplied class.
    * @return the service model
    */
    ServiceModel getServiceModel( Class clazz );

   /**
    * Apply a set of override targets resolvable from a supplied url.
    * @param url a url resolvable to a TargetDirective[]
    * @exception ModelException if an error occurs
    */
    void applyTargets( URL url )
      throws ModelException;

   /**
    * Apply a set of override targets.
    * @param targets a set of target directives
    */
    void applyTargets( TargetDirective[]targets );

   /**
    * Add a composition listener to the model.
    * @param listener the composition listener
    */
    void addCompositionListener( CompositionEventListener listener );

   /**
    * Remove a composition listener from the model.
    * @param listener the composition listener
    */
    void removeCompositionListener( CompositionEventListener listener );
}
