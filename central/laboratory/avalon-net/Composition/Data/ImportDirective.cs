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
	
	/// <summary> An inport directive used within a context directive to request a container scoped values.
	/// 
	/// <p><b>XML</b></p>
	/// <p>An import statement declares that a context value must be supplied by the container,
	/// using the container scoped value of the <code>name</code> attribute, and that the value should be
	/// supplied as a context entry keyed under the value of the <code>key</code> attribute.</p>
	/// <pre>
	/// 
	/// <font color="gray">
	/// &lt;--
	/// Declare the import of the value of "urn:avalon:home" as a keyed context
	/// entry using the key "home".
	/// --&gt;</font>
	/// 
	/// <font color="gray">&lt;context&gt;</font>
	/// <font color="gray">&lt;entry key="home">&gt;</font>
	/// &lt;import key="<font color="darkred">urn:avalon:home</font>"/&gt;
	/// <font color="gray">&lt;/entry&gt;</font>
	/// <font color="gray">&lt;/context&gt;</font>
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="ContextDirective">
	/// </seealso>
	/// <seealso cref="EntryDirective">
	/// </seealso>
	/// <seealso cref="Parameter">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	public class ImportDirective:EntryDirective
	{
		/// <summary> The container scoped key.</summary>
		private System.String m_import;
		
		/// <summary> Creation of a new entry directive.</summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <param name="containerKey">the container scoped key value to import
		/// </param>
		public ImportDirective(System.String key, System.String containerKey):base(key)
		{
			if (null == (System.Object) containerKey)
			{
				throw new System.NullReferenceException("containerKey");
			}
			m_import = containerKey;
		}

		/// <summary> Return the container scoped key that defines the object to be imported.
		/// 
		/// </summary>
		/// <returns> the contain scoped key
		/// </returns>
		public virtual System.String ImportKey
		{
			get
			{
				return m_import;
			}
			
		}

	}
}