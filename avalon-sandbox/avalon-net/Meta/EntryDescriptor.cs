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
	
	/// <summary> A descriptor that describes a value that must be placed
	/// in components Context. It contains information about;
	/// <ul>
	/// <li>key: the key that component uses to look up entry</li>
	/// <li>classname: the class/interface of the entry</li>
	/// <li>isOptional: true if entry is optional rather than required</li>
	/// </ul>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.3 $ $Date: 2004/01/31 18:59:17 $
	/// </version>
	[Serializable]
	public sealed class EntryDescriptor : Descriptor
	{
		/// <summary> The name the component uses to lookup entry.</summary>
		private System.String m_key;
		
		/// <summary> The class/interface of the Entry.</summary>
		private Type m_type;
		
		/// <summary> True if entry is optional, false otherwise.</summary>
		private bool m_optional;
		
		/// <summary> Immutable state of the entry.</summary>
		private bool m_volatile;
		
		/// <summary> An alias to a key.</summary>
		private System.String m_alias;
		
		/// <summary> Construct an non-volotile required Entry.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <param name="classname">the classname of the context entry
		/// </param>
		/// <exception cref=""> NullPointerException if the key or type value are null
		/// </exception>
		public EntryDescriptor(System.String key, Type type, System.Reflection.MemberInfo memberinfo) :
			this(key, type, false, memberinfo)
		{
		}
		
		/// <summary> Construct an non-volotile Entry.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <param name="classname">the classname of the context entry
		/// </param>
		/// <param name="optional">TRUE if this is an optional entry
		/// </param>
		/// <exception cref=""> NullPointerException if the key or type value are null
		/// </exception>
		public EntryDescriptor(System.String key, Type type, bool optional, 
			System.Reflection.MemberInfo memberinfo) : 
			this(key, type, optional, false, memberinfo)
		{
		}
		
		/// <summary> Construct an Entry.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <param name="classname">the classname of the context entry
		/// </param>
		/// <param name="optional">TRUE if this is an optional entry
		/// </param>
		/// <param name="isVolatile">TRUE if the entry is consider to be immutable
		/// </param>
		/// <exception cref=""> NullPointerException if the key or type value are null
		/// </exception>
		public EntryDescriptor(System.String key, System.Type type, bool optional, 
			bool isVolatile, System.Reflection.MemberInfo memberinfo) : 
			this(key, type, optional, isVolatile, null, memberinfo)
		{
		}
		
		/// <summary> Construct an Entry.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <param name="classname">the classname of the context entry
		/// </param>
		/// <param name="optional">TRUE if this is an optional entry
		/// </param>
		/// <param name="isVolatile">TRUE if the entry is is volatile
		/// </param>
		/// <param name="alias">an alternative key used by the component to reference the key
		/// </param>
		/// <exception cref=""> NullPointerException if the key or type value are null
		/// </exception>
		public EntryDescriptor(System.String key, System.Type type, bool optional, 
			bool isVolatile, System.String alias, System.Reflection.MemberInfo memberinfo) : 
			base(null, memberinfo)
		{
			if (null == key)
			{
				throw new System.ArgumentNullException("key");
			}
			if (null == type)
			{
				throw new System.ArgumentNullException("type");
			}
			
			m_key = key;
			m_type = type;
			m_optional = optional;
			m_volatile = isVolatile;
			m_alias = alias;
		}

		/// <summary> Return the key that Component uses to lookup entry.
		/// 
		/// </summary>
		/// <returns> the key that Component uses to lookup entry.
		/// </returns>
		public System.String Key
		{
			get
			{
				return m_key;
			}
			
		}
		/// <summary> Return the alias that Component uses to lookup the entry.
		/// If no alias is declared, the standard lookup key will be 
		/// returned.
		/// 
		/// </summary>
		/// <returns> the alias to the key.
		/// </returns>
		public System.String Alias
		{
			get
			{
				if ((System.Object) m_alias != null)
				{
					return m_alias;
				}
				else
				{
					return m_key;
				}
			}
			
		}
		/// <summary> Return the key type of value that is stored in Context.
		/// 
		/// </summary>
		/// <returns> the key type of value that is stored in Context.
		/// </returns>
		public System.Type Type
		{
			get
			{
				return m_type;
			}
			
		}
		/// <summary> Return true if entry is optional, false otherwise.
		/// 
		/// </summary>
		/// <returns> true if entry is optional, false otherwise.
		/// </returns>
		public bool Optional
		{
			get
			{
				return m_optional;
			}
			
		}
		/// <summary> Return true if entry is required, false otherwise.
		/// 
		/// </summary>
		/// <returns> true if entry is required, false otherwise.
		/// </returns>
		public bool Required
		{
			get
			{
				return !Optional;
			}
			
		}
		/// <summary> Return true if entry is volotile.
		/// 
		/// </summary>
		/// <returns> the volatile state of the entry
		/// </returns>
		public bool Volatile
		{
			get
			{
				return m_volatile;
			}
			
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <param name="other">the object to compare with this instance
		/// </param>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = other is EntryDescriptor;
			
			if (isEqual)
			{
				EntryDescriptor entry = (EntryDescriptor) other;
				
				isEqual = isEqual && m_key.Equals(entry.m_key);
				isEqual = isEqual && m_type.Equals(entry.m_type);
				isEqual = isEqual && m_optional == entry.m_optional;
				isEqual = isEqual && m_volatile == entry.m_volatile;
				if (null == (System.Object) m_alias)
				{
					isEqual = isEqual && null == (System.Object) entry.m_alias;
				}
				else
				{
					isEqual = isEqual && m_alias.Equals(entry.m_alias);
				}
			}
			
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_key.GetHashCode();
			hash ^= m_type.GetHashCode();
			hash ^= ((null != (System.Object) m_alias)?m_alias.GetHashCode():0);
			return hash;
		}
	}
}