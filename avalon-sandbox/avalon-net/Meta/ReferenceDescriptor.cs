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
	
	/// <summary> This reference defines the type of interface required
	/// by a component. The type corresponds to the class name of the
	/// interface implemented by component. Associated with each
	/// classname is a version object so that different versions of same
	/// interface can be represented.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/01/31 18:59:17 $
	/// </version>
	[Serializable]
	public sealed class ReferenceDescriptor
	{
		/// <summary> The service class.</summary>
		private System.Type m_type;
		
		/// <summary> Construct a service with specified name, version and attributes.
		/// 
		/// </summary>
		/// <param name="classname">the name of the service
		/// </param>
		/// <param name="version">the version of service
		/// </param>
		/// <exception cref=""> NullPointerException if the classname or version is null
		/// </exception>
		/// <exception cref=""> IllegalArgumentException if the classname string is invalid
		/// </exception>
		public ReferenceDescriptor(System.Type type)
		{
			if (null == (System.Object) type)
			{
				throw new System.ArgumentNullException("type");
			}
			
			m_type = type;
		}

		/// <summary> 
		/// Return classname of interface this reference refers to.
		/// </summary>
		/// <returns> the classname of the Service
		/// </returns>
		public System.Type Type
		{
			get
			{
				return m_type;
			}			
		}

		/// <summary> Determine if specified service will match this service.
		/// To match a service has to have same name and must comply with version.
		/// 
		/// </summary>
		/// <param name="other">the other ServiceInfo
		/// </param>
		/// <returns> true if matches, false otherwise
		/// </returns>
		public bool Matches(ReferenceDescriptor other)
		{
			return m_type.Equals(other.m_type);
		}
		
		/// <summary> Convert to a string of format name:version
		/// 
		/// </summary>
		/// <returns> string describing service
		/// </returns>
		public override System.String ToString()
		{
			return Type.FullName;
		}
		
		/// <summary> Compare this object with another for equality.</summary>
		/// <param name="other">the object to compare this object with
		/// </param>
		/// <returns> TRUE if the supplied object is a reference, service, or service
		/// descriptor that matches this objct in terms of classname and version
		/// </returns>
		public override bool Equals(System.Object other)
		{
			bool match = false;
			
			//
			// TODO: check validity of the following - this is 
			// assuming the equality is equivalent to compliance
			// which is not true
			//
			
			if (other is ReferenceDescriptor)
			{
				match = ((ReferenceDescriptor) other).Matches(this);
			}
			else if (other is Service)
			{
				match = ((Service) other).Matches(this);
			}
			else if (other is ServiceDescriptor)
			{
				match = ((ServiceDescriptor) other).Reference.Matches(this);
			}
			
			return match;
		}
		
		/// <summary> Returns the cashcode.</summary>
		/// <returns> the hascode value
		/// </returns>
		public override int GetHashCode()
		{
			return Type.GetHashCode();
		}
	}
}