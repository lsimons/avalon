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
	
	/// <summary> <p>A target is a tagged configuration fragment.  The tag is a path
	/// seperated by "/" charaters qualifying the component that the target
	/// configuration is to be applied to.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	public class Targets
	{
		//========================================================================
		// state
		//========================================================================
		
		/// <summary> The set of targets.</summary>
		private TargetDirective[] m_targets;
		
		//========================================================================
		// constructors
		//========================================================================
		
		/// <summary> Create an empty Targets instance.</summary>
		public Targets()
		{
			m_targets = new TargetDirective[0];
		}
		
		/// <summary> Create a new Targets instance.
		/// 
		/// </summary>
		/// <param name="targets">the set of targets
		/// </param>
		public Targets(TargetDirective[] targets)
		{
			m_targets = targets;
		}
		
		//========================================================================
		// implementation
		//========================================================================
		
		/// <summary> Return all targets.
		/// 
		/// </summary>
		/// <returns> all the targets in this targets instance.
		/// </returns>
		public virtual TargetDirective[] getTargets()
		{
			return m_targets;
		}
		
		/// <summary> Return a matching target.
		/// 
		/// </summary>
		/// <param name="path">the target path to lookup
		/// </param>
		/// <returns> the target or null if no matching target
		/// </returns>
		public virtual TargetDirective getTarget(System.String path)
		{
			System.String key = getKey(path);
			
			for (int i = 0; i < m_targets.Length; i++)
			{
				TargetDirective target = m_targets[i];
				if (target.Path.Equals(key))
				{
					return target;
				}
			}
			return null;
		}
		
		/// <summary> Return a set of targets relative to the supplied path.
		/// 
		/// </summary>
		/// <param name="path">the base path to match against
		/// </param>
		/// <returns> the set of relative targets
		/// </returns>
		public virtual Targets getTargets(System.String path)
		{
			System.String key = getKey(path);
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < m_targets.Length; i++)
			{
				TargetDirective target = m_targets[i];
				if (target.Path.StartsWith(key))
				{
					System.String name = target.Path.Substring(key.Length);
					if (name.Length > 0)
					{
						list.Add(new TargetDirective(getKey(name), target.Configuration, target.CategoriesDirective));
					}
				}
			}
			
			return new Targets( (TargetDirective[]) list.ToArray( typeof(TargetDirective[]) ) );
		}
		
		/// <summary> Convert the supplied path to a valid path.</summary>
		/// <param name="path">the path to convert
		/// </param>
		/// <returns> a good path value
		/// </returns>
		private System.String getKey(System.String path)
		{
			if (!path.StartsWith("/"))
			{
				return "/" + path;
			}
			return path;
		}
		
		/// <summary> Return a string representation of the target.</summary>
		/// <returns> a string representing the target instance
		/// </returns>
		public override System.String ToString()
		{
			System.Text.StringBuilder buffer = new System.Text.StringBuilder("[targets: ");
			for (int i = 0; i < m_targets.Length; i++)
			{
				buffer.Append(m_targets[i]);
				if (i < (m_targets.Length - 1))
				{
					buffer.Append(", ");
				}
			}
			buffer.Append(" ]");
			return buffer.ToString();
		}
	}
}