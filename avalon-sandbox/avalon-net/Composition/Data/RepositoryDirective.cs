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
	
	/// <summary> Description of repository requests.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public sealed class RepositoryDirective
	{
		private static readonly ResourceDirective[] EMPTY_RESOURCES = new ResourceDirective[0];
		
		/// <summary> The resource references</summary>
		private ResourceDirective[] m_resources;
		
		/// <summary> Create a empty RepositoryDirective.</summary>
		public RepositoryDirective() : this(null)
		{
		}
		
		/// <summary> Create a RepositoryDirective instance.
		/// 
		/// </summary>
		/// <param name="resources">the resources to be included in a classloader
		/// </param>
		public RepositoryDirective(ResourceDirective[] resources)
		{
			if (resources == null)
			{
				m_resources = EMPTY_RESOURCES;
			}
			else
			{
				m_resources = resources;
			}
		}

		/// <summary> Return the set of resource directives.
		/// 
		/// </summary>
		/// <returns> the resource directive set
		/// </returns>
		public ResourceDirective[] Resources
		{
			get
			{
				return m_resources;
			}
			
		}
	}
}