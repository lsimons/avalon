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

namespace Apache.Avalon.Castle.ManagementExtensions.Remote.Server
{
	using System;
	using System.Runtime.Remoting;
	using System.Runtime.Remoting.Channels;

	/// <summary>
	/// Summary description for MConnectorServer.
	/// </summary>
	[ManagedComponent]
	public class MConnectorServer : MarshalByRefObject, MRegistrationListener, IDisposable
	{
		public static readonly ManagedObjectName DEFAULT_NAME = new ManagedObjectName("connector.server");

		protected MServer server;
		protected MServerProxy serverProxy;
		protected String objectUri;
		protected ManagedObjectName name;

		private bool initDone = false;

		public MConnectorServer()
		{
		}

		public MConnectorServer(String objectUri)
		{
			this.objectUri = objectUri;
		}

		public MConnectorServer(MServer server, String objectUri) : this(objectUri)
		{
			if (IsProxy(server))
			{
				throw new ArgumentException("Argument can't be transparent proxy", "server");
			}

			this.server = server;

			RegisterServer();
		}

		~MConnectorServer()
		{
			DeregisterServer();
		}

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			this.server = server;
			this.name = name;

			RegisterServer();
		}

		public void AfterRegister()
		{
		}

		public void BeforeDeregister()
		{
		}

		public void AfterDeregister()
		{
			DeregisterServer();
		}

		#endregion

		[ManagedAttribute]
		public ManagedObjectName ManagedObjectName
		{
			get
			{
				return name;
			}
		}

		[ManagedAttribute]
		public String ServerUri
		{
			get
			{
				return objectUri;
			}
			set
			{
				objectUri = value;
			}
		}

		[ManagedAttribute]
		public MServer Server
		{
			get
			{
				return server;
			}
			set
			{
				if (IsProxy(value))
				{
					throw new ArgumentException("Argument can't be transparent proxy", "server");
				}
				server = value;
			}
		}

		private void RegisterServer()
		{
			if (initDone)
			{
				return;
			}

			if (serverProxy == null)
			{
				serverProxy = new MServerProxy(server);
			}

			ObjRef objref = RemotingServices.Marshal(
				serverProxy, ServerUri, typeof(MServerProxy) );

			initDone = true;
		}

		private void DeregisterServer()
		{
			if (initDone)
			{
				if (!RemotingServices.IsTransparentProxy( serverProxy ))
				{
					RemotingServices.Disconnect( serverProxy );
				}
				initDone = false;
			}
		}

		private bool IsProxy(object obj)
		{
			return RemotingServices.IsTransparentProxy( obj );
		}

		#region IDisposable Members

		public void Dispose()
		{
			DeregisterServer();
			GC.SuppressFinalize(this);
		}

		#endregion
	}
}
