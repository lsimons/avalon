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

namespace Apache.Avalon.Composition.Data
{
	using System;
	
	/// <summary> A context descriptor declares the context creation criteria for
	/// the context instance and context entries.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A context directive may contain multiple import statements.  Each import
	/// statement corresponds to a request for a context value from the container.</p>
	/// <pre>
	/// &lt;context class="<font color="darkred">MyContextClass</font>"&gt;
	/// &lt;entry key="<font color="darkred">special</font>"&gt;
	/// &lt;import key="<font color="darkred">urn:avalon:classloader</font>"/&gt;
	/// &lt;/entry&gt;
	/// &lt;entry key="<font color="darkred">xxx</font>"&gt;
	/// &lt;param class="<font color="darkred">MySpecialClass</font>"&gt;
	/// &lt;param&gt<font color="darkred">hello</font>&lt;/param&gt;
	/// &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">../lib</font>&lt;/param&gt;
	/// &lt;/param&gt;
	/// &lt;/entry&gt;
	/// &lt;/context&gt;
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="EntryDirective">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public class ContextDirective
	{

		/// <summary> The set of entry directives.</summary>
		private EntryDirective[] m_entries;
		
		/// <summary> The constext casting classname.</summary>
		private System.String m_classname;
		
		/// <summary> The optional provider source path.</summary>
		private System.String m_source;
		
		/// <summary> Creation of a new file target.</summary>
		/// <param name="entries">the set of entry descriptors
		/// </param>
		public ContextDirective(EntryDirective[] entries):this(null, entries)
		{
		}
		
		/// <summary> Creation of a new file target.</summary>
		/// <param name="classname">the context implementation class
		/// </param>
		/// <param name="entries">the set of entry descriptors
		/// </param>
		public ContextDirective(System.String classname, EntryDirective[] entries):this(classname, entries, null)
		{
		}
		
		/// <summary> Creation of a new file target.</summary>
		/// <param name="classname">the context implementation class
		/// </param>
		/// <param name="entries">the set of entry descriptors
		/// </param>
		/// <param name="source">a path to a source component for contextualization
		/// phase handling
		/// </param>
		public ContextDirective(System.String classname, EntryDirective[] entries, System.String source)
		{
			m_source = source;
			m_classname = classname;
			if (entries != null)
			{
				m_entries = entries;
			}
			else
			{
				m_entries = new EntryDirective[0];
			}
		}
		
		/// <summary> Return a named entry.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <returns> the entry corresponding to the supplied key or null if the
		/// key is unknown
		/// </returns>
		public virtual EntryDirective getEntryDirective(System.String key)
		{
			for (int i = 0; i < m_entries.Length; i++)
			{
				EntryDirective entry = m_entries[i];
				if (entry.Key.Equals(key))
				{
					return entry;
				}
			}
			return null;
		}

		/// <summary> Return the relative path to a source provider component that
		/// will handle a custom contextualization phase implementation.
		/// </summary>
		/// <returns> the source path
		/// </returns>
		public virtual System.String Source
		{
			get
			{
				return m_source;
			}
			
		}
		/// <summary> Return the classname of the context implementation to use.</summary>
		/// <returns> the classname
		/// </returns>
		public virtual System.String Classname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the set of entry directives.</summary>
		/// <returns> the entries
		/// </returns>
		public virtual EntryDirective[] EntryDirectives
		{
			get
			{
				return m_entries;
			}
			
		}
	}
}