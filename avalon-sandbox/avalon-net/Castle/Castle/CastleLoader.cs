// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

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
