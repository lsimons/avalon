// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Castle.Controller
{
	using System;
	using System.Collections;
	using System.Configuration;

	using Apache.Avalon.Castle.Controller.Config;
	using Apache.Avalon.Castle.ManagementExtensions;
	using MXUtil  = Apache.Avalon.Castle.Util.MXUtil;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for CastleController.
	/// </summary>
	[Serializable]
	[ManagedComponent]
	public class CastleController : MRegistrationListener, System.Runtime.Serialization.ISerializable
	{
		/// <summary>
		/// 
		/// </summary>
		protected MServer server;
		
		/// <summary>
		/// 
		/// </summary>
		private CastleConfig config;

		/// <summary>
		/// 
		/// </summary>
		private CastleOptions options;

		/// <summary>
		/// 
		/// </summary>
		private ArrayList startupList = new ArrayList();

		/// <summary>
		/// 
		/// </summary>
		[NonSerialized]
		protected ILogger logger;

		/// <summary>
		/// 
		/// </summary>
		protected CastleController()
		{
			logger = Logger.LoggerFactory.GetLogger("CastleController");
		}

		/// <summary>
		/// 
		/// </summary>
		public CastleController(CastleOptions options) : this()
		{
			this.options = options;
		}

		public CastleController(System.Runtime.Serialization.SerializationInfo info, 
			System.Runtime.Serialization.StreamingContext context) : this()
		{
			options = info.GetValue( "options", typeof(CastleOptions) ) as CastleOptions;
			config  = info.GetValue( "config", typeof(CastleConfig)  ) as CastleConfig;
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedAttribute]
		public CastleConfig Config
		{
			get
			{
				return config;
			}
			set
			{
				config = value;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedAttribute]
		public CastleOptions Options
		{
			get
			{
				return options;
			}
			set
			{
				options = value;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void Create()
		{
			logger.Debug("Create");
			
			if (config == null) 
			{
				logger.Error("Configuration not available");
				throw new InitializationException("Configuration not available");
			}

			InstantiateAndRegisterComponents();
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void Start()
		{
			logger.Debug("Start");

			StartQueuedComponents();
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void Stop()
		{
			logger.Debug("Stop");
			Undeploy();
		}

		/// <summary>
		/// 
		/// </summary>
		private void InstantiateAndRegisterComponents()
		{
			InstantiateAndRegisterComponents( null );
		}

		/// <summary>
		/// 
		/// </summary>
		private void InstantiateAndRegisterComponents( ManagedObjectName parent )
		{
			foreach(ComponentDescriptor component in config.Components)
			{
				RecursiveInstantiate(component, parent);
			}
		}

		/// <summary>
		/// 
		/// </summary>
		private ManagedObjectName RecursiveInstantiate( ComponentDescriptor component, ManagedObjectName parent )
		{
			ManagedObjectName name = new ManagedObjectName( component.Name );

			ArrayList children = new ArrayList();

			foreach(ComponentDescriptor child in component.Dependencies.Components)
			{
				children.Add( RecursiveInstantiate(child, name) );
			}

			Instantiate(component, name);

			foreach(ManagedObjectName childName in children)
			{
				RegisterAsChild( childName, name );
			}

			return name;
		}

		/// <summary>
		/// 
		/// </summary>
		private void Undeploy()
		{
			StopQueuedComponents();
			DestroyQueuedComponents();
		}

		/// <summary>
		/// Instantiates the component using the current MServer
		/// </summary>
		/// <param name="component"></param>
		private void Instantiate(ComponentDescriptor component, ManagedObjectName name)
		{
			logger.Debug("About to instantiate {0} type {1}", name, component.Typename);

			String typename = component.Typename;

			Object instance = server.Instantiate( typename );

			if ( !typeof(MService).IsAssignableFrom( instance.GetType() ) )
			{
				logger.Error("Type {0} does not implement interface MService", typename);
				throw new CastleContainerException( String.Format("Type {0} must implement MService", typename) );
			}

			logger.Debug("About to register {0}", name);
			server.RegisterManagedObject( instance, name );

			logger.Debug("About to setup {0}", name);
			Setup( name, component );
		}

		/// <summary>
		/// Handle lifecycle methods and setup component attributes and dependencies;
		/// </summary>
		/// <param name="name"></param>
		/// <param name="component"></param>
		private void Setup(ManagedObjectName name, ComponentDescriptor component)
		{
			SetupAttributes(name, component);
			SetupDependencies(name, component);
			CreateComponent(name);
			QueueStart(name);
		}

		private void RegisterAsChild(ManagedObjectName name, ManagedObjectName parent)
		{
			if (parent == null)
			{
				return;
			}

			MXUtil.InvokeOn( server, parent, "AddChild", name );
		}

		private void StartQueuedComponents()
		{
			for(int i=0; i < startupList.Count; i++)
			{
				StartComponent( startupList[i] as ManagedObjectName );
			}
		}

		private void StopQueuedComponents()
		{
			object[] components = startupList.ToArray();
			Array.Reverse( components );
			for(int i=0; i < components.Length; i++)
			{
				StopComponent( components[i] as ManagedObjectName );
			}
		}

		private void DestroyQueuedComponents()
		{
			object[] components = startupList.ToArray();
			Array.Reverse( components );
			for(int i=0; i < components.Length; i++)
			{
				DestroyComponent( components[i] as ManagedObjectName );
			}
		}

		private void QueueStart(ManagedObjectName name)
		{
			startupList.Add(name);
		}

		private void CreateComponent(ManagedObjectName name)
		{
			try
			{
				logger.Debug("Invoking Create on {0}", name);
				MXUtil.Create( server, name );
			}
			catch(Exception e)
			{
				logger.Error("Exception '{0}' invoking 'Create' on '{1}'", e.Message, name);

				throw e;
			}
		}

		private void StartComponent(ManagedObjectName name)
		{
			try
			{
				logger.Debug("Invoking Start on {0}", name);
				MXUtil.Start( server, name );
			}
			catch(Exception e)
			{
				logger.Error("Exception '{0}' invoking 'Start' on '{1}'", e.Message, name);

				throw e;
			}
		}

		private void StopComponent(ManagedObjectName name)
		{
			try
			{
				logger.Debug("Invoking Stop on {0}", name);
				MXUtil.Stop( server, name );
			}
			catch(Exception e)
			{
				logger.Error("Ignoring Exception '{0}' invoking 'Stop' on '{1}'", e.Message, name);
			}
		}

		private void DestroyComponent(ManagedObjectName name)
		{
			try
			{
				logger.Debug("Invoking Destroy on {0}", name);
				MXUtil.Destroy( server, name );
			}
			catch(Exception e)
			{
				logger.Error("Ignoring Exception '{0}' invoking 'Destroy' on '{1}'", e.Message, name);
			}
		}

		private void SetupAttributes(ManagedObjectName name, ComponentDescriptor component)
		{
			if (component.Attributes.Count == 0)
			{
				return;
			}

			ManagementInfo info = server.GetManagementInfo(name);

			foreach(AttributeDescriptor attribute in component.Attributes)
			{
				if (!info.Attributes.Contains(attribute.Name))
				{
					throw new InvalidConfigurationEntryException(String.Format("Entry {0} doesn't support attribute named {1}", name, attribute.Name));
				}

				ManagementAttribute mAtt = (ManagementAttribute) info.Attributes[attribute.Name];

				Object value = PerformConversion(mAtt.AttributeType, attribute.Value);

				logger.Debug("Setting Attribute '{0}' value '{1}'", attribute.Name, value);
				MXUtil.SetAttribute( server, name, attribute.Name, value );
			}
		}

		private void SetupDependencies(ManagedObjectName name, ComponentDescriptor component)
		{
			if (component.Dependencies.Count == 0)
			{
				return;
			}

			// TODO: Add dependency handling
		}

		private Object PerformConversion(Type targetType, Object currentValue)
		{
			// TODO: There is a lot to refactor here.

			if (targetType == typeof(ManagedObjectName))
			{
				return new ManagedObjectName( Convert.ToString(currentValue) );
			}

			throw new CastleContainerException("PerformConversion: Type not supported");
		}

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			logger.Debug("BeforeRegister {0}", name);
			logger.Debug("Obtaining configuration from {0}", 
				AppDomain.CurrentDomain.SetupInformation.ConfigurationFile);

			this.server = server;
			
			Config = (CastleConfig) 
				ConfigurationSettings.GetConfig("Castle/Services");
		}

		public void AfterDeregister()
		{
		}

		public void AfterRegister()
		{
		}

		public void BeforeDeregister()
		{
		}

		#endregion

		#region ISerializable Members

		public void GetObjectData(System.Runtime.Serialization.SerializationInfo info, System.Runtime.Serialization.StreamingContext context)
		{
			info.AddValue( "options", options );
			info.AddValue( "config" , config );
		}

		#endregion
	}
}
