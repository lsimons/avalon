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

package org.apache.avalon.activation.impl;

import java.util.Map;
import java.util.Hashtable;

import org.apache.avalon.activation.RuntimeFactory;
import org.apache.avalon.activation.LifestyleFactory;
import org.apache.avalon.activation.LifestyleManager;
import org.apache.avalon.activation.Appliance;
import org.apache.avalon.activation.ApplianceException;
import org.apache.avalon.activation.ApplianceRuntimeException;

import org.apache.avalon.composition.model.Commissionable;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.provider.SystemContext;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.repository.Artifact;

/**
 * A factory enabling the establishment of runtime handlers.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/02/29 22:25:25 $
 */
public class DefaultRuntimeFactory implements RuntimeFactory
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        DefaultRuntimeFactory.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final SystemContext m_system;

    private final Map m_map = new Hashtable();

    private final LifestyleFactory m_factory;

    private final boolean m_secure;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultRuntimeFactory( SystemContext system )
    {
        m_system = system;
        m_factory = new DefaultLifestyleFactory( m_system );
        m_secure = m_system.isCodeSecurityEnabled();
    }

    //-------------------------------------------------------------------
    // RuntimeFactory
    //-------------------------------------------------------------------

   /**
    * Resolve a runtime handler for a model.
    * @param model the deployment model
    * @return the runtime appliance
    */
    public Appliance getRuntime( DeploymentModel model )
    {
        synchronized( m_map )
        {

            Appliance runtime = getRegisteredRuntime( model );
            if( null != runtime ) 
                return runtime;

            //
            // create the runtime
            // check the model for an overriding runtime using the 
            // standard runtime as the default (not implemented
            // yet)
            //

            if( model instanceof ComponentModel )
            {
                ComponentModel component = (ComponentModel) model;
                LifestyleManager manager = 
                  m_factory.createLifestyleManager( component );
                runtime = newComponentRuntime( component, manager );
            }
            else if( model instanceof ContainmentModel )
            {
                ContainmentModel containment = (ContainmentModel) model;
                runtime = newContainmentRuntime( containment );
            }
            else
            {
                final String error = 
                  REZ.getString( 
                    "runtime.error.unknown-model", 
                    model.toString(),
                    model.getClass().getName() );
                  throw new ModelRuntimeException( error );
            }

            registerRuntime( model, runtime );
            return runtime;
        }
    }

    //-------------------------------------------------------------------
    // private implementation
    //-------------------------------------------------------------------

   /**
    * Resolve a runtime handler for a component model.
    * @param model the containment model
    * @return the runtime handler
    */
    protected Appliance newComponentRuntime( ComponentModel model, LifestyleManager manager )
    {
        return new DefaultAppliance( model, manager, m_secure );
    }

   /**
    * Resolve a runtime handler for a containment model.
    * @param model the containment model
    * @return the runtime handler
    */
    protected Appliance newContainmentRuntime( ContainmentModel model )
      throws ApplianceRuntimeException
    {
        return new DefaultBlock( m_system, model );
    }

   /**
    * Lookup a runtime relative to the model name.
    * @param model the deployment model
    * @return the matching runtime (possibly null)
    */
    private Appliance getRegisteredRuntime( DeploymentModel model )
    {
        String name = model.getQualifiedName();
        return (Appliance) m_map.get( name );
    }

    private void registerRuntime( DeploymentModel model, Appliance runtime )
    {
        String name = model.getQualifiedName();
        m_map.put( name, runtime );
    }
}
