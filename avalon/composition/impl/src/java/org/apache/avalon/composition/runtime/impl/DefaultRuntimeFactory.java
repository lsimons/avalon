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

package org.apache.avalon.composition.runtime.impl;

import java.util.Map;
import java.util.Hashtable;

import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.runtime.RuntimeFactory;
import org.apache.avalon.composition.runtime.Commissionable;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * A factory enabling the establishment of runtime handlers.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/07 22:46:42 $
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

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultRuntimeFactory( SystemContext system )
    {
        m_system = system;
    }

    //-------------------------------------------------------------------
    // RuntimeFactory
    //-------------------------------------------------------------------

   /**
    * Resolve a runtime handler for a model.
    * @param model the deployment model
    * @return the runtime appliance
    */
    public Commissionable getRuntime( DeploymentModel model ) 
    {
        synchronized( m_map )
        {

            Commissionable runtime = getRegisteredRuntime( model );
            if( null != runtime ) return runtime;

            //
            // create the runtime
            //

            if( model instanceof ComponentModel )
            {
                ComponentModel component = (ComponentModel) model;
                runtime = newComponentRuntime( component );
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
    private Commissionable newComponentRuntime( ComponentModel  model )
      throws ModelRuntimeException
    {
        throw new UnsupportedOperationException( "getComponentRuntime" );
    }

   /**
    * Resolve a runtime handler for a containment model.
    * @param model the containment model
    * @return the runtime handler
    */
    private Commissionable newContainmentRuntime( ContainmentModel model )
      throws ModelRuntimeException
    {
        throw new UnsupportedOperationException( "getContainmentRuntime" );
    }

   /**
    * Lookup a runtime relative to the model name.
    * @param model the deployment model
    * @return the matching runtime (possibly null)
    */
    private Commissionable getRegisteredRuntime( DeploymentModel model )
    {
        String name = model.getQualifiedName();
        return (Commissionable) m_map.get( name );
    }

    private void registerRuntime( DeploymentModel model, Commissionable runtime )
    {
        String name = model.getQualifiedName();
        m_map.put( name, runtime );
    }
}