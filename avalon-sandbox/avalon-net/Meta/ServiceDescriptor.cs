// Copyright 2004 Apache Software Foundation
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