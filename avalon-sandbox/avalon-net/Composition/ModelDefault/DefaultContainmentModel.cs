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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Data.Builder;
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> 
	/// Containment model implmentation within which composite models are aggregated
	/// as a part of a containment deployment model.
	/// </summary>
	/// <author>  
	/// <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultContainmentModel : DefaultDeploymentModel, IContainmentModel
	{
		private void InitBlock()
		{
			// m_compositionListeners = new System.Collections.IList();
			m_assembly = new DefaultState();
			m_models = new System.Collections.Hashtable();
			m_commissioned = new DefaultState();
		}

		//--------------------------------------------------------------
		// static
		//--------------------------------------------------------------
		
		// private static readonly ContainmentProfileBuilder BUILDER = new ContainmentProfileBuilder();
		
		private static readonly ContainmentProfileCreator CREATOR = new ContainmentProfileCreator();
		
		private static readonly TargetsCreator TARGETS = new TargetsCreator();
		
		private static String gePath(IContainmentContext context)
		{
			if ((System.Object) context.PartitionName == null)
			{
				return Apache.Avalon.Composition.Model.IDeploymentModel_Fields.SEPARATOR;
			}
			else
			{
				return context.PartitionName;
			}
		}
		
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_compositionListeners '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_compositionListeners' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		// private System.Collections.IList m_compositionListeners;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_assembly '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_assembly' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private DefaultState m_assembly;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_models '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_models' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private System.Collections.IDictionary m_models;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContainmentContext m_context;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_partition '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private String m_partition;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_services '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IServiceModel[] m_services;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_commissioned '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_commissioned' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private DefaultState m_commissioned;
		
		//--------------------------------------------------------------
		// state
		//--------------------------------------------------------------
		
		private CategoriesDirective m_categories;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of a new containment model.
		/// 
		/// </summary>
		/// <param name="context">the containment context that establishes
		/// the structural association of this containment model
		/// within a parent scope
		/// </param>
		public DefaultContainmentModel(IContainmentContext context):base(context)
		{
			InitBlock();
			
			m_context = context;
			
			if (null == (System.Object) context.PartitionName)
			{
				m_partition = Apache.Avalon.Composition.Model.IDeploymentModel_Fields.SEPARATOR;
			}
			else
			{
				m_partition = context.PartitionName + context.Name + Apache.Avalon.Composition.Model.IDeploymentModel_Fields.SEPARATOR;
			}
			
			//
			// populate the containment model with a set of models
			// based on the profiles contained in the supplied 
			// containment profile
			//
			
			DeploymentProfile[] profiles = context.ContainmentProfile.Profiles;
			for (int i = 0; i < profiles.Length; i++)
			{
				AddModel(profiles[i]);
			}
			
			//
			// setup the service export parameters
			//
			
			DefaultContainmentModelExportHelper helper = 
				new DefaultContainmentModelExportHelper(context, this);
			m_services = helper.createServiceExport();
		}
		
		//--------------------------------------------------------------
		// Commissionable
		//--------------------------------------------------------------
		
		/// <summary> Commission the appliance. 
		/// 
		/// </summary>
		/// <exception cref=""> Exception if a commissioning error occurs
		/// </exception>
		public override void Commission()
		{
			if (!IsAssembled)
				Assemble();
			
			lock (m_commissioned)
			{
				if (m_commissioned.Enabled)
					return ;
				
				//
				// get the startup sequence and from this
				// we locate the locally scoped models 
				// and deploy them
				//
				
				IDeploymentModel[] startup = StartupGraph;
				
				Commissioner commissioner = new Commissioner(Logger, true);
				
				try
				{
					for (int i = 0; i < startup.Length; i++)
					{
						IDeploymentModel child = startup[i];
						commissioner.commission(child);
					}
				}
				finally
				{
					commissioner.dispose();
				}

				//
				// all subsidary model and runtime structures are not
				// fully commissioned and we can proceed with the 
				// commissioning of our own runtime
				//
				
				base.Commission();
				m_commissioned.Enabled = true;
			}
		}
		
		/// <summary> Decommission the appliance.  Once an appliance is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public override void Decommission()
		{
			lock (m_commissioned)
			{
				if (!m_commissioned.Enabled)
					return ;
				
				if (Logger.IsDebugEnabled)
				{
					String message = "decommissioning";
					Logger.Debug(message);
				}
				
				base.Decommission();
				
				IDeploymentModel[] shutdown = ShutdownGraph;
				long timeout = DeploymentTimeout;
				
				/*
				Commissioner commissioner = new Commissioner(Logger, false);
				
				try
				{
					for (int i = 0; i < shutdown.Length; i++)
					{
						IDeploymentModel child = shutdown[i];
						child.Decommission();
					}
				}
				finally
				{
					commissioner.Dispose();
				}
				*/
				
				m_commissioned.Enabled = false;
			}
		}
		
		//--------------------------------------------------------------
		// IDeploymentModel
		//--------------------------------------------------------------
		
		/// <summary> Return the classloader model.
		/// 
		/// </summary>
		/// <returns> the classloader model
		/// </returns>
		public virtual ITypeLoaderModel TypeLoaderModel
		{
			get
			{
				return m_context.TypeLoaderModel;
			}
		}
		
		/// <summary> Returns true if Secure Execution mode has been enabled in the kernel.
		/// 
		/// Secure Execution mode enables the deployer to restrict the exection
		/// environment, and this flag allows for developers to quickly switch
		/// between the secure and non-secure execution modes.
		/// 
		/// </summary>
		/// <returns> true if Secure Execution mode has been enabled in the kernel.
		/// 
		/// </returns>
		public virtual bool IsSecureExecutionEnabled()
		{
			ISystemContext system = m_context.SystemContext;
			// return system.isCodeSecurityEnabled();
			return false;
		}
		
		/// <summary> Returns the maximum allowable time for deployment.
		/// 
		/// </summary>
		/// <returns> the maximum time expressed in millisecond of how 
		/// long a deployment may take.
		/// </returns>
		public override long DeploymentTimeout
		{
			get
			{
				return 0;
			}
		}
		
		/// <summary> Return the set of services produced by the model.</summary>
		/// <returns> the services
		/// </returns>
		public override ServiceDescriptor[] Services
		{
			get
			{
				return m_context.ContainmentProfile.ExportDirectives;
			}
		}
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// depedendency.
		/// </summary>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		public override bool IsaCandidate(DependencyDescriptor dependency)
		{
			ServiceDescriptor[] services = Services;
			for (int i = 0; i < services.Length; i++)
			{
				ServiceDescriptor service = services[i];
				if (service.Reference.Matches(dependency.Service))
				{
					return true;
				}
			}
			return false;
		}
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// stage dependency. The containment model implementation will 
		/// allways return FALSE.
		/// 
		/// </summary>
		/// <returns> FALSE containers don't export stage handling
		/// </returns>
		public override bool IsaCandidate(StageDescriptor stage)
		{
			return false;
		}
		
		/// <summary> Returns the assembled state of the model.</summary>
		/// <returns> true if this model is assembled
		/// </returns>
		public override bool IsAssembled
		{
			get
			{
				return m_assembly.Enabled;
			}
		}
		
		/// <summary> Assemble the model.  Model assembly is a process of 
		/// wiring together candidate service providers with consumers.
		/// The assembly implementation will assemble each deployment
		/// model contained within this model.
		/// 
		/// </summary>
		/// <exception cref=""> Exception if assembly cannot be fulfilled
		/// </exception>
		public virtual void Assemble()
		{
			System.Collections.IList list = new System.Collections.ArrayList();
			Assemble(list);
		}
		
		/// <summary> Assemble the model.</summary>
		/// <param name="subjects">the list of deployment targets making up the assembly chain
		/// </param>
		/// <exception cref=""> Exception if an error occurs during model assembly
		/// </exception>
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		public override void Assemble(System.Collections.IList subjects)
		{
			lock (m_assembly)
			{
				if (IsAssembled)
				{
					return ;
				}
				
				Logger.Debug("assembly phase");
				DefaultContainmentModelAssemblyHelper helper = new DefaultContainmentModelAssemblyHelper(m_context, this);
				
				IDeploymentModel[] models = m_context.ModelRepository.Models;
				for (int i = 0; i < models.Length; i++)
				{
					IDeploymentModel model = models[i];
					helper.assembleModel(model, subjects);
				}
				
				m_assembly.Enabled = true;
			}
		}
		
		/// <summary> Disassemble the model.</summary>
		public override void Disassemble()
		{
			lock (m_assembly)
			{
				if (!IsAssembled)
				{
					return ;
				}
				
				Logger.Debug("dissassembly phase");
				IDeploymentModel[] models = m_context.ModelRepository.Models;
				for (int i = 0; i < models.Length; i++)
				{
					IDeploymentModel model = models[i];
					model.Disassemble();
				}
				m_assembly.Enabled = false;
			}
		}
		
		/// <summary> Return the set of models assigned as providers.</summary>
		/// <returns> the providers consumed by the model
		/// </returns>
		/// <exception cref=""> IllegalStateException if the model is not in an assembled state 
		/// </exception>
		public override IDeploymentModel[] Providers
		{
			get
			{
				if (!IsAssembled)
				{
					String error = "Model is not assembled.";
					throw new System.SystemException(error);
				}
			
				System.Collections.ArrayList list = new System.Collections.ArrayList();
				IDeploymentModel[] models = m_context.ModelRepository.Models;
				for (int i = 0; i < models.Length; i++)
				{
					IDeploymentModel model = models[i];
					IDeploymentModel[] providers = model.Providers;
					for (int j = 0; j < providers.Length; j++)
					{
						IDeploymentModel provider = providers[j];
						String path = provider.Path;
						String root = Partition;
						if (!path.StartsWith(root))
						{
							list.Add(providers[j]);
						}
					}
				}
				return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
			}
		}
		
		//--------------------------------------------------------------
		// IContainmentModel
		//--------------------------------------------------------------
		
		/// <summary> Add a composition listener to the model.</summary>
		/// <param name="listener">the composition listener
		/// </param>
		/*public virtual void  addCompositionListener(CompositionListener listener)
		{
			lock (m_compositionListeners)
			{
				m_compositionListeners.Add(listener);
			}
		}*/
		
		/// <summary> Remove a composition listener from the model.</summary>
		/// <param name="listener">the composition listener
		/// </param>
		/*
		public virtual void  removeCompositionListener(CompositionListener listener)
		{
			lock (m_compositionListeners)
			{
				m_compositionListeners.RemoveElement(listener);
			}
		}*/
		
		/// <summary> Return the set of service export mappings</summary>
		/// <returns> the set of export directives published by the model
		/// </returns>
		public virtual IServiceModel[] ServiceModels
		{
			get
			{
				return m_services;
			}
		}
		
		/// <summary> Return the set of service export directives for a supplied class.</summary>
		/// <param name="type">a cleaa identifying the directive
		/// </param>
		/// <returns> the export directives
		/// </returns>
		public virtual IServiceModel GetServiceModel(System.Type type)
		{
			IServiceModel[] models = ServiceModels;
			for (int i = 0; i < models.Length; i++)
			{
				IServiceModel model = models[i];
				if (type.IsAssignableFrom(model.ServiceClass))
				{
					return model;
				}
			}
			return null;
		}
		
		/// <summary> Get the startup sequence for the model.</summary>
		public virtual IDeploymentModel[] StartupGraph
		{
			get
			{
				return m_context.DependencyGraph.StartupGraph;
			}
		}
		
		/// <summary> Get the shutdown sequence for the model.</summary>
		public virtual IDeploymentModel[] ShutdownGraph
		{
			get
			{
				return m_context.DependencyGraph.ShutdownGraph;
			}
		}
		
		/// <summary> Return the logging categories. </summary>
		/// <returns> the logging categories
		/// </returns>
		public virtual CategoriesDirective Categories
		{
			get
			{
				if (m_categories == null)
					return m_context.ContainmentProfile.Categories;

				return m_categories;
			}
			set
			{
				m_categories = value;
			}
		}
		
		/// <summary> Set categories. </summary>
		/// <param name="categories">the logging categories
		/// </param>
		public virtual void SetCategories(CategoriesDirective categories)
		{
			m_categories = categories; // TODO: merge with existing categories
		}
		
		/// <summary> Add a model referenced by a url to this model.</summary>
		/// <param name="url">the url of the model to include
		/// </param>
		/// <returns> the model 
		/// </returns>
		/// <exception cref=""> ModelException if a model related error occurs
		/// </exception>
		public virtual IContainmentModel AddContainmentModel(System.Uri url)
		{
			return AddContainmentModel(url, null);
		}
		
		public virtual IContainmentModel AddContainmentModel(System.Uri block, System.Uri config)
		{
			IContainmentModel model = CreateContainmentModel(null, block);
			AddModel(model.Name, model);
			ApplyTargets(config);
			return model;
		}
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context.
		/// 
		/// </summary>
		/// <param name="model">a containment or component model 
		/// </param>
		/// <returns> the supplied model
		/// </returns>
		public virtual IDeploymentModel AddModel(IDeploymentModel model)
		{
			String name = model.Name;
			return AddModel(name, model);
		}
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context using a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">a containment or deployment profile 
		/// </param>
		/// <returns> the model based on the supplied profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		public virtual IDeploymentModel AddModel(DeploymentProfile profile)
		{
			String name = profile.Name;
			IDeploymentModel model = CreateDeploymentModel(name, profile);
			AddModel(name, model);
			return model;
		}
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context using a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">a containment or deployment profile 
		/// </param>
		/// <returns> the model based on the supplied profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		internal virtual IDeploymentModel CreateDeploymentModel(DeploymentProfile profile)
		{
			String name = profile.Name;
			return CreateDeploymentModel(name, profile);
		}
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context using a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">a containment or deployment profile 
		/// </param>
		/// <returns> the model based on the supplied profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		internal virtual IDeploymentModel CreateDeploymentModel(String name, DeploymentProfile profile)
		{
			if (null == profile)
				throw new System.ArgumentNullException("profile");
			
			IDeploymentModel model = null;
			if (profile is ContainmentProfile)
			{
				ContainmentProfile containment = (ContainmentProfile) profile;
				model = CreateContainmentModel(containment);
			}
			else if (profile is ComponentProfile)
			{
				ComponentProfile deployment = (ComponentProfile) profile;
				model = CreateComponentModel(deployment);
			}
			else if (profile is NamedComponentProfile)
			{
				ComponentProfile deployment = CreateComponentProfile((NamedComponentProfile) profile);
				model = CreateComponentModel(deployment);
			}
			else if (profile is BlockIncludeDirective)
			{
				BlockIncludeDirective directive = (BlockIncludeDirective) profile;
				model = CreateContainmentModel(directive);
			}
			else if (profile is BlockCompositionDirective)
			{
				BlockCompositionDirective directive = (BlockCompositionDirective) profile;
				model = CreateContainmentModel(directive);
			}
			else
			{
				//
				// TODO: establish the mechanisms for the declaration
				// of a custom profile handler.
				//
				
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "containment.unknown-profile-class.error" + " " + Path + " " + profile.GetType().FullName;
				throw new ModelException(error);
			}
			return model;
		}
		
		/// <summary> Removal of a named model for the containment model.
		/// 
		/// </summary>
		/// <param name="name">the name of the subsidiary model to be removed
		/// </param>
		/// <exception cref=""> IllegalArgumentException if the supplied name is unknown
		/// </exception>
		public virtual void RemoveModel(String name)
		{
			IModelRepository repository = m_context.ModelRepository;
			lock (repository)
			{
				IDeploymentModel model = (IDeploymentModel) repository.GetModel(name);
				if (null == model)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "No model named [" + name + "] is referenced with the model [" + this + "].";
					throw new System.ArgumentException(error);
				}
				else
				{
					m_context.DependencyGraph.Add(model);
					repository.RemoveModel(model);
					// CompositionEvent event_Renamed = new CompositionEvent(this, model);
					// fireModelRemovedEvent(event_Renamed);
				}
			}
		}
		
		/// <summary> Return the partition name established by this containment context.</summary>
		/// <returns> the partition name
		/// </returns>
		public virtual String Partition
		{
			get
			{
				return m_partition;
			}
		}
		
		/// <summary> Return the set of immediate child models nested 
		/// within this model.
		/// 
		/// </summary>
		/// <returns> the nested model
		/// </returns>
		public virtual IDeploymentModel[] Models
		{
			get
			{
				return m_context.ModelRepository.Models;
			}
		}
		
		/// <summary> Return a child model relative to a supplied name.
		/// 
		/// </summary>
		/// <param name="path">a relative or absolute path
		/// </param>
		/// <returns> the named model or null if the name is unknown
		/// </returns>
		/// <exception cref=""> IllegalArgumentException if the name if badly formed
		/// </exception>
		public virtual IDeploymentModel GetModel(String path)
		{
			DefaultContainmentModelNavigationHelper helper = new DefaultContainmentModelNavigationHelper(m_context, this);
			return helper.GetModel(path);
		}
		
		/// <summary> Apply a set of override targets resolvable from a supplied url.</summary>
		/// <param name="config">a url resolvable to a TargetDirective[]
		/// </param>
		/// <exception cref=""> ModelException if an error occurs
		/// </exception>
		public virtual void ApplyTargets(System.Uri config)
		{
			if (config != null)
			{
				TargetDirective[] targets = GetTargets(config);
				ApplyTargets(targets);
			}
		}
		
		/// <summary> Apply a set of override targets.</summary>
		/// <param name="targets">a set of target directives
		/// </param>
		public virtual void ApplyTargets(TargetDirective[] targets)
		{
			for (int i = 0; i < targets.Length; i++)
			{
				TargetDirective target = targets[i];
				String path = target.Path;
				System.Object model = GetModel(path);
				if (model != null)
				{
					if (model is IComponentModel)
					{
						IComponentModel deployment = (IComponentModel) model;
						if (target.Configuration != null)
						{
							deployment.SetConfiguration(target.Configuration);
						}
						if (target.CategoriesDirective != null)
						{
							deployment.Categories = target.CategoriesDirective;
						}
					}
					else if (model is IContainmentModel)
					{
						IContainmentModel containment = (IContainmentModel) model;
						if (target.CategoriesDirective != null)
						{
							containment.Categories = target.CategoriesDirective;
						}
					}
				}
				else
				{
					String warning = "target.ignore" + " " + path;
					Logger.Warn(warning);
				}
			}
		}
		
		//--------------------------------------------------------------
		// private
		//--------------------------------------------------------------
		
		private IDeploymentModel AddModel(String name, IDeploymentModel model)
		{
			if (model.Equals(this))
				return model;

			IModelRepository repository = m_context.ModelRepository;

			lock (repository)
			{
				repository.AddModel(name, model);
				m_context.DependencyGraph.Add(model);
				// CompositionEvent event_Renamed = new CompositionEvent(this, model);
				// fireModelAddedEvent(event_Renamed);
				return model;
			}
		}
		
		/*
		private void  fireModelAddedEvent(CompositionEvent event_Renamed)
		{
			System.Collections.IEnumerator iterator = m_compositionListeners.GetEnumerator();
			//UPGRADE_TODO: Method 'java.util.Iterator.hasNext' was converted to 'System.Collections.IEnumerator.MoveNext' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratorhasNext"'
			while (iterator.MoveNext())
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'listener '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				//UPGRADE_TODO: Method 'java.util.Iterator.next' was converted to 'System.Collections.IEnumerator.Current' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratornext"'
				CompositionListener listener = (CompositionListener) iterator.Current;
				try
				{
					listener.modelAdded(event_Renamed);
				}
				//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "A composition listener raised an exception";
					Logger.warn(error, e);
				}
			}
		}
		
		private void  fireModelRemovedEvent(CompositionEvent event_Renamed)
		{
			System.Collections.IEnumerator iterator = m_compositionListeners.GetEnumerator();
			//UPGRADE_TODO: Method 'java.util.Iterator.hasNext' was converted to 'System.Collections.IEnumerator.MoveNext' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratorhasNext"'
			while (iterator.MoveNext())
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'listener '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				//UPGRADE_TODO: Method 'java.util.Iterator.next' was converted to 'System.Collections.IEnumerator.Current' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratornext"'
				CompositionListener listener = (CompositionListener) iterator.Current;
				try
				{
					listener.modelRemoved(event_Renamed);
				}
				//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "A composition listener raised an exception";
					Logger.warn(error, e);
				}
			}
		}
		*/
		
		/// <summary> Creation of a new instance of a deployment model within
		/// this containment context.
		/// 
		/// </summary>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the composition model
		/// </returns>
		private IComponentModel CreateComponentModel(ComponentProfile profile)
		{
			DefaultContainmentModelComponentHelper helper = 
				new DefaultContainmentModelComponentHelper(m_context, this);
			IComponentContext context = helper.CreateComponentContext(profile);
			IModelFactory factory = m_context.SystemContext.ModelFactory;
			return factory.CreateComponentModel(context);
		}
		
		/// <summary> Creation of a new instance of a containment model within
		/// this containment context.
		/// 
		/// </summary>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the composition model
		/// </returns>
		private IContainmentModel CreateContainmentModel(ContainmentProfile profile)
		{
			String name = profile.Name;
			return CreateContainmentModel(name, profile);
		}
		
		/// <summary> Creation of a new instance of a containment model within
		/// this containment context.
		/// 
		/// </summary>
		/// <param name="name">the containment name
		/// </param>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the composition model
		/// </returns>
		private IContainmentModel CreateContainmentModel(String name, ContainmentProfile profile)
		{
			return CreateContainmentModel(name, profile, new System.Uri[0]);
		}
		
		/// <summary> Creation of a new instance of a containment model within
		/// this containment context.
		/// 
		/// </summary>
		/// <param name="name">the containment name
		/// </param>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <param name="implicit">any implicit urls to include in the container classloader
		/// </param>
		/// <returns> the composition model
		/// </returns>
		private IContainmentModel CreateContainmentModel(String name, ContainmentProfile profile, System.Uri[] implicit_Renamed)
		{
			String partition = Partition;
			
			if (Logger.IsDebugEnabled)
			{
				String message = "containment.add" + " " + name;
				Logger.Debug(message);
			}
			
			ILoggingManager logging = m_context.SystemContext.LoggingManager;
			String base_Renamed = partition + name;
			logging.AddCategories(base_Renamed, profile.Categories);
			ILogger log = logging.GetLoggerForCategory(base_Renamed);
			
			try
			{
				ITypeLoaderModel classLoaderModel = m_context.TypeLoaderModel.CreateTypeLoaderModel(
					log, profile, implicit_Renamed);

				// Usar Path.Combine
				System.IO.FileInfo home = new System.IO.FileInfo(m_context.HomeDirectory.FullName + "\\" + name);
				System.IO.FileInfo temp = new System.IO.FileInfo(m_context.TempDirectory.FullName + "\\" + name);
				ILogger logger = Logger.CreateChildLogger(name);
				
				IModelRepository modelRepository = m_context.ModelRepository;
				
				DependencyGraph graph = m_context.DependencyGraph;
				
				DefaultContainmentContext context = new DefaultContainmentContext(logger, 
					m_context.SystemContext, classLoaderModel, 
					modelRepository, graph, home, temp, 
					this, profile, partition, name);
				
				IModelFactory factory = m_context.SystemContext.ModelFactory;
				return factory.CreateContainmentModel(context);
			}
			catch (ModelException e)
			{
				throw e;
			}
			catch (System.Exception e)
			{
				String error = "containment.container.create.error" + " " + Path + " " + profile.Name;
				throw new ModelException(error, e);
			}
		}
		
		/// <summary> Add a containment profile that is derived from an external resource.</summary>
		/// <param name="directive">the block composition directive
		/// </param>
		/// <returns> the containment model established by the include
		/// </returns>
		private IContainmentModel CreateContainmentModel(BlockCompositionDirective directive)
		{
			throw new NotImplementedException("CreateContainmentModel(BlockCompositionDirective)");

			/*
			String name = directive.Name;
			ResourceDirective resource = directive.Resource;
			String id = resource.Id;
			String group = resource.Group;
			String resourceName = resource.Name;
			String version = resource.Version;
			String type = resource.Type;
			
			IContainmentModel model = null;
			try
			{
				IRepository repository = m_context.SystemContext.getRepository();
				Artifact artifact = Artifact.createArtifact(group, resourceName, version, type);
				System.Uri url = repository.getResource(artifact);
				model = createContainmentModel(name, url);
			}
			catch (RepositoryException e)
			{
				String error = "Unable to include block [" + name + "] into the containmment model [" + getQualifiedName() + "] because of a repository related error.";
				throw new ModelException(error, e);
			}
			
			TargetDirective[] targets = directive.TargetDirectives;
			model.applyTargets(targets);
			return model;*/
		}
		
		/// <summary> Create a containment model that is derived from an external 
		/// source profile defintion.
		/// 
		/// </summary>
		/// <param name="directive">the block include directive
		/// </param>
		/// <returns> the containment model established by the include
		/// </returns>
		private IContainmentModel CreateContainmentModel(BlockIncludeDirective directive)
		{
			throw new NotImplementedException("CreateContainmentModel(BlockIncludeDirective)");

			/*
			String name = directive.Name;
			String path = directive.Path;
			
			try
			{
				if (path.IndexOf(":") < 0)
				{
					//UPGRADE_TODO: Method 'java.io.File.toURL' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1095"'
					System.Uri anchor = m_context.SystemContext.getBaseDirectory().toURL();
					//UPGRADE_TODO: Class 'java.net.URL' was converted to a 'System.Uri' which does not throw an exception if a URL specifies an unknown protocol. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1132"'
					System.Uri url = new System.Uri(anchor, path);
					return createContainmentModel(name, url);
				}
				else
				{
					//UPGRADE_TODO: Class 'java.net.URL' was converted to a 'System.Uri' which does not throw an exception if a URL specifies an unknown protocol. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1132"'
					System.Uri url = new System.Uri(path);
					return createContainmentModel(name, url);
				}
			}
			catch (System.UriFormatException e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to include block [" + name + "] into the containmment model [" + getQualifiedName() + "] because of a url related error.";
				throw new ModelException(error, e);
			}
			*/
		}
		
		/// <summary> Create a containment model that is derived from an external 
		/// source containment profile defintion.
		/// 
		/// </summary>
		/// <param name="directive">the block include directive
		/// </param>
		/// <returns> the containment model established by the include
		/// </returns>
		private IContainmentModel CreateContainmentModel(String name, System.Uri url)
		{
			throw new NotImplementedException("CreateContainmentModel(String, Uri)");

			/*
			if (url.Scheme.Equals("artifact") || url.Scheme.Equals("block"))
			{
				try
				{
					//UPGRADE_ISSUE: Method 'java.net.URL.getContent' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javanetURLgetContent"'
					Artifact artifact = (Artifact) url.getContent();
					System.Uri target = m_context.SystemContext.getRepository().getResource(artifact);
					return createContainmentModel(name, target);
				}
				//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
				catch (System.Exception e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Unresolvable artifact reference [" + url + "].";
					throw new ModelException(error, e);
				}
			}
			
			//UPGRADE_NOTE: Final was removed from the declaration of 'path '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
			String path = url.ToString();
			
			try
			{
				/*
				if (path.EndsWith(".jar"))
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'jarURL '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					System.Uri jarURL = convertToJarURL(url);
					//UPGRADE_NOTE: Final was removed from the declaration of 'blockURL '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: Class 'java.net.URL' was converted to a 'System.Uri' which does not throw an exception if a URL specifies an unknown protocol. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1132"'
					System.Uri blockURL = new System.Uri(jarURL, "/BLOCK-INF/block.xml");
					//UPGRADE_NOTE: Final was removed from the declaration of 'stream '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					System.IO.Stream stream = System.Net.WebRequest.Create(blockURL).GetResponse().GetResponseStream();
					
					try
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'profile '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						ContainmentProfile profile = BUILDER.createContainmentProfile(stream);
						
						//UPGRADE_NOTE: Final was removed from the declaration of 'message '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String message = "including composite block: " + blockURL.ToString();
						Logger.debug(message);
						
						return createContainmentModel(getName(name, profile), profile, new System.Uri[]{url});
					}
					//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
					catch (System.Exception e)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String error = "Unable to create block from embedded descriptor [" + blockURL.ToString() + "] in the containmment model [" + getQualifiedName() + "] due to a build related error.";
						throw new ModelException(error, e);
					}
				}
				else 
				if (path.EndsWith(".xml") || path.EndsWith(".block"))
				{
					DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
					IConfiguration config = builder.build(path);
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'profile '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					ContainmentProfile profile = CREATOR.createContainmentProfile(config);
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'message '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String message = "including composite block: " + path;
					Logger.debug(message);
					
					return createContainmentModel(getName(name, profile), profile);
				}
				else if (path.EndsWith("/"))
				{
					verifyPath(path);
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'blockURL '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: Class 'java.net.URL' was converted to a 'System.Uri' which does not throw an exception if a URL specifies an unknown protocol. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1132"'
					System.Uri blockURL = new System.Uri(url.ToString() + "BLOCK-INF/block.xml");
					
					DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
					IConfiguration config = builder.build(blockURL.ToString());
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'profile '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					ContainmentProfile profile = CREATOR.createContainmentProfile(config);
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'message '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String message = "including composite block: " + blockURL.ToString();
					Logger.debug(message);
					
					return createContainmentModel(getName(name, profile), profile, new System.Uri[]{url});
				}
				else if (path.EndsWith(".bar"))
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Cannot execute a block archive: " + path;
					throw new ModelException(error);
				}
				else
				{
					verifyPath(path);
					//UPGRADE_TODO: Class 'java.net.URL' was converted to a 'System.Uri' which does not throw an exception if a URL specifies an unknown protocol. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1132"'
					return createContainmentModel(name, new System.Uri(path + "/"));
				}
			}
			catch (ModelException e)
			{
				throw e;
			}
			catch (System.UriFormatException e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to include block [" + path + "] into the containmment model [" + getQualifiedName() + "] because of a url related error.";
				throw new ModelException(error, e);
			}
			catch (System.IO.IOException e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to include block [" + path + "] into the containmment model [" + getQualifiedName() + "] because of a io related error.";
				throw new ModelException(error, e);
			}
			//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Unable to include block [" + path + "] into the containmment model [" + getQualifiedName() + "] because of an unexpected error.";
				throw new ModelException(error, e);
			}
			*/
		}
		
		/// <summary> Verify the a path is valid.  The implementation will 
		/// throw an exception if a connection to a url established 
		/// using the path agument cann be resolved.
		/// 
		/// </summary>
		/// <exception cref=""> ModelException if the path is not resolvable 
		/// to a url connection
		/// </exception>
		/* private void  VerifyPath(String path)
		{
			try
			{
				System.Uri url = new System.Uri(path);
				System.Net.HttpWebRequest connection = (System.Net.HttpWebRequest) System.Net.WebRequest.Create(url);
				//UPGRADE_ISSUE: Method 'java.net.URLConnection.connect' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javanetURLConnectionconnect"'
				connection.connect();
			}
			catch (System.IO.FileNotFoundException e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "File not found: " + path;
				throw new ModelException(error);
			}
			//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Invalid path: " + path;
				throw new ModelException(error, e);
			}
		}*/
		
		private String GetName(String name, DeploymentProfile profile)
		{
			if ((System.Object) name != null)
				return name;
			return profile.Name;
		}
		
		/// <summary> Create a full deployment profile using a supplied named 
		/// profile reference.
		/// 
		/// </summary>
		/// <param name="profile">the named profile reference directive
		/// </param>
		/// <returns> the deployment profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during 
		/// profile creation
		/// </exception>
		private ComponentProfile CreateComponentProfile(NamedComponentProfile profile)
		{
			try
			{
				NamedComponentProfile holder = (NamedComponentProfile) profile;
				String typename = holder.Classname;
				String key = holder.Key;
				ITypeRepository repository = m_context.TypeLoaderModel.TypeRepository;
				TypeDescriptor type = repository.GetType(typename);
				ComponentProfile template = repository.GetProfile(type, key);
				return new ComponentProfile(profile.Name, template);
			}
			catch (System.Exception e)
			{
				String error = "containment.model.create.deployment.error" + " " + profile.Key + " " + Path + " " + profile.Classname;
				throw new ModelException(error, e);
			}
		}
		
		/// <summary>
		/// 
		/// </summary>
		/// <param name="url"></param>
		/// <returns></returns>
		private TargetDirective[] GetTargets(System.Uri url)
		{
			try
			{
				IConfiguration config = DefaultConfigurationSerializer.Deserialize(url.ToString());
				return TARGETS.CreateTargets(config).getTargets();
			}
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "Could not load the targets directive: " + url;
				throw new ModelException(error, e);
			}
		}
	}
}