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
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> Deployment model defintion.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultComponentModel : DefaultDeploymentModel, IComponentModel
	{
		private void  InitBlock()
		{
			m_assembly = new DefaultState();
		}

		//--------------------------------------------------------------
		// static
		//--------------------------------------------------------------
		
		private const String CONTEXTUALIZABLE = "Apache.Avalon.Framework.IContextualizable";
		
		private static readonly IConfiguration EMPTY_CONFIGURATION = new DefaultConfiguration("configuration", typeof(IComponentModel).FullName);
		
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		// new private IComponentContext m_context;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_contextModel '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContextModel m_contextModel;

		private IComponentContext m_componentContext;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_contextDependent '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private bool m_contextDependent;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_dependencies '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IDependencyModel[] m_dependencies;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_stages '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IStageModel[] m_stages;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_assembly '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'm_assembly' was moved to method 'InitBlock'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		private DefaultState m_assembly;
		
		//--------------------------------------------------------------
		// mutable state
		//--------------------------------------------------------------
		
		private CategoriesDirective m_categories;
		
		private IConfiguration m_config;
		
		// private Parameters m_parameters;
		
		private ActivationPolicy m_activation;
		
		private CollectionPolicy m_collection;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of a new deployment model.
		/// 
		/// </summary>
		/// <param name="context">the deployment context
		/// </param>
		public DefaultComponentModel(IComponentContext context):base(context)
		{
			InitBlock();
			
			m_componentContext = context;
			m_activation = context.Profile.Activation;
			m_categories = context.Profile.Categories;
			
			SetCollectionPolicy(context.Profile.CollectionPolicy);
			
			//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
			// ClassLoader classLoader = context.getClassLoader();
			
			if (Configurable)
			{
				IConfiguration defaults = context.Type.Configuration;
				//UPGRADE_NOTE: Final was removed from the declaration of 'explicit '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				IConfiguration explicit_Renamed = context.Profile.Configuration;
				//UPGRADE_NOTE: Final was removed from the declaration of 'consolidated '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				IConfiguration consolidated = ConsolidateConfigurations(explicit_Renamed, defaults);
				if (consolidated != null)
				{
					m_config = consolidated;
				}
				else
				{
					m_config = EMPTY_CONFIGURATION;
				}
			}
			
			/*
			if (Parameterizable)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'parameters '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				Parameters parameters = context.Profile.Parameters;
				if (parameters != null)
				{
					m_parameters = parameters;
				}
				else
				{
					m_parameters = Parameters.EMPTY_PARAMETERS;
				}
			}
			*/
			
			m_contextDependent = ContextDependentState;
			
			if (m_contextDependent)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'contextDescriptor '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				ContextDescriptor contextDescriptor = context.Type.Context;
				//UPGRADE_NOTE: Final was removed from the declaration of 'contextDirective '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				ContextDirective contextDirective = context.Profile.Context;
				//UPGRADE_NOTE: Final was removed from the declaration of 'log '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				ILogger log = Logger.CreateChildLogger("context");
				m_contextModel = new DefaultContextModel(log, contextDescriptor, contextDirective, context);
			}
			else
			{
				m_contextModel = null;
			}
			
			//
			// create the dependency models for subsequent assembly
			// management
			//
			
			DependencyDescriptor[] dependencies = context.Type.Dependencies;
			m_dependencies = new DefaultDependencyModel[dependencies.Length];
			
			for (int i = 0; i < dependencies.Length; i++)
			{
				DependencyDescriptor descriptor = dependencies[i];
				DependencyDirective directive = context.Profile.getDependencyDirective(descriptor.Key);
				m_dependencies[i] = new DefaultDependencyModel(context.Logger.CreateChildLogger("deps"), context.PartitionName, context.Profile.Name, descriptor, directive);
			}
			
			//
			// create the stage models for subsequent assembly
			// management
			//
			
			StageDescriptor[] stages = context.Type.Stages;
			m_stages = new DefaultStageModel[stages.Length];
			
			for (int i = 0; i < stages.Length; i++)
			{
				StageDescriptor descriptor = stages[i];
				StageDirective directive = context.Profile.getStageDirective(descriptor.Key);
				m_stages[i] = new DefaultStageModel(context.Logger.CreateChildLogger("stages"), context.PartitionName, descriptor, directive);
			}
		}
		
		//--------------------------------------------------------------
		// Composite
		//--------------------------------------------------------------
		
		private bool ContextAssembled
		{
			get
			{
				if (null == ContextModel)
					return true;
				
				System.Type type = ContextModel.StrategyClass;
				
				if (type.FullName.Equals(Apache.Avalon.Composition.Model.IContextModel_Fields.DEFAULT_STRATEGY_CLASSNAME))
					return true;
				
				return (null != ContextModel.Provider);
			}
			
		}

		private bool StageAssembled
		{
			get
			{
				IStageModel[] stages = StageModels;

				for (int i = 0; i < stages.Length; i++)
				{
					if (null == stages[i].Provider)
						return false;
				}
				return true;
			}
			
		}

		private bool ServiceAssembled
		{
			get
			{
				IDependencyModel[] dependencies = DependencyModels;
				for (int i = 0; i < dependencies.Length; i++)
				{
					if (null == dependencies[i].Provider)
						return false;
				}
				return true;
			}
			
		}

		private CollectionPolicy TypeCollectionPolicy
		{
			get
			{
				return m_componentContext.Type.Info.CollectionPolicy;
			}
			
		}
		/// <summary> Rest if the component type backing the model is 
		/// parameterizable.
		/// 
		/// </summary>
		/// <returns> TRUE if the compoent type is parameterizable
		/// otherwise FALSE
		/// </returns>
		/*
		public virtual bool Parameterizable
		{
			get
			{
				return typeof(IParameterizable).IsAssignableFrom(DeploymentType);
			}
		}
		*/
		/// <summary> Rest if the component type backing the model is 
		/// configurable.
		/// 
		/// </summary>
		/// <returns> TRUE if the component type is configurable
		/// otherwise FALSE
		/// </returns>
		public virtual bool Configurable
		{
			get
			{
				return typeof(IConfigurable).IsAssignableFrom(DeploymentType);
			}
			
		}
		/// <summary> Test if the component type backing the model requires the 
		/// establishment of a runtime context.
		/// 
		/// </summary>
		/// <param name="return">TRUE if the component type requires a runtime
		/// context otherwise FALSE
		/// </param>
		private bool ContextDependentState
		{
			get
			{
				if (m_componentContext.Type.Stages.Length > 0)
				{
					return true;
				}
				
				System.Type base_Renamed = m_componentContext.DeploymentType;
				String strategy = m_componentContext.Type.Context.GetAttribute(ContextDescriptor.STRATEGY_KEY, null);
				//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
				// ClassLoader classLoader = context.getClassLoader();
				
				if ((System.Object) strategy != null)
				{
					System.Type contextualizable = GetComponentClass(strategy);
					
					if (contextualizable == null)
					{
						String error = "deployment.missing-strategy.error " + strategy + " " + base_Renamed.FullName;
						throw new System.SystemException(error);
					}
					else
					{
						if (contextualizable.IsAssignableFrom(base_Renamed))
						{
							return true;
						}
						else
						{
							String error = "deployment.inconsitent-strategy.error " + contextualizable + " " + base_Renamed;
							throw new System.SystemException(error);
						}
					}
				}
				else
				{
					System.Type contextualizable = GetComponentClass(CONTEXTUALIZABLE);
					if (contextualizable != null)
					{
						if (contextualizable.IsAssignableFrom(base_Renamed))
						{
							return true;
						}
					}
				}
				return false;
			}
			
		}

		/// <summary> Returns the assembled state of the model.</summary>
		/// <returns> true if this model is assembled
		/// </returns>
		public override bool IsAssembled
		{
			get
			{
				return (ContextAssembled && StageAssembled && ServiceAssembled);
			}
		}
		
		/// <summary> Assemble the model.</summary>
		/// <exception cref=""> Exception if an error occurs during model assembly
		/// </exception>
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		public override void Assemble(System.Collections.IList subjects)
		{
			Logger.Warn("## component assembly request in : " + this + " with " + subjects);
		}
		
		/// <summary> Disassemble the model.</summary>
		public override void Disassemble()
		{
			// nothing to do
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
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Model is not assembled.";
					throw new System.SystemException(error);
				}
			
				//UPGRADE_NOTE: Final was removed from the declaration of 'list '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
				System.Collections.ArrayList list = new System.Collections.ArrayList();
				if (null != ContextModel)
				{
					IDeploymentModel provider = ContextModel.Provider;
					if (provider != null)
					{
						//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						list.Add(provider);
					}
				}
			
				IStageModel[] stages = StageModels;
				for (int i = 0; i < stages.Length; i++)
				{
					IStageModel stage = stages[i];
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(stage.Provider);
				}
			
				IDependencyModel[] dependencies = DependencyModels;
				for (int i = 0; i < dependencies.Length; i++)
				{
					IDependencyModel dependency = dependencies[i];
					list.Add(dependency.Provider);
				}
			
				return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
			}
		}
		
		//--------------------------------------------------------------
		// IDeploymentModel
		//--------------------------------------------------------------
		
		/// <summary> Return the set of services produced by the model.
		/// 
		/// </summary>
		/// <returns> the service descriptors
		/// </returns>
		public override ServiceDescriptor[] Services
		{
			get
			{
				return m_componentContext.Type.Services;
			}
		}
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// depedendency.
		/// 
		/// </summary>
		/// <param name="dependency">the dependency descriptor
		/// </param>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		public override bool IsaCandidate(DependencyDescriptor dependency)
		{
			return m_componentContext.Type.GetService(dependency.Service) != null;
		}
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// stage dependency.
		/// 
		/// </summary>
		/// <param name="stage">the stage descriptor
		/// </param>
		/// <returns> TRUE if this model can fulfill the stage dependency
		/// </returns>
		public override bool IsaCandidate(StageDescriptor stage)
		{
			return m_componentContext.Type.GetExtension(stage) != null;
		}
		
		//==============================================================
		// ComponentModel
		//==============================================================
		
		/// <summary> Return the collection policy for the model. If a profile
		/// does not declare a collection policy, then the collection
		/// policy declared by the underlying component type
		/// will be used.
		/// 
		/// </summary>
		/// <returns> the collection policy
		/// </returns>
		/// <seealso cref="InfoDescriptor#WEAK">
		/// </seealso>
		/// <seealso cref="InfoDescriptor#SOFT">
		/// </seealso>
		/// <seealso cref="InfoDescriptor#HARD">
		/// </seealso>
		/// <seealso cref="InfoDescriptor#UNDEFINED">
		/// </seealso>
		public virtual CollectionPolicy CollectionPolicy
		{
			get
			{
				if (m_collection == CollectionPolicy.Undefined)
				{
					return TypeCollectionPolicy;
				}
				else
				{
					return m_collection;
				}
			}
			set
			{
				m_collection = value;
			}
		}
		
		/// <summary> Set the collection policy for the model.
		/// 
		/// </summary>
		/// <param name="policy">the collection policy
		/// </param>
		public virtual void SetCollectionPolicy(CollectionPolicy policy)
		{
			if (policy == CollectionPolicy.Undefined)
			{
				m_collection = policy;
			}
			else
			{
				CollectionPolicy minimum = TypeCollectionPolicy;
				if (policy >= minimum)
				{
					m_collection = policy;
				}
				else
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'warning '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String warning = "Ignoring collection policy override [" + policy + "] because the value is higher that type threshhold [" + minimum + "].";
					Logger.Warn(warning);
				}
			}
		}
		
		
		/// <summary> Set categories. </summary>
		/// <param name="categories">the categories directive
		/// </param>
		public virtual CategoriesDirective Categories
		{
			get
			{
				return m_categories;
			}
			set
			{
				m_categories = value;
			}
		}
		
		/// <summary> Return the activation policy for the model. </summary>
		/// <returns> the activaltion policy
		/// </returns>
		public virtual ActivationPolicy ActivationPolicy
		{
			get
			{
				return m_activation;
			}
			set
			{
				m_activation = value;
			}
		}
		
		/// <summary> Set the activation policy for the model to the default value. </summary>
		public virtual void RevertActivationPolicy()
		{
			if (m_componentContext.Profile.Mode == Mode.Explicit)
			{
				m_activation = Apache.Avalon.Composition.Data.ActivationPolicy.Startup;
			}
			else
			{
				m_activation = Apache.Avalon.Composition.Data.ActivationPolicy.Lazy;
			}
		}
		
		/// <summary> Return the component type descriptor.</summary>
		/// <returns> the type descriptor
		/// </returns>
		public virtual TypeDescriptor TypeDescriptor
		{
			get
			{
				return m_componentContext.Type;
			}
		}
		
		/// <summary> Return the class for the deployable target.</summary>
		/// <returns> the class
		/// </returns>
		public virtual System.Type DeploymentType
		{
			get
			{
				return m_componentContext.DeploymentType;
			}
		}
		
		/// <summary> Set the parameters to the supplied value.  The supplied 
		/// parameters value will replace the existing parameters value.
		/// 
		/// </summary>
		/// <param name="parameters">the supplied parameters value
		/// </param>
		/// <exception cref=""> IllegalStateException if the component type backing the 
		/// model does not implement the parameteriazable interface
		/// </exception>
		/// <exception cref=""> NullPointerException if the supplied parameters are null
		/// </exception>
		/*
		public virtual void  setParameters(Parameters parameters)
		{
			setParameters(parameters, true);
		}*/
		
		/// <summary> Set the parameters to the supplied value.  The supplied 
		/// parameters value may suppliment or replace the existing 
		/// parameters value.
		/// 
		/// </summary>
		/// <param name="parameters">the supplied parameters
		/// </param>
		/// <param name="policy">if TRUE the supplied parameters replaces the current
		/// parameters value otherwise the existing and supplied values
		/// are aggregrated
		/// </param>
		/// <exception cref=""> IllegalStateException if the component type backing the 
		/// model does not implement the parameteriazable interface
		/// </exception>
		/// <exception cref=""> NullPointerException if the supplied parameters are null
		/// </exception>
		/*public virtual void  setParameters(Parameters parameters, bool policy)
		{
			if (!Parameterizable)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
				String error = "todo: error message"; //"deployment.parameters.irrational", DeploymentType.FullName, this.ToString());
				throw new System.SystemException(error);
			}
			
			if (parameters == null)
			{
				throw new System.ArgumentNullException("parameters");
			}
			
			if (policy)
			{
				System.Collections.Specialized.NameValueCollection props = Parameters.toProperties(m_parameters);
				System.Collections.Specialized.NameValueCollection suppliment = Parameters.toProperties(parameters);
				System.Collections.IEnumerator enum_Renamed = suppliment.Keys.GetEnumerator();
				//UPGRADE_TODO: Method 'java.util.Enumeration.hasMoreElements' was converted to 'System.Collections.IEnumerator.MoveNext' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073"'
				while (enum_Renamed.MoveNext())
				{
					//UPGRADE_TODO: Method 'java.util.Enumeration.nextElement' was converted to 'System.Collections.IEnumerator.Current' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073"'
					String name = (String) enum_Renamed.Current;
					String value_Renamed = suppliment[name];
					if ((System.Object) value_Renamed == null)
					{
						SupportClass.HashtableRemove(props, name);
					}
					else
					{
						//UPGRADE_TODO: Method 'java.util.Properties.setProperty' was converted to 'System.Collections.Specialized.NameValueCollection.Item' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073"'
						props[name] = value_Renamed;
					}
				}
				m_parameters = Parameters.fromProperties(props);
			}
			else
			{
				m_parameters = parameters;
			}
		}*/
		
		/// <summary> Return the parameters to be applied to the component.
		/// If the the component type does not implementation the 
		/// Parameterizable interface, the implementation returns null. 
		/// 
		/// </summary>
		/// <returns> the assigned parameters
		/// </returns>
		/*public virtual Parameters getParameters()
		{
			return m_parameters;
		}*/
		
		/// <summary> Set the configuration to the supplied value.  The supplied 
		/// configuration will replace the existing configuration.
		/// 
		/// </summary>
		/// <param name="config">the supplied configuration
		/// </param>
		/// <exception cref=""> IllegalStateException if the component type backing the 
		/// model does not implement the configurable interface
		/// </exception>
		/// <exception cref=""> NullPointerException if the supplied configuration is null
		/// </exception>
		public virtual void SetConfiguration(IConfiguration config)
		{
			SetConfiguration(config, true);
		}
		
		/// <summary> Set the configuration to the supplied value.  The supplied 
		/// configuration may suppliment or replace the existing configuration.
		/// 
		/// </summary>
		/// <param name="config">the supplied configuration
		/// </param>
		/// <param name="policy">if TRUE the supplied configuration replaces the current
		/// configuration otherwise the resoved configuration shall be layed above
		/// the configuration supplied with the profile which in turn is layer above 
		/// the type default configuration (if any)
		/// </param>
		/// <exception cref=""> IllegalStateException if the component type backing the 
		/// model does not implement the configurable interface
		/// </exception>
		/// <exception cref=""> NullPointerException if the supplied configuration is null
		/// </exception>
		public virtual void SetConfiguration(IConfiguration config, bool policy)
		{
			if (!Configurable)
			{
				String error = "deployment.configuration.irrational " + DeploymentType.FullName + " " + this.ToString();
				throw new System.SystemException(error);
			}
			
			if (config == null)
			{
				throw new System.ArgumentNullException("config");
			}
			
			if (policy)
			{
				m_config = ConsolidateConfigurations(config, m_config);
			}
			else
			{
				m_config = config;
			}
		}
		
		/// <summary> Return the configuration to be applied to the component.
		/// The implementation returns the current configuration state.
		/// If the the component type does not implementation the 
		/// Configurable interface, the implementation returns null. 
		/// 
		/// </summary>
		/// <returns> the qualified configuration
		/// </returns>
		public virtual IConfiguration Configuration
		{
			get
			{
				return m_config;
			}
		}
		
		/// <summary> Test if the component type backing the model requires the 
		/// establishment of a runtime context.
		/// 
		/// </summary>
		/// <returns> TRUE if the component type requires a runtime
		/// context otherwise FALSE
		/// </returns>
		public virtual bool IsContextDependent
		{
			get
			{
				return m_contextDependent;
			}
		}
		
		/// <summary> Return the context model for this deployment model.
		/// 
		/// </summary>
		/// <returns> the context model if this model is context dependent, else
		/// the return value is null
		/// </returns>
		public virtual IContextModel ContextModel
		{
			get
			{
				return m_contextModel;
			}
		}
		
		
		/// <summary> Return the dependency models for this component type.
		/// 
		/// </summary>
		/// <returns> the dependency models
		/// </returns>
		public virtual IDependencyModel[] DependencyModels
		{
			get
			{
				return m_dependencies;
			}
		}
		
		/// <summary> Return a dependency model matching the supplied descriptor. If 
		/// no model matches the supplied descriptor the implementation
		/// will return null.
		/// 
		/// </summary>
		/// <param name="dependency">the dependency descriptor
		/// </param>
		/// <returns> the matching stage model
		/// </returns>
		public virtual IDependencyModel GetDependencyModel(DependencyDescriptor dependency)
		{
			IDependencyModel[] models = DependencyModels;
			for (int i = 0; i < models.Length; i++)
			{
				IDependencyModel model = models[i];
				if (dependency.Equals(model.Dependency))
				{
					return model;
				}
			}
			return null;
		}
		
		
		/// <summary> Return the stage models for this component type.
		/// 
		/// </summary>
		/// <returns> the stage models
		/// </returns>
		public virtual IStageModel[] StageModels
		{
			get
			{
				return m_stages;
			}
		}
		
		/// <summary> Return a stage model matching the supplied descriptor. If 
		/// no stage model matches the supplied descriptor the implementation
		/// will return null.
		/// 
		/// </summary>
		/// <param name="stage">the stage descriptor
		/// </param>
		/// <returns> the matching stage model
		/// </returns>
		public virtual IStageModel GetStageModel(StageDescriptor stage)
		{
			IStageModel[] stages = StageModels;
			for (int i = 0; i < stages.Length; i++)
			{
				IStageModel model = stages[i];
				if (stage.Equals(model.Stage))
				{
					return model;
				}
			}
			return null;
		}
		
		
		/// <summary> Return the set of services produced by the model as a array of classes.
		/// 
		/// </summary>
		/// <returns> the service classes
		/// </returns>
		public virtual System.Type[] Interfaces
		{
			get
			{
				//
				// TODO: add a SoftReference to hold the service class array
				// instad of generating each time
				//
			
				//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
				// ClassLoader classLoader = context.getClassLoader();
				//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
				System.Collections.ArrayList list = new System.Collections.ArrayList();
				ServiceDescriptor[] services = Services;
				for (int i = 0; i < services.Length; i++)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'service '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					ServiceDescriptor service = services[i];
					//UPGRADE_NOTE: Final was removed from the declaration of 'typename '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					System.Type type = service.Reference.Type;
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					//// list.Add(GetComponentClass(classLoader, typename));
					list.Add( type );
				}
			
				//
				// if the component is an extension then add all implemented 
				// interfaces
				//
			
				if (TypeDescriptor.Extensions.Length > 0)
				{
					System.Type[] interfaces = DeploymentType.GetInterfaces();
					for (int i = 0; i < interfaces.Length; i++)
					{
						//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						list.Add(interfaces[i]);
					}
				}
			
				return (System.Type[]) list.ToArray( typeof(System.Type) );
			}
		}
		
		/// <summary> Return the deployment timeout value for the component.
		/// 
		/// </summary>
		/// <returns> the default deployment timeout value
		/// </returns>
		public override long DeploymentTimeout
		{
			get
			{
				String value_Renamed = m_componentContext.Type.Info.GetAttribute(
					Apache.Avalon.Composition.Model.IDeploymentModel_Fields.DEPLOYMENT_TIMEOUT_KEY, null);

				if (null != (System.Object) value_Renamed)
				{
					try
					{
						return System.Int64.Parse(value_Renamed);
					}
					catch (System.FormatException nfe)
					{
						String error = "Invalid timout parameter [" + value_Renamed + "] in component type [" + m_componentContext.Type + "].";
						throw new ModelRuntimeException(error, nfe);
					}
				}
				else
				{
					return base.DeploymentTimeout;
				}
			}
		}
		
		//==============================================================
		// implementation
		//==============================================================
		
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		private System.Type GetComponentClass(String typename)
		{
			if ((System.Object) typename == null)
			{
				throw new System.ArgumentNullException("typename");
			}
			
			try
			{
				return Type.GetType(typename);
			}
			catch (System.Exception)
			{
				return null;
			}
		}
		
		private IConfiguration ConsolidateConfigurations(IConfiguration primary, IConfiguration defaults)
		{
			if (primary == null)
			{
				return defaults;
			}
			else
			{
				if (defaults == null)
				{
					return primary;
				}
				else
				{
					return new CascadingConfiguration(primary, defaults);
				}
			}
		}
	}
}