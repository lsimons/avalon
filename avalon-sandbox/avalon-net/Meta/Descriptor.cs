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
	using System.Reflection;

	using Apache.Avalon.Framework;
	
	/// <summary> This is the Abstract class for all feature feature descriptors.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.4 $ $Date: 2004/02/28 22:15:37 $
	/// </version>
	[Serializable]
	public abstract class Descriptor
	{
		private static readonly System.String[] EMPTY_SET = new System.String[0];

		/// <summary>
		/// The member from which this descriptor applies.
		/// </summary>
		private MemberInfo m_memberinfo;
		
		/// <summary>
		/// The arbitrary set of attributes associated with Component.
		/// </summary>
		private System.Collections.Specialized.NameValueCollection m_attributes;
		
		/// <summary> Creation of an abstract descriptor.</summary>
		/// <param name="attributes">the set of attributes to assign to the descriptor
		/// </param>
		protected internal Descriptor(System.Collections.Specialized.NameValueCollection attributes, MemberInfo memberinfo)
		{
			m_attributes = attributes;
			m_memberinfo = memberinfo;
		}
		
		/// <summary> Return the attribute for specified key.
		/// 
		/// </summary>
		/// <param name="key">the attribute key to resolve
		/// </param>
		/// <returns> the attribute for specified key.
		/// </returns>
		public virtual System.String GetAttribute(System.String key)
		{
			if (null == m_attributes)
			{
				return null;
			}
			else
			{
				return m_attributes[key];
			}
		}

		/// <summary> Returns the set of attribute names available under this descriptor.
		/// 
		/// </summary>
		/// <returns> an array of the properties names held by the descriptor.
		/// </returns>
		public virtual System.String[] AttributeNames
		{
			get
			{
				if (null == m_attributes)
				{
					return EMPTY_SET;
				}
				else
				{
					return m_attributes.AllKeys;
				}
			}
		}

		/// <summary>
		/// 
		/// </summary>
		public MemberInfo MemberInfo
		{
			get
			{
				return m_memberinfo;
			}
		}

		/// <summary> Returns the property set.
		/// TODO: check necessity for this operationi and if really needed return 
		/// a cloned equivalent (i.e. disable modification)
		/// 
		/// </summary>
		/// <returns> the property set.
		/// </returns>
		protected virtual internal System.Collections.Specialized.NameValueCollection Properties
		{
			get
			{
				return m_attributes;
			}
		}
		
		/// <summary> Return the attribute for specified key.
		/// 
		/// </summary>
		/// <param name="key">the attribute key to resolve
		/// </param>
		/// <param name="defaultValue">the default value to use if the value is not defined
		/// </param>
		/// <returns> the attribute for specified key.
		/// </returns>
		public virtual System.String GetAttribute(System.String key, System.String defaultValue)
		{
			if (null == m_attributes)
			{
				return defaultValue;
			}
			else
			{
				return (m_attributes[key] == null)?defaultValue:m_attributes[key];
			}
		}
		
		/// <summary> Compare this object with another for equality.</summary>
		/// <param name="other">the object to compare this object with
		/// </param>
		/// <returns> TRUE if the supplied object equivalent
		/// </returns>
		public override bool Equals(System.Object other)
		{
			if (other is Descriptor)
			{
				Descriptor descriptor = (Descriptor) other;
				if (null == m_attributes)
					return null == descriptor.m_attributes;
				
				// TODO: Compare keys
				return m_attributes.Count == descriptor.m_attributes.Count;
				// return m_attributes.(descriptor.m_attributes);
			}
			return false;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			if (m_attributes != null)
			{
				return m_attributes.GetHashCode();
			}
			else
			{
				return 1;
			}
		}
	}
}