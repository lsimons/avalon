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
		public DependencyDescriptor(System.String role, System.String service, System.Reflection.MemberInfo memberinfo) : 
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
		/// <summary> Return the service class/interface descriptor that describes the
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
		/// <summary> Return the service class/interface descriptor that describes the
		/// dependency must fulfilled by a provider.
		/// 
		/// </summary>
		/// <returns> a reference to service reference that describes the fulfillment
		/// obligations that must be met by a service provider.
		/// </returns>
		public ReferenceDescriptor Reference
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
			return "[" + Key + "] " + Reference;
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