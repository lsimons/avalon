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

	using Apache.Avalon.Castle.ManagementExtensions.Default;
	using Apache.Avalon.Castle.ManagementExtensions.Test.Components;

	/// <summary>
	/// Summary description for MDefaultServerTestCase.
	/// </summary>
	[TestFixture]
	public class MDefaultServerTestCase : Assertion
	{
		protected MDefaultServer server = new MDefaultServer();
		protected Type httpServerType = typeof(DummyHttpServer);
		protected Type smtpServerType = typeof(DummySmtpServer);

		[Test]
		public void TestInstantiate()
		{
			Object obj = server.Instantiate( httpServerType.Assembly.FullName, httpServerType.FullName );
			AssertNotNull( obj );
			AssertEquals( httpServerType, obj.GetType() );
		}

		[Test]
		public void TestCreateManagedObject()
		{
			ManagedObjectName name = new ManagedObjectName("domain.org:type=httpServer");

			try
			{
				ManagedInstance inst = server.CreateManagedObject( 
					httpServerType.Assembly.FullName, httpServerType.FullName, name );
				AssertNotNull( inst );
				AssertEquals( httpServerType.FullName, inst.TypeName );
				AssertEquals( name, inst.Name );
			}
			finally
			{
				server.UnregisterManagedObject( name );
			}
		}

		[Test]
		public void TestRegisterManagedObject()
		{
			ManagedObjectName name = new ManagedObjectName("domain.org:type=httpServer");

			try
			{
				Object httpServer = server.Instantiate( httpServerType.Assembly.FullName, httpServerType.FullName );

				ManagedInstance inst = server.RegisterManagedObject( httpServer, name );
				AssertNotNull( inst );
				AssertEquals( httpServerType.FullName, inst.TypeName );
				AssertEquals( name, inst.Name );
			}
			finally
			{
				server.UnregisterManagedObject( name );
			}
		}

		[Test]
		public void TestGetManagementInfo()
		{
			ManagedObjectName name1 = new ManagedObjectName("domain.org:type=httpServer");
			ManagedObjectName name2 = new ManagedObjectName("domain.net:type=smtpServer");

			try
			{
				Object httpServer = server.Instantiate( httpServerType.Assembly.FullName, httpServerType.FullName );
				server.RegisterManagedObject( httpServer, name1 );

				ManagementInfo info = server.GetManagementInfo( name1 );
				AssertNotNull( info );
				AssertEquals( 3, info.Operations.Count );
				AssertEquals( 1, info.Attributes.Count );

				Object smtpServer = server.Instantiate( smtpServerType.Assembly.FullName, smtpServerType.FullName );

				try
				{
					server.RegisterManagedObject( smtpServer, name1 );

					Fail("Should not allow register with same name.");
				}
				catch(InstanceAlreadyRegistredException)
				{
					// OK
				}

				server.RegisterManagedObject( smtpServer, name2 );

				info = server.GetManagementInfo( name2 );
				AssertNotNull( info );
				AssertEquals( 2, info.Operations.Count );
				AssertEquals( 1, info.Attributes.Count );
			}
			finally
			{
				server.UnregisterManagedObject( name1 );
				server.UnregisterManagedObject( name2 );
			}
		}

		[Test]
		public void TestAttributes()
		{
			ManagedObjectName name = new ManagedObjectName("domain.net:type=smtpServer");

			try
			{
				Object smtpServer = server.Instantiate( smtpServerType.Assembly.FullName, smtpServerType.FullName );

				ManagedInstance inst = server.RegisterManagedObject( smtpServer, name );

				int port = (int) server.GetAttribute(name, "Port");
				AssertEquals( 1088, port );

				server.SetAttribute( name, "Port", 25 );
				
				port = (int) server.GetAttribute(name, "Port");
				AssertEquals( 25, port );
			}
			finally
			{
				server.UnregisterManagedObject( name );
			}
		}

		[Test]
		public void TestInvoke()
		{
			ManagedObjectName name = new ManagedObjectName("domain.org:type=httpServer");

			try
			{
				Object httpServer = server.Instantiate( httpServerType.Assembly.FullName, httpServerType.FullName );

				ManagedInstance inst = server.RegisterManagedObject( httpServer, name );

				bool state = (bool) server.GetAttribute(name, "Started");
				AssertEquals( false, state );

				server.Invoke( name, "Start", null, null );
				
				state = (bool) server.GetAttribute(name, "Started");
				AssertEquals( true, state );

				server.Invoke( name, "Stop", null, null );
				state = (bool) server.GetAttribute(name, "Started");
				AssertEquals( false, state );
			}
			finally
			{
				server.UnregisterManagedObject( name );
			}
		}
	}
}
