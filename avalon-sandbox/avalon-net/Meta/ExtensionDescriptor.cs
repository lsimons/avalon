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
	
	/// <summary> A descriptor that describes a name and inteface of a lifecycle stage.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.3 $ $Date: 2004/01/31 18:59:17 $
	/// </version>
	[Serializable]
	public sealed class ExtensionDescriptor : Descriptor
	{
		
		/// <summary> The extension identifier.</summary>
		private System.String m_urn;
		
		/// <summary> Creation of an extension descriptor without attributes.</summary>
		/// <param name="urn">the extension identifier
		/// </param>
		/// <exception cref=""> NullPointerException if the urn identifer is null
		/// </exception>
		public ExtensionDescriptor(System.String urn):this(urn, null)
		{
		}
		
		/// <summary> Creation of a extension descriptor with attributes.</summary>
		/// <param name="urn">the extension identifier
		/// </param>
		/// <param name="attributes">a set of attributes to associate with the extension
		/// </param>
		/// <exception cref=""> NullPointerException if the supplied urn is null
		/// </exception>
		public ExtensionDescriptor(System.String urn, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			
			if (null == (System.Object) urn)
			{
				throw new System.NullReferenceException("urn");
			}
			
			m_urn = urn;
		}

		/// <summary> Return the interface reference
		/// 
		/// </summary>
		/// <returns> the reference.
		/// </returns>
		public System.String Key
		{
			get
			{
				return m_urn;
			}
			
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			if (other is ExtensionDescriptor)
			{
				if (base.Equals(other))
				{
					return m_urn.Equals(((ExtensionDescriptor) other).m_urn);
				}
			}
			return false;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_urn.GetHashCode();
			return hash;
		}
		
		/// <summary> Return a stringified representation of the instance.</summary>
		/// <returns> the string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[extension " + Key + "]";
		}
	}
}