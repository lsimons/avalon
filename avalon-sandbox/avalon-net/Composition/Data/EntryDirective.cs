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

namespace Apache.Avalon.Composition.Data
{
	using System;

	/// <summary> A entry descriptor declares the context entry import or creation criteria for
	/// a single context entry instance.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A entry may contain either (a) a single nested import directive, or (b) a single param constructor directives.</p>
	/// <pre>
	/// <font color="gray">&lt;context&gt;</font>
	/// 
	/// &lt!-- option (a) nested import -->
	/// &lt;entry key="<font color="darkred">my-home-dir</font>"&gt;
	/// &lt;include key="<font color="darkred">urn:avalon:home</font>"/&gt;
	/// &lt;/entry&gt;
	/// 
	/// &lt!-- option (b) param constructors -->
	/// &lt;entry key="<font color="darkred">title</font>"&gt;
	/// &lt;param&gt;<font color="darkred">Lord of the Rings</font>&lt;/&gt;
	/// &lt;/entry&gt;
	/// &lt;entry key="<font color="darkred">home</font>"&gt;
	/// &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">../home</font>&lt;/param&gt;
	/// &lt;/entry&gt;
	/// 
	/// <font color="gray">&lt;/context&gt;</font>
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="ImportDirective">
	/// </seealso>
	/// <seealso cref="Parameter">
	/// </seealso>
	/// <seealso cref="ContextDirective">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public abstract class EntryDirective
	{
		/// <summary> Return the context key.</summary>
		/// <returns> the key
		/// </returns>
		public virtual System.String Key
		{
			get
			{
				return m_key;
			}
			
		}
		/// <summary> The entry key.</summary>
		private System.String m_key;
		
		/// <summary> Creation of a new entry directive using a import directive.</summary>
		/// <param name="key">the entry key
		/// </param>
		public EntryDirective(System.String key)
		{
			if (null == (System.Object) key)
			{
				throw new System.NullReferenceException("key");
			}
			m_key = key;
		}
	}
}