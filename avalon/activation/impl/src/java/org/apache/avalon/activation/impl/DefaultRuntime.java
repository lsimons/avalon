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

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.activation.RuntimeFactory;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.Runtime;

import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoryDirective;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.Factory;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Implementation of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/02/10 16:19:15 $
 */
public class DefaultRuntime implements Runtime
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultRuntime.class );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final RuntimeFactory m_runtime;

    //--------------------------------------------------------------
    // mutable state
    //--------------------------------------------------------------

    private boolean m_trace;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new system context.
    *
    * @param system the system context
    */
    public DefaultRuntime( SystemContext system )
    {
        if( system == null )
        {
            throw new NullPointerException( "system" );
        }
        m_runtime = new DefaultRuntimeFactory( system );
    }

    //--------------------------------------------------------------
    // Runtime
    //--------------------------------------------------------------

   /**
    * Request the commissioning of a runtime for a supplied deployment 
    * model.
    * @param model the deployment model 
    * @exception Exception of a commissioning error occurs
    */
    public void commission( DeploymentModel model ) throws Exception
    {
        getRuntimeFactory().getRuntime( model ).commission();
    }

   /**
    * Request the decommissioning of a runtime for a supplied deployment 
    * model.
    * @param model the deployment model 
    * @exception Exception of a commissioning error occurs
    */
    public void decommission( DeploymentModel model )
    {
        getRuntimeFactory().getRuntime( model ).decommission();
    }

   /**
    * Request resolution of an object from the runtime.
    * @param model the deployment model
    * @exception Exception if a deployment error occurs
    */
    public Object resolve( DeploymentModel model ) throws Exception
    {
        return getRuntimeFactory().getRuntime( model ).resolve();
    }

   /**
    * Request the release of an object from the runtime.
    * @param model the deployment model
    * @param instance the object to release
    * @exception Exception if a deployment error occurs
    */
    public void release( DeploymentModel model, Object instance )
    {
        getRuntimeFactory().getRuntime( model ).release( instance );
    }

    //------------------------------------------------------------------
    // runtime operations
    //------------------------------------------------------------------

   /**
    * Return the runtime factory.
    *
    * @return the factory
    */
    private RuntimeFactory getRuntimeFactory()
    {
        return m_runtime;
    }


}
