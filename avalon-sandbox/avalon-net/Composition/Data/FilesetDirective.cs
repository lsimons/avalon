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
	
	/// <summary> <p>A fileset directive is a scoped defintion of a set of files.  A fileset
	/// a structurally defined as a base directory and a set of relative filenames
	/// represented as include directives.</p>
	/// 
	/// <p><b>XML</b></p>
	/// <pre>
	/// &lt;fileset dir="<font color="darkred">lib</font>"&gt;
	/// &lt;include name="<font color="darkred">avalon-framework.jar</font>"/&gt;
	/// &lt;include name="<font color="darkred">logkit.jar</font>"/&gt;
	/// &lt;/dirset&gt;
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="IncludeDirective">
	/// </seealso>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public class FilesetDirective
	{
		/// <summary> Return the base directory.
		/// 
		/// </summary>
		/// <returns> the directory
		/// </returns>
		public virtual System.String BaseDirectory
		{
			get
			{
				return m_base;
			}
			
		}
		/// <summary> Return the set of include directives.
		/// 
		/// </summary>
		/// <returns> the include set
		/// </returns>
		public virtual IncludeDirective[] Includes
		{
			get
			{
				return m_includes;
			}
			
		}
		/// <summary> The base directory from which include directives will be resolved.</summary>
		private System.String m_base;
		
		/// <summary> The set of include directives.</summary>
		private IncludeDirective[] m_includes;
		
		/// <summary> Create a FilesetDirective instance.
		/// 
		/// </summary>
		/// <param name="base">the base directory path against which includes are evaluated
		/// </param>
		/// <param name="includes">the set of includes to include in the fileset
		/// </param>
		public FilesetDirective(System.String baseObj, IncludeDirective[] includes)
		{
			m_base = baseObj;
			m_includes = includes;
		}
	}
}