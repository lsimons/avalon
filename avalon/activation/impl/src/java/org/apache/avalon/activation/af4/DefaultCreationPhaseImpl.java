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

package org.apache.avalon.activation.af4;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.lifecycle.CreationException;
import org.apache.avalon.activation.lifecycle.ContextualizationHandler;
import org.apache.avalon.activation.lifecycle.CreationPhase;
import org.apache.avalon.activation.lifecycle.LifecycleCreateExtension;
import org.apache.avalon.lifecycle.Creator;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.StageModel;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.apache.avalon.meta.info.StageDescriptor;

public class DefaultCreationPhaseImpl extends AbstractLogEnabled
    implements CreationPhase
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultCreationPhaseImpl.class );
    
    /**
     * Invocation of the deployment creation stage extension.
     * @param model the model representing the object under deployment
     * @param stage the extension stage descriptor
     * @param object the object under deployment
     * @exception if a deployment error occurs
     */
    public void create( Engine engine, ComponentModel model, Object instance)
      throws CreationException
    {
        StageDescriptor[] stages = model.getType().getStages();
        if( ( stages.length > 0 ) && getLogger().isDebugEnabled() )
        {
            getLogger().debug( "stage count: " + stages.length );
        }

        for( int i=0; i<stages.length; i++ )
        {
            StageDescriptor stage = stages[i];
            Appliance provider = getStageProvider( engine, model, stage );

            if( getLogger().isDebugEnabled() )
            {
                Class c = ((ComponentModel)provider.getModel()).getDeploymentClass();

                getLogger().debug( "processing create: " + c.getName() 
                + ", [" +  Creator.class.isAssignableFrom( c ) + "]" );
            }
            
            LifecycleCreateExtension handler;
            try
            {
                handler = (LifecycleCreateExtension) provider.resolve();
            } catch( Exception e )
            {
                String errorMessage = "Unable to resolve the LifeCycle handler: " + provider;
                throw new CreationException( errorMessage, e );
            }
            
            try
            {                
                if( getLogger().isDebugEnabled() )
                {
                    int id = System.identityHashCode( instance );
                    getLogger().debug( "applying model create stage to: " + id );
                }
                handler.create( model, engine, stage, instance );
            }
            finally
            {
                provider.release( handler );
            }
        }
    }

    private Appliance getStageProvider( 
        final Engine engine, 
        final ComponentModel componentModel, 
        final StageDescriptor stage ) 
      throws IllegalArgumentException
    {
        final String key = stage.getKey();
        StageModel stageModel = componentModel.getStageModel( stage );
        if( null != stageModel )
        {
            DeploymentModel provider = stageModel.getProvider();
            if( null != provider )
            {
                return engine.locate( provider );
            }
            else
            {
                final String error = 
                  "Null provider returned for the stage: " + stage;
                throw new IllegalStateException( error );
            }
        }
        else
        {
            final String error = 
              REZ.getString( 
                "lifecycle.stage.key.unknown.error",
                componentModel.getQualifiedName(), key );
            throw new IllegalStateException( error );
        }
    }
}
 
