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

package org.apache.avalon.composition.model.impl;

import java.util.ArrayList;

import org.apache.avalon.composition.data.DeploymentProfile;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.AssemblyException;
import org.apache.avalon.composition.model.ModelRepository;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.model.TypeUnknownException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.ModelSelector;
import org.apache.avalon.composition.model.ProfileSelector;
import org.apache.avalon.composition.provider.ContainmentContext;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.meta.info.Type;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * A utility class that assists in the location of a model relative
 * a supplied path.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/10 16:23:33 $
 */
class DefaultContainmentModelAssemblyHelper
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        DefaultContainmentModelAssemblyHelper.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final ContainmentContext m_context;
    private final ContainmentModel m_model;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultContainmentModelAssemblyHelper( 
      ContainmentContext context, ContainmentModel model )
    {
        m_context = context;
        m_model = model;
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    public void assembleModel( DeploymentModel model ) 
      throws AssemblyException
    {
         if( null == model )
         {
             throw new NullPointerException( "model" );
         }

         if( model.isAssembled() ) 
         {
             return;
         }
         else
         {
             if( model instanceof ComponentModel )
             {
                 assembleComponent( (ComponentModel) model );
             }
             else
             {
                 model.assemble();
             }
        }
    }

    private void assembleComponent( ComponentModel model ) throws AssemblyException
    {
        ModelRepository repository = m_context.getModelRepository();

        //
        // locate and assemble the component context handler
        //

        if( model.getContextModel() != null )
        {
            ContextModel context = model.getContextModel();
            Class clazz = context.getStrategyClass();
            if( !clazz.getName().equals( 
              ContextModel.DEFAULT_STRATEGY_CLASSNAME ) )
            {
                if( null == context.getProvider() )
                {
                    try
                    {
                        StageDescriptor stage = 
                          new StageDescriptor( clazz.getName() );
                        DeploymentModel provider = 
                          findExtensionProvider( repository, stage );
                        context.setProvider( provider );
                    }
                    catch( Throwable e )
                    {
                        final String error = 
                          "Unable to assemble component: " 
                          + model 
                         + " due to a component context phase handler establishment failure.";
                        throw new AssemblyException( error, e );
                    }
                }
            }
        }

        //
        // locate and resolve the stage providers
        //

        StageModel[] stages = model.getStageModels();
        for( int i=0; i<stages.length; i++ )
        {
            StageModel stage = stages[i];
            if( null == stage.getProvider() )
            {
                try
                {
                    DeploymentModel provider =
                      findExtensionProvider( repository, stage );
                    stage.setProvider( provider );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to assemble component: " 
                      + model 
                      + " due to a component extension handler establishment failure.";
                    throw new AssemblyException( error, e );
                }
            }
        }

        //
        // locate and resolve the service providers
        //

        DependencyModel[] dependencies = model.getDependencyModels();
        for( int i=0; i<dependencies.length; i++ )
        {
            DependencyModel dependency = dependencies[i];
            if( null == dependency.getProvider() )
            {
                try
                {
                    DeploymentModel provider =
                      findDependencyProvider( repository, dependency );
                    dependency.setProvider( provider );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to assemble component: " + model 
                      + " due to a service provider establishment failure.";
                    throw new AssemblyException( error, e );
                }
            }
        }
    }

    private DeploymentModel findDependencyProvider( 
      ModelRepository repository, DependencyModel dependency )
      throws AssemblyException
    {
        String path = dependency.getPath();
        if( null != path )
        {
            DeploymentModel model = m_model.getModel( path );
            if( null == model )
            {
                final String error = 
                  "Could not locate a model at the address: [" 
                  + path + "] in " + this + ".";
                throw new AssemblyException( error );
            }
            assembleModel( model );
            return model;
        }
        else
        {
            return findDependencyProvider( 
              repository, dependency.getDependency() );
        }
    }

    private DeploymentModel findDependencyProvider( 
      ModelRepository repository, DependencyDescriptor dependency )
      throws AssemblyException
    {
        DeploymentModel[] candidates = 
          repository.getCandidateProviders( dependency );
        ModelSelector selector = new DefaultModelSelector();
        DeploymentModel model = selector.select( candidates, dependency );
        if( model != null )
        {
            assembleModel( model );
            return model;
        }

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        DeploymentProfile[] profiles = findDependencyProfiles( dependency );
        ProfileSelector profileSelector = new DefaultProfileSelector();
        DeploymentProfile profile = profileSelector.select( profiles, dependency );
        if( profile != null ) 
        {
            try
            {
                DeploymentModel solution = m_model.addModel( profile );
                assembleModel( solution );
                return solution;
            }
            catch( AssemblyException ae )
            {
                final String error = 
                  "Nested assembly failure while attempting to construct model"
                  + " for the profile: [" + profile + "] for the dependency: ["
                  + dependency + "].";
                throw new AssemblyException( error, ae );
            }
            catch( ModelException me )
            {
                final String error = 
                  "Nested model failure while attempting to add model"
                  + " for the profile: [" + profile + "] for the dependency: ["
                  + dependency + "].";
                throw new AssemblyException( error, me );
            }
        }
        else
        {
            final String error = 
              "Unable to locate a service provider for the dependency: [ "
              + dependency + "].";
            throw new AssemblyException( error );
        }
    }

    private DeploymentModel findExtensionProvider( 
      ModelRepository repository, StageModel stage )
      throws AssemblyException
    {
        String path = stage.getPath();
        if( null != path )
        {
            DeploymentModel model = m_model.getModel( path );
            if( null == model )
            {
                final String error = 
                  "Could not locate a model at the address: [" 
                  + path + "] in " + this + ".";
                throw new AssemblyException( error );
            }
            assembleModel( model );
            return model;
        }
        else
        {
            return findExtensionProvider( repository, stage.getStage() );
        }
    }

    private DeploymentModel findExtensionProvider( 
      ModelRepository repository, StageDescriptor stage )
      throws AssemblyException
    {
        DeploymentModel[] candidates = 
          repository.getCandidateProviders( stage );
        ModelSelector selector = new DefaultModelSelector();
        DeploymentModel model = selector.select( candidates, stage );
        if( model != null )
        {
            assembleModel( model );
            return model;
        }

        //
        // otherwise, check for any packaged profiles that 
        // we could use to construct the model
        //

        DeploymentProfile[] profiles = findExtensionProfiles( stage );
        ProfileSelector profileSelector = new DefaultProfileSelector();
        DeploymentProfile profile = profileSelector.select( profiles, stage );
        if( profile != null ) 
        {
            try
            {
                DeploymentModel solution = m_model.addModel( profile );
                assembleModel( solution );
                return solution;
            }
            catch( AssemblyException ae )
            {
                final String error = 
                  "Nested assembly failure while attempting to construct model"
                  + " for the extension profile: [" + profile 
                  + "] for the stage dependency: ["
                  + stage + "].";
                throw new AssemblyException( error, ae );
            }
            catch( ModelException me )
            {
                final String error = 
                  "Nested model failure while attempting to add model"
                  + " for the extension profile: [" + profile 
                  + "] for the stage dependency: ["
                  + stage + "].";
                throw new AssemblyException( error, me );
            }
        }
        else
        {
            final String error = 
              "Unable to locate a extension provider for the stage: [ "
              + stage + "].";
            throw new AssemblyException( error );
        }
    }

    private DeploymentProfile[] findExtensionProfiles( StageDescriptor stage )
    {
        TypeRepository repository = m_context.getClassLoaderModel().getTypeRepository();
        Type[] types = repository.getTypes( stage );
        try
        {
            return getProfiles( repository, types );
        }
        catch( TypeUnknownException tue )
        {
            // will not happen
            final String error = "An irrational condition has occured.";
            throw new ModelRuntimeException( error, tue );
        }
    }

    private DeploymentProfile[] findDependencyProfiles( DependencyDescriptor dependency )
    {
        TypeRepository repository = m_context.getClassLoaderModel().getTypeRepository();
        Type[] types = repository.getTypes( dependency );
        try
        {
            return getProfiles( repository, types );
        }
        catch( TypeUnknownException tue )
        {
            // will not happen
            final String error = "An irrational condition has occured.";
            throw new ModelRuntimeException( error, tue );
        }
    }

    private DeploymentProfile[] getProfiles( TypeRepository repository, Type[] types )
      throws TypeUnknownException
    {
        ArrayList list = new ArrayList();
        for( int i=0; i<types.length; i++ )
        {
            DeploymentProfile[] profiles = 
            repository.getProfiles( types[i] );
            for( int j=0; j<profiles.length; j++ )
            {
                list.add( profiles[j] );
            }
        }
        return (DeploymentProfile[]) list.toArray( new DeploymentProfile[0] );
    }

}
