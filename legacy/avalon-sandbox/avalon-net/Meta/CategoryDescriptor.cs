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
	
	/// <summary> A descriptor describing an Avalon Logger
	/// child instances that the component will create using the
	/// <code>org.apache.avalon.framework.logger.Logger#getChildLogger</code>
	/// method. The name of each category is relative to the component.  For
	/// example, a component with an internal logging category named "data"
	/// would aquire a logger for that category using the
	/// <code>m_logger.getChildLogger( "data" );</code>. The establishment
	/// of logging channels and targets for the returned channel is container
	/// concern facilities by type-level category declarations.
	/// 
	/// <p>Also associated with each Logger is a set of arbitrary
	/// attributes that can be used to store extra information
	/// about Logger requirements.</p>
	/// </summary>
	[Serializable]
	public class CategoryDescriptor : Descriptor
	{
		public const System.String SEPARATOR = ".";
		
		private System.String m_name;
		
		/// <summary> Create a descriptor for logging category.
		/// 
		/// </summary>
		/// <param name="name">the logging category name
		/// </param>
		/// <param name="attributes">a set of attributes associated with the declaration
		/// 
		/// </param>
		/// <exception cref=""> NullPointerException if name argument is null
		/// </exception>
		public CategoryDescriptor(System.String name, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			if (null == (System.Object) name)
			{
				throw new System.NullReferenceException("name");
			}
			
			m_name = name;
		}

		/// <summary> Return the name of logging category.
		/// 
		/// </summary>
		/// <returns> the category name.
		/// </returns>
		public virtual System.String Name
		{
			get
			{
				return m_name;
			}
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = other is CategoryDescriptor;
			if (isEqual)
			{
				isEqual = isEqual && m_name.Equals(((CategoryDescriptor) other).m_name);
			}
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_name.GetHashCode();
			return hash;
		}
	}
}