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

import org.apache.avalon.activation.appliance.Deployable;

import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.AssemblyException;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

import org.apache.avalon.framework.logger.Logger;


public class DummyDeploymentModel 
    implements DeploymentModel
{
    private long m_timeout;
    private Deployable m_deployable;

    DummyDeploymentModel( Deployable deployable, long timeout )
    {
        m_timeout = timeout;
        m_deployable = deployable;
    }
    
   /**
    * Return the name of the model.
    * @return the name
    */
    public String getName()
    {
        return "dummy";
    }

   /**
    * Return the model partition path.
    * @return the path
    */
    public String getPath()
    {
        return "/dummy";
    }

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    public String getQualifiedName()
    {
        return "/dummy";
    }

   /**
    * Return the mode of model establishment.
    * @return the mode
    */
    public Mode getMode()
    {
        return null;
    }

   /**
    * Set the runtime handler for the model.
    * @param handler the runtime handler
    */
    public void setHandler( Object handler )
    {
    }

   /**
    * Get the assigned runtime handler for the model.
    * @return the runtime handler
    */
    public Object getHandler()
    {
        return m_deployable;
    }

   /**
    * Return the assigned logging channel.
    * @return the logging channel
    */
    public Logger getLogger()
    {
        return null;
    }

    //-----------------------------------------------------------
    // service production
    //-----------------------------------------------------------
    
   /**
    * Return the set of services produced by the model.
    * @return the services
    */
    public ServiceDescriptor[] getServices()
    {
        return new ServiceDescriptor[0];
    }

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * depedendency.
    * @return true if this model can fulfill the dependency
    */
    public boolean isaCandidate( DependencyDescriptor dependency )
    {
        return true;
    }

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * stage dependency.
    * @return true if this model can fulfill the dependency
    */
    public boolean isaCandidate( StageDescriptor stage )
    {
        return true;
    }

    //-----------------------------------------------------------
    // composite assembly
    //-----------------------------------------------------------

   /**
    * Returns the assembled state of the model.
    * @return true if this model is assembled
    */
    public boolean isAssembled()
    {
        return true;
    }

    /**
     * Assemble the model.
     * @exception Exception if an error occurs during model assembly
     */
    public void assemble() throws AssemblyException
    {
    }

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    public DeploymentModel[] getConsumerGraph()
    {
        return null;
    }

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    public DeploymentModel[] getProviderGraph()
    {
        return null;
    }

   /**
    * Disassemble the model.
    */
    public void disassemble(){}

   /**
    * Return the set of models assigned as providers.
    * @return the providers consumed by the model
    * @exception IllegalStateException if invoked prior to 
    *    the completion of the assembly phase 
    */
    public DeploymentModel[] getProviders()
    {
        return null;
    }

   /** 
    * Returns the maximum allowable time for deployment.
    *
    * @return the maximum time expressed in millisecond of how 
    * long a deployment may take.
    */
    public long getDeploymentTimeout()
    {
        return m_timeout;
    }
}
