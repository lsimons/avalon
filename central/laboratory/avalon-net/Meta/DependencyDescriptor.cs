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

namespace Apache.Avalon.Meta
{
	using System;
	using Apache.Avalon.Framework;
	
	/// <summary> A descriptor that describes dependency information for
	/// a particular Component. This class contains information
	/// about;
	/// <ul>
	/// <li>role: the name component uses to look up dependency</li>
	/// <li>service: the class/interface that the dependency must provide</li>
	/// </ul>
	/// 
	/// <p>Also associated with each dependency is a set of arbitrary
	/// attributes that can be used to store extra information
	/// about dependency. See {@link InfoDescriptor} for example
	/// of how to declare the container specific attributes.</p>
	/// 
	/// <p>Possible uses for the attributes are to declare container
	/// specific constraints of component. For example a dependency on
	/// a Corba ORB may also require that the Corba ORB contain the
	/// TimeServer and PersistenceStateService at initialization. Or it
	/// may require that the componenet be multi-thread safe or that
	/// it is persistent etc. These are all container specific
	/// demands.</p>
	/// </summary>
	[Serializable]
	public sealed class DependencyDescriptor : Descriptor
	{
		/// <summary> The name the component uses to lookup dependency.</summary>
		private System.String m_key;
		
		/// <summary> The service class/interface that the dependency must provide.</summary>
		private ReferenceDescriptor m_service;
		
		/// <summary> True if dependency is optional, false otherwise.</summary>
		private bool m_optional;
		
		/// <summary> Creation of a new dependency descriptor using the default 1.0 version</summary>
		/// <param name="role">the role name that will be used by the type when looking up a service
		/// </param>
		/// <param name="service">the interface service
		/// </param>
		public DependencyDescriptor(System.String role, System.Type service, System.Reflection.MemberInfo memberinfo) : 
			this(role, new ReferenceDescriptor( service ), memberinfo )
		{
		}
		
		/// <summary> Creation of a new dependency descriptor.</summary>
		/// <param name="role">the role name that will be used by the type when looking up a service
		/// </param>
		/// <param name="service">the version insterface service reference
		/// </param>
		public DependencyDescriptor(System.String role, ReferenceDescriptor service, System.Reflection.MemberInfo memberinfo) : 
			this(role, service, false, null, memberinfo)
		{
		}
		
		/// <summary> Creation of a new dependency descriptor.</summary>
		/// <param name="role">the role name that will be used by the type when looking up a service
		/// </param>
		/// <param name="service">the version insterface service reference
		/// </param>
		/// <param name="optional">TRUE if this depedency is optional
		/// </param>
		/// <param name="attributes">a set of attributes to associate with the dependency
		/// </param>
		public DependencyDescriptor(System.String role, 
			ReferenceDescriptor service, bool optional, 
			System.Collections.Specialized.NameValueCollection attributes,
			System.Reflection.MemberInfo memberinfo) : 
			base(attributes, memberinfo)
		{
			if (null == (System.Object) role)
			{
				throw new System.NullReferenceException("role");
			}
			
			if (null == service)
			{
				throw new System.NullReferenceException("service");
			}
			
			m_key = role;
			m_service = service;
			m_optional = optional;
		}

		/// <summary> Return the name the component uses to lookup the dependency.
		/// 
		/// </summary>
		/// <returns> the name the component uses to lookup the dependency.
		/// </returns>
		public System.String Key
		{
			get
			{
				return m_key;
			}
			
		}
		/// <summary> Return the service interface descriptor that describes the
		/// dependency that the provider provides.
		/// 
		/// </summary>
		/// <returns> a reference to service reference that describes the fulfillment
		/// obligations that must be met by a service provider.
		/// </returns>
		/// <deprecated> use getReference()
		/// </deprecated>
		public ReferenceDescriptor Service
		{
			get
			{
				return m_service;
			}
			
		}

		/// <summary> Return true if dependency is optional, false otherwise.
		/// 
		/// </summary>
		/// <returns> true if dependency is optional, false otherwise.
		/// </returns>
		public bool Optional
		{
			get
			{
				return m_optional;
			}
			
		}
		/// <summary> Return true if dependency is required, false otherwise.
		/// 
		/// </summary>
		/// <returns> true if dependency is required, false otherwise.
		/// </returns>
		public bool Required
		{
			get
			{
				return !Optional;
			}
			
		}
		
		public override System.String ToString()
		{
			return "[" + Key + "] " + Service;
		}
		
		/// <summary> Compare this object with another for equality.</summary>
		/// <param name="other">the object to compare this object with
		/// </param>
		/// <returns> TRUE if the supplied object is a reference, service, or service
		/// descriptor that matches this objct in terms of classname and version
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = base.Equals(other) && other is DependencyDescriptor;
			if (other is DependencyDescriptor)
			{
				DependencyDescriptor dep = (DependencyDescriptor) other;
				
				isEqual = isEqual && m_optional == dep.m_optional;
				isEqual = isEqual && m_service.Equals(dep.m_service);
			}
			
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_service.GetHashCode();
			
			return hash;
		}
	}
}