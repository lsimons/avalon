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
	
	/// <summary> 
	/// This descriptor defines the type of service offerend or required
	/// by a component. The type corresponds to the class name of the
	/// class/interface implemented by component. Associated with each
	/// classname is a version object so that different versions of same
	/// interface can be represented.
	/// 
	/// <p>Also associated with each service is a set of arbitrary
	/// attributes that can be used to store extra information
	/// about service. See {@link InfoDescriptor} for example
	/// of how to declare the container specific attributes.</p>
	/// 
	/// <p>Possible uses for the attributes are to declare a service
	/// as "stateless", "pass-by-value", "remotable" or even to attach
	/// attributes such as security or transaction constraints. These
	/// attributes are container specific and should not be relied
	/// upon to work in all containers.</p>
	/// 
	/// </summary>
	[Serializable]
	public class ServiceDescriptor : Descriptor
	{
		/// <summary> The service reference that descriptor is describing.</summary>
		private ReferenceDescriptor m_designator;
		
		/// <summary> Construct a service descriptor.
		/// 
		/// </summary>
		/// <param name="descriptor">the service descriptor
		/// </param>
		/// <exception cref=""> NullPointerException if the descriptor argument is null
		/// </exception>
		public ServiceDescriptor(ServiceDescriptor descriptor) : 
			base(descriptor.Properties, null)
		{
			m_designator = descriptor.Reference;
		}
		
		
		/// <summary> Construct a service descriptor for specified ReferenceDescriptor
		/// 
		/// </summary>
		/// <param name="designator">the service reference
		/// </param>
		/// <exception cref=""> NullPointerException if the designator argument is null
		/// </exception>
		public ServiceDescriptor(ReferenceDescriptor designator) : this(designator, null)
		{
		}
		
		/// <summary> Construct a service with specified name, version and attributes.
		/// 
		/// </summary>
		/// <param name="designator">the ReferenceDescriptor
		/// </param>
		/// <param name="attributes">the attributes of service
		/// </param>
		/// <exception cref=""> NullPointerException if the designator argument is null
		/// </exception>
		public ServiceDescriptor(ReferenceDescriptor designator, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			
			if (null == designator)
			{
				throw new System.NullReferenceException("designator");
			}
			
			m_designator = designator;
		}

		/// <summary> Retrieve the reference that service descriptor refers to.
		/// 
		/// </summary>
		/// <returns> the reference that service descriptor refers to.
		/// </returns>
		virtual public ReferenceDescriptor Reference
		{
			get
			{
				return m_designator;
			}
			
		}
		
		/// <summary> Return the cashcode for this instance.</summary>
		/// <returns> the instance hashcode
		/// </returns>
		public override int GetHashCode()
		{
			return m_designator.GetHashCode();
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = base.Equals(other) && other is ServiceDescriptor;
			isEqual = isEqual && m_designator.Equals(((ServiceDescriptor) other).m_designator);
			return isEqual;
		}
	}
}