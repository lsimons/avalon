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
	
	/// <summary> A block include directive that references a source file describing a block.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@avalon.apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	public class BlockIncludeDirective : ComponentProfile
	{
		/// <summary> The include path.</summary>
		private System.String m_path;
		
		/// <summary> Creation of a new entry directive.</summary>
		/// <param name="name">the name to assign to the included container
		/// </param>
		/// <param name="path">a relative path to the block descriptor
		/// </param>
		public BlockIncludeDirective(System.String name, System.String path)
			: base(name, name)
		{
			if ((System.Object) path == null)
			{
				throw new System.NullReferenceException("path");
			}
			m_path = path;
		}

		/// <summary> Return the containment include path.
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