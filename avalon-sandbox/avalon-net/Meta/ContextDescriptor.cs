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
	
	/// <summary> A descriptor describing the Context that the Component
	/// is passed to describe information about Runtime environment
	/// of Component. It contains information such as;
	/// <ul>
	/// <li>classname: the classname of the Context type if it
	/// differs from base Context class (ie BlockContext).</li>
	/// <li>entries: a list of entries contained in context</li>
	/// </ul>
	/// 
	/// <p>Also associated with each Context is a set of arbitrary
	/// attributes that can be used to store extra information
	/// about Context requirements.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/01/13 00:59:28 $
	/// </version>
	[Serializable]
	public class ContextDescriptor : Descriptor
	{
		//---------------------------------------------------------
		// static
		//---------------------------------------------------------
		
		/// <summary> The context entry key for accessing a component name.</summary>
		public static readonly System.String NAME_KEY = "urn:avalon:name";
		
		/// <summary> The context entry key for accessing a component partition name.</summary>
		public static readonly System.String PARTITION_KEY = "urn:avalon:partition";
		
		/// <summary> The context entry key for accessing a component home directory.</summary>
		public static readonly System.String HOME_KEY = "urn:avalon:home";
		
		/// <summary> The context entry key for accessing a component temporary directory.</summary>
		public static readonly System.String TEMP_KEY = "urn:avalon:temp";
		
		/// <summary> The context entry key for accessing a component classloader.</summary>
		public static readonly System.String CLASSLOADER_KEY = "urn:avalon:classloader";
		
		/// <summary> Context attribute key used to declare a custom contextualization
		/// interface.
		/// </summary>
		// public static readonly System.String STRATEGY_KEY = "urn:avalon:context.strategy";
		
		/// <summary> Context interface classname.</summary>
		// public const System.String AVALON_CONTEXT_CLASSNAME = "org.apache.avalon.framework.context.Context";
		
		//---------------------------------------------------------
		// immutable state
		//---------------------------------------------------------
		
		private System.String m_classname;
		
		private EntryDescriptor[] m_entries;
		
		//---------------------------------------------------------
		// constructors
		//---------------------------------------------------------
		
		/// <summary> Create a standard descriptor without attributes.</summary>
		/// <param name="entries">the set of entries required within the context
		/// </param>
		public ContextDescriptor(EntryDescriptor[] entries):this(null, entries, null)
		{
		}
		
		/// <summary> Create a descriptor without attributes.</summary>
		/// <param name="classname">the classname of a castable interface 
		/// </param>
		/// <param name="entries">the set of entries required within the context
		/// </param>
		public ContextDescriptor(System.String classname, EntryDescriptor[] entries):this(classname, entries, null)
		{
		}
		
		/// <summary> Create a descriptor.</summary>
		/// <param name="classname">the classname of a castable interface 
		/// </param>
		/// <param name="entries">the set of entries required within the context
		/// </param>
		/// <param name="attributes">supplimentary attributes associated with the context
		/// </param>
		/// <exception cref=""> NullPointerException if the entries argument is null
		/// </exception>
		public ContextDescriptor(System.String classname, EntryDescriptor[] entries, System.Collections.Specialized.NameValueCollection attributes):base(attributes)
		{
			if (null == entries)
			{
				throw new System.NullReferenceException("entries");
			}
			
			if (null == (System.Object) classname)
			{
				m_classname = AVALON_CONTEXT_CLASSNAME;
			}
			else
			{
				m_classname = classname;
			}
			m_entries = entries;
		}
		
		//---------------------------------------------------------
		// implementation
		//---------------------------------------------------------
		
		/// <summary> Return the entry with specified alias.  If the entry
		/// does not declare an alias the method will return an 
		/// entry with the matching key.
		/// 
		/// </summary>
		/// <param name="alias">the context entry key to lookup
		/// </param>
		/// <returns> the entry with specified key.
		/// </returns>
		public virtual EntryDescriptor getEntry(System.String alias)
		{
			if (null == (System.Object) alias)
			{
				throw new System.NullReferenceException("alias");
			}
			
			for (int i = 0; i < m_entries.Length; i++)
			{
				EntryDescriptor entry = m_entries[i];
				if (entry.Alias.Equals(alias))
				{
					return entry;
				}
			}
			
			for (int i = 0; i < m_entries.Length; i++)
			{
				EntryDescriptor entry = m_entries[i];
				if (entry.Key.Equals(alias))
				{
					return entry;
				}
			}
			
			return null;
		}
		
		/// <summary> Returns a set of entry descriptors resulting from a merge of the descriptors
		/// container in this descriptor with the supplied descriptors.
		/// 
		/// </summary>
		/// <param name="entries">the entries to merge
		/// </param>
		/// <returns> the mergerged set of entries
		/// </returns>
		/// <exception cref=""> IllegalArgumentException if a entry conflict occurs
		/// </exception>
		public virtual EntryDescriptor[] Merge(EntryDescriptor[] entries)
		{
			for (int i = 0; i < entries.Length; i++)
			{
				EntryDescriptor entry = entries[i];
				System.String key = entry.Key;
				EntryDescriptor local = getEntry(entry.Key);
				if (local != null)
				{
					if (!entry.Typename.Equals(local.Typename))
					{
						System.String error = "Conflicting entry type for key: " + key;
						throw new System.ArgumentException(error);
					}
				}
			}
			
			return Join(entries, Entries);
		}
		
		private EntryDescriptor[] Join(EntryDescriptor[] primary, EntryDescriptor[] secondary)
		{
			EntryDescriptor[] array = new EntryDescriptor[ primary.Length + secondary.Length ];
			primary.CopyTo(array, 0);
			secondary.CopyTo(array, primary.Length);
			return array;
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = base.Equals(other);
			if (isEqual)
				isEqual = other is ContextDescriptor;
			if (isEqual)
			{
				ContextDescriptor entity = (ContextDescriptor) other;
				isEqual = isEqual && m_classname.Equals(entity.m_classname);
				for (int i = 0; i < m_entries.Length; i++)
				{
					isEqual = isEqual && m_entries[i].Equals(entity.m_entries[i]);
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
			hash ^= m_classname.GetHashCode();
			for (int i = 0; i < m_entries.Length; i++)
			{
				hash ^= m_entries[i].GetHashCode();
			}
			return hash;
		}

		/// <summary> Return the classname of the context
		/// object interface that the supplied context argument
		/// supports under a type-safe cast.
		/// 
		/// </summary>
		/// <returns> the reference descriptor.
		/// </returns>
		virtual public System.String ContextInterfaceClassname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the local entries contained in the context.
		/// 
		/// </summary>
		/// <returns> the entries contained in the context.
		/// </returns>
		virtual public EntryDescriptor[] Entries
		{
			get
			{
				return m_entries;
			}
		}
	}
}