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
	using Apache.Avalon.Castle.Util;
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
		protected MServer m_server;
		
		/// <summary>
		/// 
		/// </summary>
		private CastleConfig m_config;

		/// <summary>
		/// 
		/// </summary>
		private CastleOptions m_options;

		/// <summary>
		/// 
		/// </summary>
		private ArrayList m_startupList = new ArrayList();

		/// <summary>
		/// 
		/// </summary>
		private ArrayList m_componentsList = new ArrayList();

		/// <summary>
		/// 
		/// </summary>
		[NonSerialized]
		protected ILogger m_logger;

		#region Constructors

		/// <summary>
		/// 
		/// </summary>
		protected CastleController()
		{
			m_logger = Logger.LoggerFactory.GetLogger("CastleController");
		}

		/// <summary>
		/// 
		/// </summary>
		public CastleController(CastleOptions options) : this()
		{
			m_options = options;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="info"></param>
		/// <param name="context"></param>
		public CastleController(System.Runtime.Serialization.SerializationInfo info, 
			System.Runtime.Serialization.StreamingContext context) : this()
		{
			m_options = info.GetValue( "options", typeof(CastleOptions) ) as CastleOptions;
			m_config  = info.GetValue( "config", typeof(CastleConfig)  ) as CastleConfig;
		}

		#endregion

		#region CastleController Attributes

		/// <summary>
		/// 
		/// </summary>
		[ManagedAttribute]
		public CastleConfig Config
		{
			get
			{
				return m_config;
			}
			set
			{
				m_config = value;
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
				return m_options;
			}
			set
			{
				m_options = value;
			}
		}

		#endregion

		#region Main Operations

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void Create()
		{
			m_logger.Debug("Create");
			
			if (m_config == null) 
			{
				m_logger.Error("Configuration not available");
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
			m_logger.Debug("Start");

			StartQueuedComponents();

			m_startupList = null;
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void Stop()
		{
			m_logger.Debug("Stop");
			Undeploy();
		}

		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void AddService( String typename, ManagedObjectName serviceName )
		{
			Assert.ArgumentNotNull( typename, "typename" );
			Assert.ArgumentNotNull( serviceName, "serviceName" );

			AddService( typename, serviceName, null );
		}
		
		/// <summary>
		/// 
		/// </summary>
		[ManagedOperation]
		public void AddService( String typename, ManagedObjectName serviceName, ManagedObjectName parentName )
		{
			Assert.ArgumentNotNull( typename, "typename" );
			Assert.ArgumentNotNull( serviceName, "serviceName" );
			
			Instantiate( typename, serviceName );

			if (parentName != null)
			{
				RegisterAsChild( serviceName, parentName );
			}
			
			CreateComponent( serviceName );

			StartComponent( serviceName );
		}

		#endregion

		#region Private Implementation

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
			foreach(ComponentDescriptor component in m_config.Components)
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
			StopComponents();
			DestroyComponents();
		}

		/// <summary>
		/// Instantiates the component using the current MServer
		/// </summary>
		/// <param name="component"></param>
		private void Instantiate(ComponentDescriptor component, ManagedObjectName name)
		{
			Instantiate( component.Typename, name );

			m_logger.Debug("About to setup {0}", name);
			Setup( name, component );
		}

		/// <summary>
		/// Instantiates the component using the current MServer
		/// </summary>
		/// <param name="component"></param>
		private void Instantiate(String typename, ManagedObjectName name)
		{
			m_logger.Debug("About to instantiate {0} type {1}", name, typename);

			Object instance = m_server.Instantiate( typename );

			if ( !typeof(MService).IsAssignableFrom( instance.GetType() ) )
			{
				m_logger.Error("Type {0} does not implement interface MService", typename);
				throw new CastleContainerException( String.Format("Type {0} must implement MService", typename) );
			}

			m_logger.Debug("About to register {0}", name);
			m_server.RegisterManagedObject( instance, name );
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

			MXUtil.InvokeOn( m_server, parent, "AddChild", name );
		}

		private void StartQueuedComponents()
		{
			for(int i=0; i < m_startupList.Count; i++)
			{
				StartComponent( m_startupList[i] as ManagedObjectName );
			}
		}

		private void StopComponents()
		{
			object[] components = m_componentsList.ToArray();
			for(int i=0; i < components.Length; i++)
			{
				StopComponent( components[i] as ManagedObjectName );
			}
		}

		private void DestroyComponents()
		{
			object[] components = m_componentsList.ToArray();
			for(int i=0; i < components.Length; i++)
			{
				DestroyComponent( components[i] as ManagedObjectName );
			}
		}

		private void QueueStart(ManagedObjectName name)
		{
			if (!m_startupList.Contains(name))
			{
				m_startupList.Add(name);
			}
		}

		private void CreateComponent(ManagedObjectName name)
		{
			try
			{
				m_logger.Debug("Invoking Create on {0}", name);
				MXUtil.Create( m_server, name );
			}
			catch(Exception e)
			{
				m_logger.Error("Exception '{0}' invoking 'Create' on '{1}'", e.Message, name);

				throw e;
			}
		}

		private void StartComponent(ManagedObjectName name)
		{
			try
			{
				m_logger.Debug("Invoking Start on {0}", name);
				MXUtil.Start( m_server, name );

				if (!m_componentsList.Contains( name ) )
				{
					m_componentsList.Add( name );
				}
			}
			catch(Exception e)
			{
				m_logger.Error("Exception '{0}' invoking 'Start' on '{1}'", e.Message, name);

				throw e;
			}
		}

		private void StopComponent(ManagedObjectName name)
		{
			try
			{
				m_logger.Debug("Invoking Stop on {0}", name);
				MXUtil.Stop( m_server, name );
			}
			catch(Exception e)
			{
				m_logger.Error("Ignoring Exception '{0}' invoking 'Stop' on '{1}'", e.Message, name);
			}
		}

		private void DestroyComponent(ManagedObjectName name)
		{
			try
			{
				m_logger.Debug("Invoking Destroy on {0}", name);
				MXUtil.Destroy( m_server, name );
			}
			catch(Exception e)
			{
				m_logger.Error("Ignoring Exception '{0}' invoking 'Destroy' on '{1}'", e.Message, name);
			}
		}

		private void SetupAttributes(ManagedObjectName name, ComponentDescriptor component)
		{
			if (component.Attributes.Count == 0)
			{
				return;
			}

			ManagementInfo info = m_server.GetManagementInfo(name);

			foreach(AttributeDescriptor attribute in component.Attributes)
			{
				if (!info.Attributes.Contains(attribute.Name))
				{
					throw new InvalidConfigurationEntryException(String.Format("Entry {0} doesn't support attribute named {1}", name, attribute.Name));
				}

				ManagementAttribute mAtt = (ManagementAttribute) info.Attributes[attribute.Name];

				Object value = PerformConversion(mAtt.AttributeType, attribute.Value);

				m_logger.Debug("Setting Attribute '{0}' value '{1}'", attribute.Name, value);
				MXUtil.SetAttribute( m_server, name, attribute.Name, value );
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

		#endregion

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			m_logger.Debug("BeforeRegister {0}", name);
			m_logger.Debug("Obtaining configuration from {0}", 
				AppDomain.CurrentDomain.SetupInformation.ConfigurationFile);

			m_server = server;
			
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
			info.AddValue( "options", m_options );
			info.AddValue( "config" , m_config );
		}

		#endregion
	}
}
