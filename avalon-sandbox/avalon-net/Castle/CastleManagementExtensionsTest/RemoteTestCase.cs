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

namespace Apache.Avalon.Castle.ManagementExtensions.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.ManagementExtensions.Default;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Server;
	using Apache.Avalon.Castle.ManagementExtensions.Remote.Client;

	/// <summary>
	/// Summary description for RemoteTestCase.
	/// </summary>
	[TestFixture]
	public class RemoteTestCase : Assertion
	{
		MServer server = null;

		[SetUp]
		public void Init()
		{
			server = MServerFactory.CreateServer("test", true);
		}

		[TearDown]
		public void Terminate()
		{
			MServerFactory.Release(server);
		}

		[Test]
		public void TestServerCreation()
		{
			MConnectorServer serverConn = 
					   MConnectorServerFactory.CreateServer( "provider:http:binary:test.rem", null, server );

			AssertNotNull( serverConn );

			ManagedObjectName name = new ManagedObjectName("connector.http:formatter=binary");
			server.RegisterManagedObject( serverConn, name );

			AssertEquals( name, serverConn.ManagedObjectName );

			AppDomain child = null;
		
			try
			{
				child = AppDomain.CreateDomain(
					"Child", 
					new System.Security.Policy.Evidence(AppDomain.CurrentDomain.Evidence), 
					AppDomain.CurrentDomain.SetupInformation);

				RemoteClient client = (RemoteClient) 
					child.CreateInstanceAndUnwrap( typeof(RemoteClient).Assembly.FullName, typeof(RemoteClient).FullName );

				AssertNotNull( client.TestClientCreation() );
			}
			finally
			{
				server.UnregisterManagedObject( name );

				if (child != null)
				{
					AppDomain.Unload(child);
				}
			}
		}

		[Test]
		public void TestTcpServerCreation()
		{
			System.Collections.Specialized.NameValueCollection props = 
				new System.Collections.Specialized.NameValueCollection();
			props.Add("port", "3131");

			MConnectorServer serverConn = 
					   MConnectorServerFactory.CreateServer( "provider:tcp:binary:test.rem", props, null );
			AssertNotNull( serverConn );

			ManagedObjectName name = new ManagedObjectName("connector.tcp:formatter=binary");
			server.RegisterManagedObject( serverConn, name );

			AssertEquals( name, serverConn.ManagedObjectName );

			AppDomain child = null;

			try
			{
				child = AppDomain.CreateDomain(
					"Child", 
					new System.Security.Policy.Evidence(AppDomain.CurrentDomain.Evidence), 
					AppDomain.CurrentDomain.SetupInformation);

				RemoteClient client = (RemoteClient) 
					child.CreateInstanceAndUnwrap( typeof(RemoteClient).Assembly.FullName, typeof(RemoteClient).FullName );

				AssertNotNull( client.TestTcpClientCreation() );
			}
			finally
			{
				server.UnregisterManagedObject( name );

				if (child != null)
				{
					AppDomain.Unload(child);
				}
			}
		}
	}

	public class RemoteClient : MarshalByRefObject
	{
		public String[] TestClientCreation()
		{
			using(MConnector connector = MConnectorFactory.CreateConnector( "provider:http:binary:test.rem", null ))
			{
				Assertion.AssertNotNull( connector );
				Assertion.AssertNotNull( connector.ServerConnection );

				MServer server = (MServer) connector.ServerConnection;
				String[] domains = server.GetDomains();
				Assertion.AssertNotNull( domains );
				return domains;
			}
		}

		public String[] TestTcpClientCreation()
		{
			System.Collections.Specialized.NameValueCollection props = 
				new System.Collections.Specialized.NameValueCollection();
			props.Add("port", "3131");

			using(MConnector connector = MConnectorFactory.CreateConnector( "provider:tcp:binary:test.rem", props ))
			{
				Assertion.AssertNotNull( connector );
				Assertion.AssertNotNull( connector.ServerConnection );

				MServer server = (MServer) connector.ServerConnection;
				Assertion.AssertNotNull( server.GetDomains() );
				return server.GetDomains();
			}
		}
	}
}
