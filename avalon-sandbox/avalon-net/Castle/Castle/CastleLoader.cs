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

		public MServer Server
		{
			get
			{
				return server;
			}
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
					MXUtil.Stop( server, CONTROLLER );
				}

				MServerFactory.Release(server);

				server = null;
			}
		}

		protected virtual void CreateMServer(CastleOptions options)
		{
			logger.Debug("Creating MServer");
			server = MServerFactory.CreateServer(options.DomainName, options.IsolatedDomain);
		}

		protected virtual void CreateController(CastleOptions options)
		{
			logger.Debug("Creating Controller");

			CastleController controllerInstance = new CastleController(options);

			logger.Debug("Registering Controller");
			controller = server.RegisterManagedObject( controllerInstance, CONTROLLER );

			logger.Debug("Invoking Create on Controller...");
			MXUtil.Create( server, CONTROLLER );

			logger.Debug("Invoking Start on Controller...");
			MXUtil.Start( server, CONTROLLER );
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
