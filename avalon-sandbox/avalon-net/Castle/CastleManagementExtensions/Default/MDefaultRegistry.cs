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

	/// <summary>
	/// Default (and simple) implementation of <see cref="Apache.Avalon.Castle.ManagementExtensions.MRegistry"/>.
	/// TODO: Implement a lyfecycle for registering managed components.
	/// </summary>
	[ManagedComponent]
	public class MDefaultRegistry : MarshalByRefObject, MRegistry
	{
		protected DomainCollection domains = new DomainCollection();

		protected MServer server;

		public MDefaultRegistry(MServer server)
		{
			if (server == null)
			{
				throw new ArgumentNullException("server");
			}

			this.server = server;
		}

		#region MRegistry Members

		/// <summary>
		/// Registers an instance if its not already registered.
		/// </summary>
		/// <param name="instance">Instance to be register - can't be null.</param>
		/// <param name="name">Instance's name - can't be null.</param>
		/// <returns>A ManagedInstance representing the instance data.</returns>
		[ManagedOperation]
		public ManagedInstance RegisterManagedObject(Object instance, ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			if (instance == null)
			{
				throw new ArgumentNullException("instance");
			}

			ComponentType cType = MInspector.Inspect(instance);
			MDynamicSupport dynamic = null;

			if (cType == ComponentType.None)
			{
				throw new InvalidComponentException("Component is not a managed component.");
			}
			
			if (cType == ComponentType.Standard)
			{
				dynamic = MDynamicSupportFactory.Create(instance);
			}
			else if (cType == ComponentType.Dynamic)
			{
				dynamic = (MDynamicSupport) instance;
			}

			String domainName = name.Domain;
			Domain domain = null;

			lock(domains)
			{
				domain = domains[domainName];

				if (domain == null)
				{
					domain = new Domain(domainName);
					domains.Add(domain);
				}
			}

			Entry entry = new Entry(instance, dynamic);

			try
			{
				MRegistrationListener registration = instance as MRegistrationListener;

				InvokeBeforeRegister(registration, name);

				lock(domain)
				{
					if (domain.Contains(name))
					{
						throw new InstanceAlreadyRegistredException(name.ToString());
					}

					domain.Add(name, entry);
				}

				InvokeAfterRegister(registration);
			}
			catch(Exception e)
			{
				domain.Remove(name);

				throw e;
			}

			return new ManagedInstance(instance.GetType().FullName, name);
		}

		/// <summary>
		/// Returns <see cref="Apache.Avalon.Castle.ManagementExtensions.ManagedInstance"/>
		/// of specified <see cref="Apache.Avalon.Castle.ManagementExtensions.ManagedObjectName"/>.
		/// </summary>
		/// <param name="name">The name to be located.</param>
		/// <returns>A ManagedInstance representing the instance data.</returns>
		[ManagedOperation]
		public ManagedInstance GetManagedInstance(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			return new ManagedInstance(GetEntry(name).Instance.GetType().FullName, name);
		}

		/// <summary>
		/// Unregisters the specified object.
		/// </summary>
		/// <param name="name">The name to be located.</param>
		[ManagedOperation]
		public void UnregisterManagedObject(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			String domainName = name.Domain;
			
			try
			{
				Domain domain = FindDomain(domainName);
				Entry entry = domain[name];

				if (entry != null)
				{
					MRegistrationListener listener = entry.Instance as MRegistrationListener;

					InvokeBeforeDeregister(listener);

					domain.Remove(name);

					InvokeAfterDeregister(listener);
				}
			}
			catch(InvalidDomainException)
			{
			}
		}

		/// <summary>
		/// Returns true if the Registry contains the specified object.
		/// </summary>
		/// <param name="name">The name to be located.</param>
		/// <returns>true if the object could be found</returns>
		[ManagedOperation]
		public bool Contains(ManagedObjectName name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			String domainName = name.Domain;
			
			Domain domain = FindDomain(domainName);

			return domain.Contains(name);
		}

		/// <summary>
		/// Returns the number of currently registered objects.
		/// </summary>
		[ManagedAttribute]
		public int Count
		{
			get
			{
				int total = 0;

				foreach(Domain domain in domains)
				{
					total += domain.Count;
				}

				return total;
			}
		}

		/// <summary>
		/// Indexer for registered objects.
		/// </summary>
		public Object this[ManagedObjectName name]
		{
			get
			{
				if (name == null)
				{
					throw new ArgumentNullException("name");
				}

				return GetEntry(name).Instance;
			}
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
			return GetEntry(name).Invoker.Invoke(action, args, signature);
		}

		/// <summary>
		/// Returns the info (attributes and operations) about the specified object.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public ManagementInfo GetManagementInfo(ManagedObjectName name)
		{
			return GetEntry(name).Invoker.Info;
		}

		/// <summary>
		/// Gets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public Object GetAttributeValue(ManagedObjectName name, String attributeName)
		{
			return GetEntry(name).Invoker.GetAttributeValue(attributeName);
		}

		/// <summary>
		/// Sets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <param name="attributeValue"></param>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		public void SetAttributeValue(ManagedObjectName name, String attributeName, Object attributeValue)
		{
			GetEntry(name).Invoker.SetAttributeValue(attributeName, attributeValue);
		}

		/// <summary>
		/// Returns an array of registered domains.
		/// </summary>
		/// <returns>a list of domains</returns>
		public String[] GetDomains()
		{
			return domains.ToArray();
		}

		/// <summary>
		/// Queries the registerd components.
		/// </summary>
		/// <returns></returns>
		public ManagedObjectName[] Query(ManagedObjectName query)
		{
			// TODO: several queries...
			
			if (query.LiteralProperties.Equals("*"))
			{
				return FindAllFromDomain(query.Domain);
			}

			return null;
		}

		#endregion

		/// <summary>
		/// Helper to locate the domain.
		/// </summary>
		/// <param name="domainName"></param>
		/// <returns></returns>
		private Domain FindDomain(String domainName)
		{
			Domain domain = domains[domainName];
				
			if (domain == null)
			{
				throw new InvalidDomainException(domainName);
			}

			return domain;
		}

		/// <summary>
		/// Helper to locate Entries.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		private Entry GetEntry(ManagedObjectName name)
		{
			Domain domain = FindDomain(name.Domain);
			Entry entry = domain[name];

			if (entry == null)
			{
				throw new ManagedObjectNotFoundException(name.ToString());
			}

			return entry;
		}

		private void InvokeBeforeRegister(MRegistrationListener listener, ManagedObjectName name)
		{
			if (listener != null)
			{
				listener.BeforeRegister(server, name);
			}
		}

		private void InvokeAfterRegister(MRegistrationListener listener)
		{
			if (listener != null)
			{
				listener.AfterRegister();
			}
		}

		private void InvokeBeforeDeregister(MRegistrationListener listener)
		{
			if (listener != null)
			{
				try
				{
					listener.BeforeDeregister();
				}
				catch(Exception)
				{
					// An exception here shall not stop us from continue
				}
			}
		}

		private void InvokeAfterDeregister(MRegistrationListener listener)
		{
			if (listener != null)
			{
				try
				{
					listener.AfterDeregister();
				}
				catch(Exception)
				{
					// An exception here shall not stop us from continue
				}
			}
		}

		private ManagedObjectName[] FindAllFromDomain(String domainName)
		{
			try
			{
				Domain domain = FindDomain(domainName);
				return domain.ToArray();
			}
			catch(InvalidDomainException)
			{
			}

			return null;
		}
	}
}