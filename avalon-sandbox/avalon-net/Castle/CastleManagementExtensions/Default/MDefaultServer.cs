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

namespace Apache.Avalon.Castle.ManagementExtensions.Default
{
	using System;
	using System.Reflection;

	/// <summary>
	/// Summary description for MDefaultServer.
	/// </summary>
	public class MDefaultServer : MarshalByRefObject, MServer
	{
		protected MRegistry registry;

		public MDefaultServer()
		{
			registry = new Default.MDefaultRegistry();

			SetupRegistry();
		}

		public MDefaultServer(MRegistry registry)
		{
			if (registry == null)
			{
				throw new ArgumentNullException("registry");
			}

			this.registry = registry;

			SetupRegistry();
		}

		private void SetupRegistry()
		{
			RegisterManagedObject(registry, MConstants.REGISTRY_NAME);
		}

		#region MServer Members

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="typeName"></param>
		/// <returns></returns>
		public Object Instantiate(String typeName)
		{
			if (typeName == null)
			{
				throw new ArgumentNullException("typeName");
			}

			Type targetType = Type.GetType(typeName, true, true);

			object instance = AppDomain.CurrentDomain.CreateInstanceAndUnwrap(
				targetType.Assembly.FullName, targetType.FullName);

			return instance;
		}

		/// <summary>
		/// Instantiates the specified type using the server domain.
		/// </summary>
		/// <param name="typeName"></param>
		/// <param name="typeName"></param>
		/// <returns></returns>
		public Object Instantiate(String assemblyName, String typeName)
		{
			if (assemblyName == null)
			{
				throw new ArgumentNullException("assemblyName");
			}
			if (typeName == null)
			{
				throw new ArgumentNullException("typeName");
			}

			object instance = AppDomain.CurrentDomain.CreateInstanceAndUnwrap(
				assemblyName, typeName);

			return instance;
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="typeName"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		public ManagedInstance CreateManagedObject(String typeName, ManagedObjectName name)
		{
			if (typeName == null)
			{
				throw new ArgumentNullException("typeName");
			}
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			Object instance = Instantiate(typeName);
			return RegisterManagedObject(instance, name);
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="assemblyName"></param>
		/// <param name="typeName"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		public ManagedInstance CreateManagedObject(String assemblyName, String typeName, ManagedObjectName name)
		{
			if (assemblyName == null)
			{
				throw new ArgumentNullException("assemblyName");
			}
			if (typeName == null)
			{
				throw new ArgumentNullException("typeName");
			}
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			Object instance = Instantiate(assemblyName, typeName);
			return RegisterManagedObject(instance, name);
		}


		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="instance"></param>
		/// <param name="name"></param>
		/// <returns></returns>
        public ManagedInstance RegisterManagedObject(Object instance, ManagedObjectName name)
		{
			if (instance == null)
			{
				throw new ArgumentNullException("instance");
			}
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			return registry.RegisterManagedObject(instance, name);
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		public ManagedInstance GetManagedInstance(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			return registry.GetManagedInstance(name);
		}

		/// <summary>
		/// TODO: Summary
		/// </summary>
		/// <param name="name"></param>
		public void UnregisterManagedObject(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			registry.UnregisterManagedObject(name);
		}

		/// <summary>
		/// Invokes an action in managed object
		/// </summary>
		/// <param name="name"></param>
		/// <param name="action"></param>
		/// <param name="args"></param>
		/// <param name="signature"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public Object Invoke(ManagedObjectName name, String action, Object[] args, Type[] signature)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			if (action == null)
			{
				throw new ArgumentNullException("action");
			}
			return registry.Invoke(name, action, args, signature);
		}

		/// <summary>
		/// Returns the info (attributes and operations) about the specified object.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public ManagementInfo GetManagementInfo(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			
			return registry.GetManagementInfo(name);
		}

		/// <summary>
		/// Gets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public Object GetAttribute(ManagedObjectName name, String attributeName)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			if (attributeName == null)
			{
				throw new ArgumentNullException("attributeName");
			}

			return registry.GetAttributeValue(name, attributeName);
		}

		/// <summary>
		/// Sets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <param name="attributeValue"></param>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public void SetAttribute(ManagedObjectName name, String attributeName, Object attributeValue)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			if (attributeName == null)
			{
				throw new ArgumentNullException("attributeName");
			}
			
			registry.SetAttributeValue(name, attributeName, attributeValue);
		}

		#endregion
	}
}
