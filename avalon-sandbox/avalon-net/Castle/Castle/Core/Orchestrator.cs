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

namespace Apache.Avalon.Castle.Core
{
	using System;

	using Apache.Avalon.Castle.Core.Proxies;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Model.Default;
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Repository;
	using Apache.Avalon.Castle.Util;
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

		protected ManagedObjectName runtimeName;

		protected ManagedObjectName repositoryName;
		
		protected CastleOptions options;

		protected IRepository repository;

		protected ILoggingManager loggingManager;

		protected IRuntime runtime;
		
		protected ILogger logger = Logger.LoggerFactory.GetLogger("Orchestrator");

		protected OrchestratorNotificationSystem notificationSystem;

		protected ISystemContext systemContext;

		/// <summary>
		/// 
		/// </summary>
		public Orchestrator()
		{
			logger.Debug("Constructor");
		}

		[ManagedAttribute]
		public ISystemContext SystemContext
		{
			get
			{
				return systemContext;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName Runtime
		{
			get
			{
				return runtimeName;
			}
			set
			{
				runtimeName = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName Repository
		{
			get
			{
				return repositoryName;
			}
			set
			{
				repositoryName = value;
			}
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

		[ManagedOperation]
		public void DeployContainmentProfile(ContainmentProfile profile)
		{
			logger.Info("Creating ContainmentModel");

			ILogger sublogger = loggingManager.GetLoggerForCategory( "Containment" );
			IContainmentContext containmentContext = CreateContainmentContext( SystemContext, sublogger, profile );

			IContainmentModel containmentModel = new DefaultContainmentModel( containmentContext );

			logger.Info("Assembling");

			containmentModel.Assemble();

			logger.Info("Deploying");

			containmentModel.Commission();

			logger.Info("Started");
		}

		protected IContainmentContext CreateContainmentContext( ISystemContext system, ILogger logger, 
			ContainmentProfile profile )
		{
			system.LoggingManager.AddCategories( profile.Categories );

			ITypeLoaderContext typeLoaderContext = new DefaultTypeLoaderContext( 
				logger, system.Repository, system.BaseDirectory, profile.TypeLoaderDirective );

			ITypeLoaderModel typeModel = new DefaultTypeLoaderModel( typeLoaderContext );

			DefaultContainmentContext context = new DefaultContainmentContext( 
				logger, system, typeModel, null, null, profile );

			return context;
		}

		public override void Create()
		{
			logger.Debug("Create");
			base.Create();

			RetriveCastleOptions();

			CreateSystemContext();
		}
	
		public override void Start()
		{
			logger.Debug("Start");
			base.Start();

			// Create/Start notification system

			CreateAndStartNotificationSystem();

			// Ask DeployManager (whatever implementation) to starts
			// inspecting - and deploying

			InitDeployer();
		}

		public override void Stop()
		{
			logger.Debug("Stop");

			base.Stop();
		}

		protected void RetriveCastleOptions()
		{
			options = (CastleOptions) 
				MXUtil.GetAttribute( server, CastleLoader.CONTROLLER, "Options" );
		}

		protected void CreateSystemContext() 
		{
			EnsureRuntimeImplementationExists();

			EnsureLoggingImplementationExists();

			DefaultSystemContext context = new DefaultSystemContext(
				runtime, loggingManager, IOUtil.ToFile( options.BasePath ), 
				IOUtil.ToFile( options.HomePath ), 
				IOUtil.ToFile( options.TempPath ), repository, 
				"system", options.TraceEnabled, options.DeploymentTimeout, false);

			context.Put( "urn:composition:dir", IOUtil.ToFile( options.BasePath ) );
			context.MakeReadOnly();

			systemContext = context;
		}

		protected void InitDeployer()
		{
			AssertNotNull( DeployManager, "DeployManager implementation required" );

			MXUtil.InvokeOn( server, DeployManager, "Inspect" );
		}

		protected void EnsureRuntimeImplementationExists()
		{
			AssertNotNull( Runtime, "Runtime implementation required" );

			if ( runtime == null )
			{
				runtime = new RuntimeProxy( server, Runtime );
			}
		}

		protected void EnsureLoggingImplementationExists()
		{
			AssertNotNull( LoggerManager, "LoggingManager implementation required" );

			if ( loggingManager == null )
			{
				loggingManager = new LoggingManagerProxy( server, LoggerManager );
			}
		}

		protected void CreateAndStartNotificationSystem()
		{
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

		protected void RegisterForPhases(ManagedObjectName name)
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
				logger.Error("Exception {0} invoking 'RegisterForPhases' on {1}", 
					e.Message, name);

				throw e;
			}
		}

		private void AssertNotNull( ManagedObjectName name, String message )
		{
			if (name == null)
			{
				throw new OrchestratorException( message );
			}
		}
	}
}
