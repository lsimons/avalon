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

namespace Apache.Avalon.Castle.ManagementExtensions
{
	using System;
	using System.Collections;
	using System.Configuration;
	using System.Security.Policy;

	using Apache.Avalon.Castle.ManagementExtensions.Default;

	/// <summary>
	/// Summary description for MServerFactory.
	/// </summary>
	public sealed class MServerFactory
	{
		public static readonly String CustomServerConfigurationKey = "MServerFactory";

		private static readonly Hashtable domains = Hashtable.Synchronized(
			new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default));

		private MServerFactory()
		{
		}

		/// <summary>
		/// Creates a <see cref="MServer"/> instance.
		/// </summary>
		/// <param name="createNewAppDomain">true if MServerFactory should create a dedicated
		/// AppDomain for the <see cref="MServer"/> instance.</param>
		/// <returns>A <see cref="MServer"/> instance.</returns>
		public static MServer CreateServer(bool createNewAppDomain)
		{
			return CreateServer(String.Empty, createNewAppDomain);
		}

		/// <summary>
		/// Creates a <see cref="MServer"/> instance.
		/// </summary>
		/// <param name="domain">The domain name</param>
		/// <param name="createNewAppDomain">true if MServerFactory should create a dedicated
		/// AppDomain for the <see cref="MServer"/> instance.</param>
		/// <returns>A <see cref="MServer"/> instance.</returns>
		public static MServer CreateServer(String domain, bool createNewAppDomain)
		{
			if (domain == null)
			{
				throw new ArgumentNullException("domain");
			}

			if (domains.Contains(domain))
			{
				throw new DomainAlreadyExistsException(domain);
			}

			String typeName = ConfigurationSettings.AppSettings[CustomServerConfigurationKey];
			Type serverType = null;

			if (typeName != null && typeName != String.Empty)
			{
				// TODO: Allow custom servers..
			}
			else
			{
				serverType = typeof(MDefaultServer);
			}

			if (createNewAppDomain)
			{
				// Lets create a seperated AppDomain for this server
				
				AppDomain currentDomain = AppDomain.CurrentDomain;

				String configFile =  String.Format(
					"{0}{1}.config", 
					currentDomain.BaseDirectory, domain); 

				AppDomainSetup setup = new AppDomainSetup();

				setup.ApplicationName = domain;
				setup.ApplicationBase = currentDomain.SetupInformation.ApplicationBase;
				setup.PrivateBinPath = currentDomain.SetupInformation.PrivateBinPath;
				setup.ConfigurationFile = configFile;
				// setup.ShadowCopyFiles = "false";
				// setup.ShadowCopyDirectories = appBase;

				Evidence baseEvidence = currentDomain.Evidence;
				Evidence evidence = new Evidence(baseEvidence);

				AppDomain newDomain = AppDomain.CreateDomain(
					domain, evidence, setup);

				object remoteInstance = newDomain.CreateInstanceAndUnwrap(
					serverType.Assembly.FullName, serverType.FullName);

				// Register the domain

				domains.Add(domain, new DomainInfo( domain, remoteInstance as MServer, newDomain) );

				// As this already method "unwraps" the target object, its safe
				// to return it - in an "wrapped" object we should invoke the 
				// class's constructor

				return (MServer) remoteInstance;
			}
			else
			{
				object localInstance = Activator.CreateInstance(serverType);

				// Register the domain

				domains.Add(domain, new DomainInfo( domain, localInstance as MServer ) );

				return (MServer) localInstance;
			}
		}

		/// <summary>
		/// Releases a <see cref="MServer"/> instance. This method
		/// accepts a null argument.
		/// </summary>
		/// <param name="server">The <see cref="MServer"/> instance to be released.</param>
		public static void Release(MServer server)
		{
			if (server != null)
			{
				foreach(DomainInfo info in domains.Values)
				{
					if (info.Server == server)
					{
						domains.Remove( info.Name );

						if (info.DedicatedDomain != null)
						{
							AppDomain.Unload( info.DedicatedDomain );
						}

						break;
					}
				}
			}
		}
	}

	/// <summary>
	/// Holds registered domains information.
	/// </summary>
	class DomainInfo
	{
		public String Name;
		public AppDomain DedicatedDomain;
		public MServer Server;

		private DomainInfo(String name)
		{
			this.Name = name;
		}

		public DomainInfo(String name, MServer Server) : this(name)
		{
			this.Server = Server;
		}

		public DomainInfo(String name, MServer Server, AppDomain domain) : this(name, Server)
		{
			this.DedicatedDomain = domain;
		}
	}
}
