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

namespace Apache.Avalon.Castle.Core
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for Orchestrator.
	/// </summary>
	[ManagedComponent]
	public class Orchestrator : ManagedService
	{
		private static readonly int LOGGER_MANAGER = 0;
		private static readonly int CONFIG_MANAGER = 1;
		private static readonly int LOOKUP_MANAGER = 2;

		protected ManagedObjectName[] childServices = new ManagedObjectName[3];
		protected ManagedObjectName deployManager;

		protected ILogger logger = Logger.LoggerFactory.GetLogger("Orchestrator");

		private OrchestratorNotificationSystem notificationSystem;

		/// <summary>
		/// 
		/// </summary>
		public Orchestrator()
		{
			logger.Debug("Constructor");
		}

		[ManagedAttribute]
		public ManagedObjectName DeployManager
		{
			get
			{
				return deployManager;
			}
			set
			{
				deployManager = value;
			}
		}


		[ManagedAttribute]
		public ManagedObjectName LoggerManager
		{
			get
			{
				return childServices[LOGGER_MANAGER];
			}
			set
			{
				childServices[LOGGER_MANAGER] = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName ConfigurationManager
		{
			get
			{
				return childServices[CONFIG_MANAGER];
			}
			set
			{
				childServices[CONFIG_MANAGER] = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName LookupManager
		{
			get
			{
				return childServices[LOOKUP_MANAGER];
			}
			set
			{
				childServices[LOOKUP_MANAGER] = value;
			}
		}
	
		public override void Start()
		{
			logger.Debug("Start");

			base.Create();

			// Start up notification system

			notificationSystem = new OrchestratorNotificationSystem();

			foreach(ManagedObjectName child in childServices)
			{
				if (child == null)
				{
					continue;
				}

				RegisterForPhases(child);
			}
		}

		public override void Stop()
		{
			logger.Debug("Stop");
		}

		private void RegisterForPhases(ManagedObjectName name)
		{
			try
			{
				logger.Debug("Invoking RegisterForPhases on {0}", name);

				server.Invoke( 
					name, 
					"RegisterForPhases", 
					new object[] { notificationSystem }, 
					new Type[] { typeof(OrchestratorNotificationSystem) } );

				logger.Debug("Done");
			}
			catch(Exception e)
			{
				logger.Error("Exception {0} invoking 'RegisterForPhases' on {1}", e.Message, name);

				throw e;
			}
		}
	}
}
