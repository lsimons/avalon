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
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultContainmentModelAssemblyHelper
	{
		//-------------------------------------------------------------------
		// static
		//-------------------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'REZ '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'REZ' was moved to static method 'Apache.Avalon.Composition.Model.Default.DefaultContainmentModelAssemblyHelper'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		// private static readonly Resources REZ;
		
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContainmentContext m_context;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_model '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
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
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
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
		
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
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
							//UPGRADE_TODO: The equivalent in .NET for method 'java.util.List.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
							subjects.Add(model);
							StageDescriptor stage = new StageDescriptor(type.FullName);
							IDeploymentModel provider = findExtensionProvider(repository, stage, subjects);
							context.Provider = provider;
						}
						//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
						catch (System.Exception e)
						{
							//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
							//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
							String error = "Unable to assemble component: " + model + " due to a component context phase handler establishment failure.";
							throw new AssemblyException(error, e);
						}
						finally
						{
							//UPGRADE_TODO: Method 'java.util.List.remove' was converted to 'System.Collections.IList.Remove' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilListremove_javalangObject"'
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
						//UPGRADE_TODO: The equivalent in .NET for method 'java.util.List.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						subjects.Add(model);
						IDeploymentModel provider = findExtensionProvider(repository, stage, subjects);
						stage.Provider = provider;
					}
					//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
					catch (System.Exception e)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						String error = "Unable to assemble component: " + model + " due to a component extension handler establishment failure.";
						throw new AssemblyException(error, e);
					}
					finally
					{
						//UPGRADE_TODO: Method 'java.util.List.remove' was converted to 'System.Collections.IList.Remove' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilListremove_javalangObject"'
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
						//UPGRADE_TODO: The equivalent in .NET for method 'java.util.List.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						subjects.Add(model);
						IDeploymentModel provider = findDependencyProvider(repository, dependency, subjects);
						dependency.Provider = provider;
					}
					//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
					catch (System.Exception e)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						String error = "Unable to assemble component: " + model + " due to a service provider establishment failure.";
						throw new AssemblyException(error, e);
					}
					finally
					{
						//UPGRADE_TODO: Method 'java.util.List.remove' was converted to 'System.Collections.IList.Remove' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilListremove_javalangObject"'
						subjects.Remove(model);
					}
				}
			}
		}
		
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		private IDeploymentModel findDependencyProvider(IModelRepository repository, IDependencyModel dependency, System.Collections.IList subjects)
		{
			String path = dependency.Path;
			if (null != (System.Object) path)
			{
				IDeploymentModel model = m_model.GetModel(path);
				if (null == model)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
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
		
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
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
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "Nested assembly failure while attempting to construct model" + " for the profile: [" + profile + "] for the dependency: [" + dependency + "].";
					throw new AssemblyException(error, ae);
				}
				catch (ModelException me)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "Nested model failure while attempting to add model" + " for the profile: [" + profile + "] for the dependency: [" + dependency + "].";
					throw new AssemblyException(error, me);
				}
			}
			else
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to locate a service provider for the dependency: [ " + dependency + "].";
				throw new AssemblyException(error);
			}
		}
		
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		private IDeploymentModel findExtensionProvider(IModelRepository repository, IStageModel stage, System.Collections.IList subjects)
		{
			String path = stage.Path;
			if (null != (System.Object) path)
			{
				IDeploymentModel model = m_model.GetModel(path);
				if (null == model)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
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
		
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
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
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "Nested assembly failure while attempting to construct model" + " for the extension profile: [" + profile + "] for the stage dependency: [" + stage + "].";
					throw new AssemblyException(error, ae);
				}
				catch (ModelException me)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "Nested model failure while attempting to add model" + " for the extension profile: [" + profile + "] for the stage dependency: [" + stage + "].";
					throw new AssemblyException(error, me);
				}
			}
			else
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
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
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
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
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "An irrational condition has occured.";
				throw new ModelRuntimeException(error, tue);
			}
		}
		
		private DeploymentProfile[] getProfiles(ITypeRepository repository, TypeDescriptor[] types)
		{
			//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < types.Length; i++)
			{
				DeploymentProfile[] profiles = repository.GetProfiles(types[i]);
				for (int j = 0; j < profiles.Length; j++)
				{
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(profiles[j]);
				}
			}
			return (DeploymentProfile[]) list.ToArray( typeof(DeploymentProfile) );
		}
	}
}