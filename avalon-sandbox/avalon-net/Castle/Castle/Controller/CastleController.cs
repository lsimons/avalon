// Copyright 2004 Apache Software Foundation
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

	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for CastleController.
	/// </summary>
	[ManagedComponent]
	public class CastleController : MRegistrationListener
	{
		/// <summary>
		/// 
		/// </summary>
		protected MServer server;
		
		/// <summary>
		/// 
		/// </summary>
		private CastleConfig config;

		private ArrayList startupList = new ArrayList();

		protected ILogger logger = Logger.LoggerFactory.GetLogger("CastleController");

		public CastleController()
		{
			logger.Debug("Constructor");
		}

		[ManagedOperation]
		public void Create()
		{
			logger.Debug("Create");

			InstantiateAndRegisterComponents();

			StartQueueComponents();
		}

		[ManagedOperation]
		public void Start()
		{
			logger.Debug("Start");
		}

		[ManagedOperation]
		public void Stop()
		{
			logger.Debug("Stop");

			Undeploy();
		}

		private void InstantiateAndRegisterComponents()
		{
			foreach(ComponentDescriptor component in config.Components)
			{
				RecursiveInstantiate(component);
			}
		}

		private void RecursiveInstantiate(ComponentDescriptor component)
		{
			foreach(ComponentDescriptor child in component.Dependencies.Components)
			{
				RecursiveInstantiate(child);
			}

			Instantiate(component);
		}

		private void Undeploy()
		{
			StopQueueComponents();
			DestroyQueueComponents();
		}

		/// <summary>
		/// Instantiates the component using the current MServer
		/// </summary>
		/// <param name="component"></param>
		private void Instantiate(ComponentDescriptor component)
		{
			logger.Debug("About to instantiate {0} type {1}", component.Name, component.Typename);

			ManagedObjectName name = new ManagedObjectName( component.Name );
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

		private void StartQueueComponents()
		{
			for(int i=0; i < startupList.Count; i++)
			{
				StartComponent( startupList[i] as ManagedObjectName );
			}
		}

		private void StopQueueComponents()
		{
			object[] components = startupList.ToArray();
			Array.Reverse( components );
			for(int i=0; i < components.Length; i++)
			{
				StopComponent( components[i] as ManagedObjectName );
			}
		}

		private void DestroyQueueComponents()
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

				server.Invoke(name, "Create", null, null);
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

				server.Invoke(name, "Start", null, null);
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

				server.Invoke(name, "Stop", null, null);
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

				server.Invoke(name, "Destroy", null, null);
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
				server.SetAttribute(name, attribute.Name, value);
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

			this.server = server;

			logger.Debug("Obtaining configuration from {0}", AppDomain.CurrentDomain.SetupInformation.ConfigurationFile);
			
			config = (CastleConfig) 
				ConfigurationSettings.GetConfig("Castle/Services");

			if (config == null)
			{
				logger.Error("Null configuration returned");

				throw new InitializationException("Could not find configuration.");
			}
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
	}
}
