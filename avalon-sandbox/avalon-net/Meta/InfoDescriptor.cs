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
	
	/// <summary> This class is used to provide explicit information to assembler
	/// and administrator about the Component. It includes information
	/// such as;
	/// 
	/// <ul>
	/// <li>a symbolic name</li>
	/// <li>classname</li>
	/// </ul>
	/// 
	/// <p>The InfoDescriptor also includes an arbitrary set
	/// of attributes about component. Usually these are container
	/// specific attributes that can store arbitrary information.
	/// The attributes should be stored with keys based on package
	/// name of container.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.3 $ $Date: 2004/01/31 18:59:17 $
	/// </version>
	[Serializable]
	public sealed class InfoDescriptor : Descriptor
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		/// <summary> The short name of the Component Type. Useful for displaying
		/// human readable strings describing the type in
		/// assembly tools or generators.
		/// </summary>
		private System.String m_name;
		
		/// <summary> The implementation classname.</summary>
		private System.Type m_type;
		
		/// <summary> The component lifestyle.</summary>
		private Lifestyle m_lifestyle;
		
		/// <summary> The component configuration schema.</summary>
		private System.String m_schema;
		
		/// <summary> The component garbage collection policy. The value returned is either 
		/// LIBERAL, DEMOCAT or CONSERVATIVE.  A component implementing a LIBERAL policy 
		/// will be decommissioned if no references exist.  A component declaring a 
		/// DEMOCRAT policy will exist without reference so long as memory contention
		/// does not occur.  A component implementing CONSERVATIVE policies will be 
		/// maintained irrespective of usage and memory constraints so long as its 
		/// scope exists (the jvm for a "singleton" and Thread for "thread" lifestyles).  
		/// The default policy is CONSERVATIVE.
		/// </summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_collection '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private CollectionPolicy m_collection;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		/// <summary> Creation of a new info descriptor using a supplied name, key
		/// and attribute set.
		/// 
		/// </summary>
		/// <param name="name">the component name
		/// </param>
		/// <param name="classname">the implemetation classname
		/// </param>
		/// <param name="attributes">a set of attributes associated with the component type
		/// </param>
		/// <exception cref=""> IllegalArgumentException if the implementation key is not a classname
		/// @since 1.2
		/// </exception>
		public InfoDescriptor(System.String name, 
			System.Type type, Lifestyle lifestyle, 
			CollectionPolicy collection, System.String schema, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			if (null == (System.Object) type)
			{
				throw new System.ArgumentNullException("type");
			}
			if (null == (System.Object) name)
			{
				throw new System.ArgumentNullException("name");
			}
			
			m_name = name;
			m_collection = collection;
			m_type = type;
			m_schema = schema;
			m_lifestyle = lifestyle;
		}
		
		/// <summary> Return the component termination policy as a String.
		/// 
		/// </summary>
		/// <returns> the policy
		/// </returns>
		public CollectionPolicy CollectionPolicy
		{
			get
			{
				return m_collection;
			}
		}
		
		/// <summary> Return a string representation of the info descriptor.</summary>
		/// <returns> the stringified type
		/// </returns>
		public override System.String ToString()
		{
			return "[" + Name + "] " + Type;
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = base.Equals(other) && other is InfoDescriptor;
			
			if (isEqual)
			{
				InfoDescriptor info = (InfoDescriptor) other;
				isEqual = isEqual && m_type.Equals(info.m_type);
				isEqual = isEqual && (m_collection == info.m_collection);
				isEqual = isEqual && m_name.Equals(info.m_name);
				isEqual = isEqual && m_lifestyle.Equals(info.m_lifestyle);
			}
			
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			
			hash ^= m_type.GetHashCode();
			
			if (null != (System.Object) m_name)
			{
				hash ^= m_name.GetHashCode();
			}
			
			if (null != (System.Object) m_lifestyle)
			{
				hash ^= m_lifestyle.GetHashCode();
			}
			
			return hash;
		}
		
		/// <summary> Return the symbolic name of component.
		/// 
		/// </summary>
		/// <returns> the symbolic name of component.
		/// </returns>
		public System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		
		/// <summary> Return the configuration schema.
		/// 
		/// </summary>
		/// <returns> the schema declaration (possibly null)
		/// </returns>
		public System.String ConfigurationSchema
		{
			get
			{
				return m_schema;
			}
			
		}
		/// <summary> Return the implementation class name for the component type.
		/// 
		/// </summary>
		/// <returns> the implementation class name
		/// </returns>
		public System.Type Type
		{
			get
			{
				return m_type;
			}
			
		}

		/// <summary> Return the component lifestyle.
		/// 
		/// </summary>
		/// <returns> the lifestyle
		/// </returns>
		public Lifestyle Lifestyle
		{
			get
			{
				return m_lifestyle;
			}
			
		}
	}
}