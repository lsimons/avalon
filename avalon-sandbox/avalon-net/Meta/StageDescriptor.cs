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
	/// A descriptor that describes a name and inteface of a lifecycle stage.
	/// </summary>
	[Serializable]
	public class StageDescriptor : Descriptor
	{
		
		
		/// <summary> The stage identifier.</summary>
		private System.String m_urn;
		
		/// <summary> Constructor a stage descriptor without attributes.</summary>
		/// <param name="urn">the stage identifier
		/// </param>
		/// <exception cref=""> NullPointerException if the classname argument is null
		/// </exception>
		public StageDescriptor(System.String urn):this(urn, null)
		{
		}
		
		/// <summary> Constructor a stage descriptor with attributes.</summary>
		/// <param name="urn">the stage identifier
		/// </param>
		/// <param name="attributes">a set of attribute values to associated with the stage
		/// </param>
		/// <exception cref=""> NullPointerException if the reference argument is null
		/// </exception>
		public StageDescriptor(System.String urn, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			
			if (null == (System.Object) urn)
			{
				throw new System.NullReferenceException("urn");
			}
			m_urn = urn;
		}

		/// <summary> Return the stage identifier.
		/// 
		/// </summary>
		/// <returns> the urn identifier
		/// </returns>
		virtual public System.String Key
		{
			get
			{
				return m_urn;
			}
			
		}
		
		/// <summary> Return the hashcode for the instance</summary>
		/// <returns> the instance hashcode
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_urn.GetHashCode();
			return hash;
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			if (other is StageDescriptor)
			{
				if (base.Equals(other))
				{
					return m_urn.Equals(((StageDescriptor) other).m_urn);
				}
			}
			return false;
		}
		
		/// <summary> Return a stringified representation of the instance.</summary>
		/// <returns> the string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[stage " + Key + "]";
		}
	}
}