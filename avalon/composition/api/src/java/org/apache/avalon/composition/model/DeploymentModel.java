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

import java.util.List;

import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.composition.model.Commissionable;
import org.apache.avalon.composition.model.Resolver;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

import org.apache.avalon.framework.logger.Logger;

/**
 * Model desribing a deployment scenario.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.14 $ $Date: 2004/02/12 05:59:41 $
 */
public interface DeploymentModel extends Commissionable, Resolver
{
    String SEPARATOR = "/";

    String DEPLOYMENT_TIMEOUT_KEY = "urn:composition:deployment.timeout";

   /**
    * Return the name of the model.
    * @return the name
    */
    String getName();

   /**
    * Return the model partition path.
    * @return the path
    */
    String getPath();

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    String getQualifiedName();

   /**
    * Return the mode of model establishment.
    * @return the mode
    */
    Mode getMode();

   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    Logger getLogger();

    //-----------------------------------------------------------
    // service production
    //-----------------------------------------------------------
    
   /**
    * Return the set of services produced by the model.
    * @return the services
    */
    ServiceDescriptor[] getServices();

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * depedendency.
    * @return true if this model can fulfill the dependency
    */
    boolean isaCandidate( DependencyDescriptor dependency );

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * stage dependency.
    * @return true if this model can fulfill the dependency
    */
    boolean isaCandidate( StageDescriptor stage );

    //-----------------------------------------------------------
    // composite assembly
    //-----------------------------------------------------------

    /**
     * Returns the assembled state of the model.
     * @return true if this model is assembled
     */
    boolean isAssembled();

    /**
     * Assemble the model.
     * @param subjects a list of deployment models that make up the assembly chain
     * @exception Exception if an error occurs during model assembly
     */
    void assemble( List subjects ) throws AssemblyException;

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    DeploymentModel[] getConsumerGraph();

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    DeploymentModel[] getProviderGraph();

    /**
     * Disassemble the model.
     */
    void disassemble();

    /**
     * Return the set of models assigned as providers.
     * @return the providers consumed by the model
     * @exception IllegalStateException if invoked prior to 
     *    the completion of the assembly phase 
     */
    DeploymentModel[] getProviders();

   /** 
    * Return the default deployment timeout value declared in the 
    * kernel configuration.  The implementation looks for a value
    * assigned under the property key "urn:composition:deployment.timeout"
    * and defaults to 1000 msec if undefined.
    *
    * @return the default deployment timeout value
    */
   long getDeploymentTimeout();

}
