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

	/// <summary> <p>An file include directive.</p>
	/// <p><b>XML</b></p>
	/// <p>An include element is normally contained within a scoping structure such as a
	/// fileset or directory set. The include element contains the single attribute name
	/// which is used to refer to the file or directory (depending on the containing
	/// context.</p>
	/// <pre>
	/// <font color="gray">&lt;fileset dir="lib"&gt;</font>
	/// &lt;include name="<font color="darkred">avalon-framework.jar</font>" /&gt;
	/// <font color="gray">&lt;/fileset&gt;</font>
	/// </pre>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public class IncludeDirective
	{

		/// <summary> The base directory</summary>
		private System.String m_path;
		
		/// <summary> Create a IncludeDirective instance.
		/// 
		/// </summary>
		/// <param name="path">the path to include
		/// </param>
		public IncludeDirective(System.String path)
		{
			m_path = path;
		}

		/// <summary> Return the included path.
		/// 
		/// </summary>
		/// <returns> the path
		/// </returns>
		public virtual System.String Path
		{
			get
			{
				return m_path;
			}
			
		}
		
	}
}