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
	using System.Collections;

	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Model.Default;
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Repository;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Castle.Util.Proxies;
	using Apache.Avalon.Castle.ManagementExtensions;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for Orchestrator.
	/// </summary>
	[ManagedComponent]
	public class Orchestrator : ManagedService
	{
		#region Fields

		protected ManagedObjectName m_deployManagerName;

		protected ManagedObjectName m_loggingManagerName;

		protected ManagedObjectName m_runtimeName;

		protected ManagedObjectName m_repositoryName;
		
		protected CastleOptions m_options;

		protected IRepository m_repository;

		protected ILoggingManager m_loggingManager;

		protected IRuntime m_runtime;
		
		protected ILogger m_logger = Logger.LoggerFactory.GetLogger("Orchestrator");

		protected OrchestratorNotificationSystem m_notificationSystem;

		protected ISystemContext m_systemContext;

		protected ArrayList m_applications = new ArrayList();

		#endregion

		#region Constructor

		/// <summary>
		/// 
		/// </summary>
		public Orchestrator()
		{
			m_logger.Debug("Constructor");
		}

		#endregion

		#region Attributes

		[ManagedAttribute]
		public ISystemContext SystemContext
		{
			get
			{
				return m_systemContext;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName Runtime
		{
			get
			{
				return m_runtimeName;
			}
			set
			{
				m_runtimeName = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName Repository
		{
			get
			{
				return m_repositoryName;
			}
			set
			{
				m_repositoryName = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName DeployManager
		{
			get
			{
				return m_deployManagerName;
			}
			set
			{
				m_deployManagerName = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName LoggerManager
		{
			get
			{
				return m_loggingManagerName;
			}
			set
			{
				m_loggingManagerName = value;
			}
		}

		#endregion

		#region Deployment Operations

		[ManagedOperation]
		public void DeployContainmentProfile(ContainmentProfile profile)
		{
			m_logger.Info("Creating ContainmentModel");

			ILogger sublogger = m_loggingManager.GetLoggerForCategory( "Containment" );
			IContainmentContext containmentContext = CreateContainmentContext( SystemContext, sublogger, profile );

			IContainmentModel containmentModel = new DefaultContainmentModel( containmentContext );

			m_logger.Info("Assembling");

			containmentModel.Assemble();

			m_logger.Info("Deploying");

			containmentModel.Commission();

			m_logger.Info("Started");

			m_applications.Add( containmentModel );
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

		#endregion

		#region ManagedService overrides

		public override void Create()
		{
			m_logger.Debug("Create");
			base.Create();

			RetriveCastleOptions();

			CreateSystemContext();
		}
	
		public override void Start()
		{
			m_logger.Debug("Start");
			base.Start();

			// Create/Start notification system

			CreateAndStartNotificationSystem();

			// Ask DeployManager (whatever implementation) to starts
			// inspecting - and deploying

			InitDeployer();
		}

		public override void Stop()
		{
			m_logger.Debug("Stop");

			base.Stop();

			foreach( IContainmentModel model in m_applications )
			{
				model.Decommission();
			}

			foreach( IContainmentModel model in m_applications )
			{
				model.Disassemble();
			}
		}

		#endregion

		#region Private implementation

		protected void RetriveCastleOptions()
		{
			m_options = (CastleOptions) 
				MXUtil.GetAttribute( Server, CastleConstants.CONTROLLER_NAME, "Options" );
		}

		protected void CreateSystemContext() 
		{
			EnsureRuntimeImplementationExists();

			EnsureLoggingImplementationExists();

			DefaultSystemContext context = new DefaultSystemContext(
				m_runtime, m_loggingManager, IOUtil.ToFile( m_options.BasePath ), 
				IOUtil.ToFile( m_options.HomePath ), 
				IOUtil.ToFile( m_options.TempPath ), m_repository, 
				"system", m_options.TraceEnabled, m_options.DeploymentTimeout, false);

			context.Put( "urn:composition:dir", IOUtil.ToFile( m_options.BasePath ) );
			context.MakeReadOnly();

			m_systemContext = context;
		}

		protected void InitDeployer()
		{
			AssertNotNull( DeployManager, "DeployManager implementation required" );

			MXUtil.InvokeOn( Server, DeployManager, "Inspect" );
		}

		protected void EnsureRuntimeImplementationExists()
		{
			AssertNotNull( Runtime, "Runtime implementation required" );

			if ( m_runtime == null )
			{
				m_runtime = (IRuntime) ManagedObjectProxyGenerator.CreateProxy( 
					Runtime, Server, typeof(IRuntime) );
			}
		}

		protected void EnsureLoggingImplementationExists()
		{
			AssertNotNull( LoggerManager, "LoggingManager implementation required" );

			if ( m_loggingManager == null )
			{
				m_loggingManager = (ILoggingManager) ManagedObjectProxyGenerator.CreateProxy( 
					LoggerManager, Server, typeof(ILoggingManager) );
			}
		}

		protected void CreateAndStartNotificationSystem()
		{
			m_notificationSystem = new OrchestratorNotificationSystem( Server );

			foreach(ManagedObjectName child in Children)
			{
				if (!child.Domain.Equals( CastleConstants.CASTLE_PHASE_DOMAIN ))
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
				m_logger.Debug("Invoking RegisterForPhases on {0}", name);

				MXUtil.InvokeOn( Server, name, "RegisterForPhases", m_notificationSystem );

				m_logger.Debug("Done");
			}
			catch(Exception e)
			{
				m_logger.Error("Exception {0} invoking 'RegisterForPhases' on {1}", 
					e.Message, name);

				throw e;
			}
		}

		#endregion

		#region Creation / Destruction

		/// <summary>
		/// 
		/// </summary>
		/// <param name="model"></param>
		[ManagedOperation]
		public void InvokeCreatePhases( IComponentModel model )
		{
			
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="model"></param>
		[ManagedOperation]
		public void InvokeDestructionPhases( IComponentModel model )
		{
		}

		#endregion

		#region Utilities

		private void AssertNotNull( ManagedObjectName name, String message )
		{
			if (name == null)
			{
				throw new OrchestratorException( message );
			}
		}

		#endregion
	}
}
