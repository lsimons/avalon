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

namespace Apache.Avalon.Castle.ManagementExtensions.Remote
{
	using System;

	/// <summary>
	/// Summary description for MServerProxy.
	/// </summary>
	public class MServerProxy : MarshalByRefObject, MServer
	{
		protected MServer server;

		public MServerProxy(MServer server)
		{
			if (server == null)
			{
				throw new ArgumentNullException("server");
			}

			this.server = server;
		}

		#region MServer Members

		public Object Instantiate(String typeName)
		{
			return server.Instantiate(typeName);
		}

		public Object Instantiate(String assemblyName, String typeName)
		{
			return server.Instantiate(assemblyName, typeName);
		}

		public ManagedInstance CreateManagedObject(String typeName, ManagedObjectName name)
		{
			return server.CreateManagedObject(typeName, name);
		}

		public ManagedInstance CreateManagedObject(String assemblyName, String typeName, ManagedObjectName name)
		{
			return server.CreateManagedObject(assemblyName, typeName, name);
		}

		public ManagedInstance RegisterManagedObject(Object instance, ManagedObjectName name)
		{
			return server.RegisterManagedObject(instance, name);
		}

		public ManagedInstance GetManagedInstance(ManagedObjectName name)
		{
			return server.GetManagedInstance(name);
		}

		public void UnregisterManagedObject(ManagedObjectName name)
		{
			server.UnregisterManagedObject(name);
		}

		public Object Invoke(ManagedObjectName name, String action, Object[] args, Type[] signature)
		{
			return server.Invoke(name, action, args, signature);
		}

		public ManagementInfo GetManagementInfo(ManagedObjectName name)
		{
			return server.GetManagementInfo(name);
		}

		public Object GetAttribute(ManagedObjectName name, String attributeName)
		{
			return server.GetAttribute(name, attributeName);
		}

		public void SetAttribute(ManagedObjectName name, String attributeName, Object attributeValue)
		{
			server.SetAttribute(name, attributeName, attributeValue);
		}

		public ManagedObjectName[] Query(ManagedObjectName query)
		{
			return server.Query(query);
		}

		#endregion

		#region MServerConnection Members

		public String[] GetDomains()
		{
			return server.GetDomains();
		}

		#endregion
	}
}
