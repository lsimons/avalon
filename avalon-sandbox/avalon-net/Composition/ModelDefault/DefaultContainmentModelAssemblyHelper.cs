/* 
* Copyright 2003-2004 The Apache Software Foundation
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

namespace Apache.Avalon.Composition.Model.Default
{
	using System;
	
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> A utility class that assists in the location of a model relative
	/// a supplied path.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/03/07 22:06:40 $
	/// </version>
	class DefaultContainmentModelAssemblyHelper
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		private IContainmentContext m_context;
		private DefaultContainmentModel m_model;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		public DefaultContainmentModelAssemblyHelper(IContainmentContext context, DefaultContainmentModel model)
		{
			m_context = context;
			m_model = model;
		}
		
		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------
		
		/// <summary> Assemble a target model during which all deployment and runtime 
		/// dependencies are assigned a provider model.
		/// 
		/// </summary>
		/// <param name="model">the target model to be assembled
		/// </param>
		/// <param name="subject">the model requesting the assembly
		/// </param>
		public virtual void  assembleModel(IDeploymentModel model, System.Collections.IList subjects)
		{
			if (null == model)
			{
				throw new System.ArgumentNullException("model");
			}
			if (null == subjects)
			{
				throw new System.ArgumentNullException("subjects");
			}
			if (subjects.Contains(model))
			{
				return ;
			}
			if (model.IsAssembled)
			{
				return ;
			}
			
			if (model is IComponentModel)
			{
				AssembleComponent((IComponentModel) model, subjects);
			}
			else
			{
				model.Assemble(subjects);
			}
		}
		
		private void AssembleComponent(IComponentModel model, System.Collections.IList subjects)
		{
			IModelRepository repository = m_context.ModelRepository;
			
			//
			// locate and assemble the component context handler
			//
			
			if (model.ContextModel != null)
			{
				IContextModel context = model.ContextModel;
				System.Type type = context.StrategyClass;
				if (!type.FullName.Equals(Apache.Avalon.Composition.Model.IContextModel_Fields.DEFAULT_STRATEGY_CLASSNAME))
				{
					if (null == context.Provider)
					{
						try
						{
							subjects.Add(model);
							StageDescriptor stage = new StageDescriptor(type.FullName);
							IDeploymentModel provider = findExtensionProvider(repository, stage, subjects);
							context.Provider = provider;
						}
						catch (System.Exception e)
						{
							String error = "Unable to assemble component: " + model + " due to a component context phase handler establishment failure.";
							throw new AssemblyException(error, e);
						}
						finally
						{
							subjects.Remove(model);
						}
					}
				}
			}
			
			//
			// locate and resolve the stage providers
			//
			
			IStageModel[] stages = model.StageModels;
			for (int i = 0; i < stages.Length; i++)
			{
				IStageModel stage = stages[i];
				if (null == stage.Provider)
				{
					try
					{
						subjects.Add(model);
						IDeploymentModel provider = findExtensionProvider(repository, stage, subjects);
						stage.Provider = provider;
					}
					catch (System.Exception e)
					{
						String error = "Unable to assemble component: " + model + " due to a component extension handler establishment failure.";
						throw new AssemblyException(error, e);
					}
					finally
					{
						subjects.Remove(model);
					}
				}
			}
			
			//
			// locate and resolve the service providers
			//
			
			IDependencyModel[] dependencies = model.DependencyModels;
			for (int i = 0; i < dependencies.Length; i++)
			{
				IDependencyModel dependency = dependencies[i];
				if (null == dependency.Provider)
				{
					try
					{
						subjects.Add(model);
						IDeploymentModel provider = findDependencyProvider(repository, dependency, subjects);
						dependency.Provider = provider;
					}
					catch (System.Exception e)
					{
						String error = "Unable to assemble component: " + model + " due to a service provider establishment failure.";
						throw new AssemblyException(error, e);
					}
					finally
					{
						subjects.Remove(model);
					}
				}
			}
		}
		
		private IDeploymentModel findDependencyProvider(IModelRepository repository, IDependencyModel dependency, System.Collections.IList subjects)
		{
			String path = dependency.Path;
			if (null != (System.Object) path)
			{
				IDeploymentModel model = m_model.GetModel(path);
				if (null == model)
				{
					String error = "Could not locate a model at the address: [" + path + "] in " + this + ".";
					throw new AssemblyException(error);
				}
				assembleModel(model, subjects);
				return model;
			}
			else
			{
				return findDependencyProvider(repository, dependency.Dependency, subjects);
			}
		}
		
		private IDeploymentModel findDependencyProvider(IModelRepository repository, DependencyDescriptor dependency, System.Collections.IList subjects)
		{
			IDeploymentModel[] candidates = repository.GetCandidateProviders(dependency);
			IModelSelector selector = new DefaultModelSelector();
			IDeploymentModel model = selector.Select(candidates, dependency);
			if (model != null)
			{
				assembleModel(model, subjects);
				return model;
			}
			
			//
			// otherwise, check for any packaged profiles that 
			// we could use to construct the model
			//
			
			DeploymentProfile[] profiles = findDependencyProfiles(dependency);
			IProfileSelector profileSelector = new DefaultProfileSelector();
			DeploymentProfile profile = profileSelector.Select(profiles, dependency);
			if (profile != null)
			{
				try
				{
					IDeploymentModel solution = m_model.CreateDeploymentModel(profile);
					assembleModel(solution, subjects);
					m_model.AddModel(solution);
					return solution;
				}
				catch (AssemblyException ae)
				{
					String error = "Nested assembly failure while attempting to construct model" + " for the profile: [" + profile + "] for the dependency: [" + dependency + "].";
					throw new AssemblyException(error, ae);
				}
				catch (ModelException me)
				{
					String error = "Nested model failure while attempting to add model" + " for the profile: [" + profile + "] for the dependency: [" + dependency + "].";
					throw new AssemblyException(error, me);
				}
			}
			else
			{
				String error = "Unable to locate a service provider for the dependency: [ " + dependency + "].";
				throw new AssemblyException(error);
			}
		}
		
		private IDeploymentModel findExtensionProvider(IModelRepository repository, IStageModel stage, System.Collections.IList subjects)
		{
			String path = stage.Path;
			if (null != (System.Object) path)
			{
				IDeploymentModel model = m_model.GetModel(path);
				if (null == model)
				{
					String error = "Could not locate a model at the address: [" + path + "] in " + this + ".";
					throw new AssemblyException(error);
				}
				assembleModel(model, subjects);
				return model;
			}
			else
			{
				return findExtensionProvider(repository, stage.Stage, subjects);
			}
		}
		
		private IDeploymentModel findExtensionProvider(IModelRepository repository, StageDescriptor stage, System.Collections.IList subjects)
		{
			IDeploymentModel[] candidates = repository.GetCandidateProviders(stage);
			IModelSelector selector = new DefaultModelSelector();
			IDeploymentModel model = selector.Select(candidates, stage);
			if (model != null)
			{
				assembleModel(model, subjects);
				return model;
			}
			
			//
			// otherwise, check for any packaged profiles that 
			// we could use to construct the model
			//
			
			DeploymentProfile[] profiles = findExtensionProfiles(stage);
			IProfileSelector profileSelector = new DefaultProfileSelector();
			DeploymentProfile profile = profileSelector.Select(profiles, stage);
			if (profile != null)
			{
				try
				{
					IDeploymentModel solution = m_model.CreateDeploymentModel(profile);
					assembleModel(solution, subjects);
					m_model.AddModel(solution);
					return solution;
				}
				catch (AssemblyException ae)
				{
					String error = "Nested assembly failure while attempting to construct model" + " for the extension profile: [" + profile + "] for the stage dependency: [" + stage + "].";
					throw new AssemblyException(error, ae);
				}
				catch (ModelException me)
				{
					String error = "Nested model failure while attempting to add model" + " for the extension profile: [" + profile + "] for the stage dependency: [" + stage + "].";
					throw new AssemblyException(error, me);
				}
			}
			else
			{
				String error = "Unable to locate a extension provider for the stage: [ " + stage + "].";
				throw new AssemblyException(error);
			}
		}
		
		private DeploymentProfile[] findExtensionProfiles(StageDescriptor stage)
		{
			ITypeRepository repository = m_context.TypeLoaderModel.TypeRepository;
			TypeDescriptor[] types = repository.GetTypes(stage);
			try
			{
				return getProfiles(repository, types);
			}
			catch (TypeUnknownException tue)
			{
				// will not happen
				String error = "An irrational condition has occured.";
				throw new ModelRuntimeException(error, tue);
			}
		}
		
		private DeploymentProfile[] findDependencyProfiles(DependencyDescriptor dependency)
		{
			ITypeRepository repository = m_context.TypeLoaderModel.TypeRepository;
			TypeDescriptor[] types = repository.GetTypes(dependency);
			try
			{
				return getProfiles(repository, types);
			}
			catch (TypeUnknownException tue)
			{
				// will not happen
				String error = "An irrational condition has occured.";
				throw new ModelRuntimeException(error, tue);
			}
		}
		
		private DeploymentProfile[] getProfiles(ITypeRepository repository, TypeDescriptor[] types)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < types.Length; i++)
			{
				DeploymentProfile[] profiles = repository.GetProfiles(types[i]);
				for (int j = 0; j < profiles.Length; j++)
				{
					list.Add(profiles[j]);
				}
			}
			return (DeploymentProfile[]) list.ToArray( typeof(DeploymentProfile) );
		}
	}
}