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
	
	/// <summary> A block reference directive contains an identifier and verion of 
	/// a local resource to be included by reference into 
	/// a container.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@avalon.apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	public class BlockCompositionDirective : ComponentProfile
	{
		/// <summary> The version identifier.</summary>
		private ResourceDirective m_resource;
		
		/// <summary> Nested targets.</summary>
		private TargetDirective[] m_targets;
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="name">the name to assign to the container 
		/// established by the composition directive
		/// </param>
		/// <param name="resource">a resource reference from which a block 
		/// description can be resolved
		/// </param>
		public BlockCompositionDirective(System.String name, ResourceDirective resource):this(name, resource, new TargetDirective[0])
		{
		}
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="name">the name to assign to the container 
		/// established by the composition directive
		/// </param>
		/// <param name="resource">a resource reference from which a block 
		/// description can be resolved
		/// </param>
		public BlockCompositionDirective(System.String name, ResourceDirective resource, TargetDirective[] targets) : 
			base(name, resource.Type)
		{
			if (resource == null)
			{
				throw new System.NullReferenceException("resource");
			}
			m_resource = resource;
			m_targets = targets;
		}

		/// <summary> Return the resource reference.</summary>
		/// <returns> the resource
		/// </returns>
		public virtual ResourceDirective Resource
		{
			get
			{
				return m_resource;
			}
			
		}
		/// <summary> Return the relative targets.</summary>
		/// <returns> the targets
		/// </returns>
		public virtual TargetDirective[] TargetDirectives
		{
			get
			{
				return m_targets;
			}
			
		}
	}
}