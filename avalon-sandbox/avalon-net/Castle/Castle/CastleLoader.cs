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

namespace Apache.Avalon.Castle
{
	using System;
	using System.Reflection;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Server;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Client;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for CastleLoader.
	/// </summary>
	public class CastleLoader : IDisposable
	{
		protected static readonly ManagedObjectName CONTROLLER = 
			new ManagedObjectName(Castle.CASTLE_DOMAIN + ":name=Controller");
		
		protected MServer server;

		protected MConnectorServer connectorServer;

		protected ManagedInstance controller;

		protected ILogger logger = Logger.LoggerFactory.GetLogger("CastleLoader");

		public CastleLoader()
		{
		}

		~CastleLoader()
		{
			logger.Debug("Running Finalizer()");
			
			Stop();
		}

		public void Start(CastleOptions options)
		{
			logger.Debug("Start()");

			CreateMServer(options);
			CreateController(options);
			
			if (options.EnableRemoteManagement)
			{
				CreateServerConnector(options.ServerConnectorUrl, null);
			}
		}

		public MServer Server
		{
			get
			{
				return server;
			}
		}

		public void Stop()
		{
			logger.Debug("Stop()");

			if (connectorServer != null && connectorServer.ManagedObjectName != null )
			{
				server.UnregisterManagedObject( connectorServer.ManagedObjectName );
				
				connectorServer = null;
			}

			if (server != null)
			{
				if (controller != null)
				{
					server.Invoke(controller.Name, "Stop", null, null);
				}

				MServerFactory.Release(server);

				server = null;
			}
		}

		protected virtual void CreateMServer(CastleOptions options)
		{
			logger.Debug("Creating MServer");
			
			server = MServerFactory.CreateServer(options.DomainName, options.IsolatedDomain);
			
			logger.Debug("Done!");
		}

		protected virtual void CreateController(CastleOptions options)
		{
			logger.Debug("Creating Controller");

			controller = server.CreateManagedObject( 
				Assembly.GetExecutingAssembly().FullName, 
				"Apache.Avalon.Castle.Controller.CastleController",
				CONTROLLER);

			logger.Debug("Done!");

			logger.Debug("Invoking Create on Controller...");
			server.Invoke(controller.Name, "Create", null, null);
			logger.Debug("Done!");

			logger.Debug("Invoking Start on Controller...");
			server.Invoke(controller.Name, "Start", null, null);
			logger.Debug("Done!");
		}

		protected virtual void CreateServerConnector(String url, System.Collections.Specialized.NameValueCollection properties)
		{
			logger.Debug("Creating ServerConnector");

			connectorServer = MConnectorServerFactory.CreateServer(url, properties, null );

			server.RegisterManagedObject( connectorServer, MConnectorServer.DEFAULT_NAME );

			logger.Debug("Testing...");

			MConnector connector = MConnectorFactory.CreateConnector( url, properties );
			connector.ServerConnection.GetDomains();

			logger.Debug("Done!");
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
