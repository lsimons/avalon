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
	/// <version>  $Revision: 1.4 $ $Date: 2004/02/28 22:15:37 $
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
		// public static readonly System.String CLASSLOADER_KEY = "urn:avalon:classloader";
		
		/// <summary>
		/// Context attribute key used to declare a custom contextualization interface.
		/// </summary>
		public static readonly String STRATEGY_KEY = "urn:avalon:context.strategy";

		/// <summary> Context interface classname.</summary>
		public static readonly System.String AVALON_CONTEXT_CLASSNAME = "Apache.Avalon.Framework.IContext";
		
		//---------------------------------------------------------
		// immutable state
		//---------------------------------------------------------
		
		private System.Type m_type;
		
		private EntryDescriptor[] m_entries;
		
		//---------------------------------------------------------
		// constructors
		//---------------------------------------------------------
		
		/// <summary> Create a standard descriptor without attributes.</summary>
		/// <param name="entries">the set of entries required within the context
		/// </param>
		public ContextDescriptor(EntryDescriptor[] entries) : this(null, entries, null)
		{
		}
		
		/// <summary> Create a descriptor without attributes.</summary>
		/// <param name="classname">the classname of a castable interface 
		/// </param>
		/// <param name="entries">the set of entries required within the context
		/// </param>
		public ContextDescriptor(Type contextType, EntryDescriptor[] entries) : this(contextType, entries, null)
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
		public ContextDescriptor(Type contextType, 
			EntryDescriptor[] entries, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			if (null == entries)
			{
				throw new System.NullReferenceException("entries");
			}
			
			if (contextType == null)
			{
				m_type = typeof(Apache.Avalon.Framework.DefaultContext);
			}
			else
			{
				m_type = contextType;
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
		public virtual EntryDescriptor GetEntry(System.String alias)
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
				EntryDescriptor local = GetEntry(entry.Key);
				if (local != null)
				{
					if (!entry.Type.Equals(local.Type))
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
				isEqual = isEqual && m_type.Equals(entity.m_type);
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
			hash ^= m_type.GetHashCode();
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
		public virtual Type ContextInterface
		{
			get
			{
				return m_type;
			}
			
		}
		/// <summary> Return the local entries contained in the context.
		/// 
		/// </summary>
		/// <returns> the entries contained in the context.
		/// </returns>
		public virtual EntryDescriptor[] Entries
		{
			get
			{
				return m_entries;
			}
		}
	}
}