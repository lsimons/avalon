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
	/// <version>  $Revision: 1.1 $ $Date: 2004/01/13 00:59:28 $
	/// </version>
	[Serializable]
	public sealed class EntryDescriptor
	{
		
		/// <summary> The name the component uses to lookup entry.</summary>
		private System.String m_key;
		
		/// <summary> The class/interface of the Entry.</summary>
		private System.String m_classname;
		
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
		public EntryDescriptor(System.String key, System.String typename):this(key, typename, false)
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
		public EntryDescriptor(System.String key, System.String typename, bool optional):this(key, typename, optional, false)
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
		public EntryDescriptor(System.String key, System.String typename, bool optional, bool isVolatile):this(key, typename, optional, isVolatile, null)
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
		public EntryDescriptor(System.String key, System.String typename, bool optional, bool isVolatile, System.String alias)
		{
			if (null == key)
			{
				throw new System.NullReferenceException("key");
			}
			if (null == typename)
			{
				throw new System.NullReferenceException("typename");
			}
			
			m_key = key;
			m_classname = typename;
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
		public System.String Typename
		{
			get
			{
				return m_classname;
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
				isEqual = isEqual && m_classname.Equals(entry.m_classname);
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
			hash ^= m_classname.GetHashCode();
			hash ^= ((null != (System.Object) m_alias)?m_alias.GetHashCode():0);
			return hash;
		}
	}
}