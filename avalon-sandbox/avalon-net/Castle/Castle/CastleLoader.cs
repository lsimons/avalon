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

namespace Apache.Avalon.Castle
{
	using System;
	using System.Reflection;

	using Apache.Avalon.Castle.Controller;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Server;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Client;
	using ILogger = Apache.Avalon.Framework.ILogger;
	using MXUtil  = Apache.Avalon.Castle.Util.MXUtil;

	/// <summary>
	/// Summary description for CastleLoader.
	/// </summary>
	public class CastleLoader : IDisposable
	{
		public static readonly ManagedObjectName CONTROLLER = 
			new ManagedObjectName(Castle.CASTLE_DOMAIN + ":name=Controller");
		
		protected MServer m_server;

		protected MConnectorServer m_connectorServer;

		protected ManagedInstance m_controller;

		protected ILogger m_logger = Logger.LoggerFactory.GetLogger("CastleLoader");

		public CastleLoader()
		{
		}

		~CastleLoader()
		{
			m_logger.Debug("Running Finalizer()");
			
			Stop();
		}

		public MServer Server
		{
			get
			{
				return m_server;
			}
		}

		public void Start(CastleOptions options)
		{
			m_logger.Debug("Start()");

			CreateMServer(options);
			CreateController(options);
			
			if (options.EnableRemoteManagement)
			{
				CreateServerConnector(options.ServerConnectorUrl, null);
			}
		}

		public void Stop()
		{
			m_logger.Debug("Stop()");

			if (m_connectorServer != null && m_connectorServer.ManagedObjectName != null )
			{
				m_server.UnregisterManagedObject( m_connectorServer.ManagedObjectName );
				
				m_connectorServer = null;
			}

			if (m_server != null)
			{
				if (m_controller != null)
				{
					MXUtil.Stop( m_server, CONTROLLER );
				}

				MServerFactory.Release(m_server);

				m_server = null;
			}
		}

		protected virtual void CreateMServer(CastleOptions options)
		{
			m_logger.Debug("Creating MServer");
			m_server = MServerFactory.CreateServer(options.DomainName, options.IsolatedDomain);
		}

		protected virtual void CreateController(CastleOptions options)
		{
			m_logger.Debug("Creating Controller");

			CastleController controllerInstance = new CastleController(options);

			m_logger.Debug("Registering Controller");
			m_controller = m_server.RegisterManagedObject( controllerInstance, CONTROLLER );

			m_logger.Debug("Invoking Create on Controller...");
			MXUtil.Create( m_server, CONTROLLER );

			m_logger.Debug("Invoking Start on Controller...");
			MXUtil.Start( m_server, CONTROLLER );
		}

		protected virtual void CreateServerConnector(String url, System.Collections.Specialized.NameValueCollection properties)
		{
			m_logger.Debug("Creating ServerConnector");

			m_connectorServer = MConnectorServerFactory.CreateServer(url, properties, null );

			m_server.RegisterManagedObject( m_connectorServer, MConnectorServer.DEFAULT_NAME );

			m_logger.Debug("Testing...");

			MConnector connector = MConnectorFactory.CreateConnector( url, properties );
			connector.ServerConnection.GetDomains();

			m_logger.Debug("Done!");
		}

		#region IDisposable Members

		public void Dispose()
		{
			GC.SuppressFinalize(this);

			Stop();
		}

		#endregion
	}
}
