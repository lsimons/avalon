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
	using Apache.Avalon.Framework;
	
	/// <summary> <p>A target is a tagged configuration fragment.  The tag is a path
	/// seperated by "/" charaters qualifying the component that the target
	/// configuration is to be applied to.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public class TargetDirective
	{
		//========================================================================
		// immutable state
		//========================================================================
		
		/// <summary> The path.</summary>
		private System.String m_path;
		
		/// <summary> The configuration.</summary>
		private IConfiguration m_config;
		
		/// <summary> The configuration.</summary>
		private CategoriesDirective m_categories;
		
		//========================================================================
		// constructors
		//========================================================================
		
		/// <summary> Create a new null Target instance.
		/// 
		/// </summary>
		/// <param name="path">target path
		/// </param>
		public TargetDirective(System.String path):this(path, null)
		{
		}
		
		/// <summary> Create a new Target instance.
		/// 
		/// </summary>
		/// <param name="path">target path
		/// </param>
		/// <param name="configuration">the configuration 
		/// </param>
		public TargetDirective(System.String path, IConfiguration configuration):this(path, configuration, null)
		{
		}
		
		/// <summary> Create a new Target instance.
		/// 
		/// </summary>
		/// <param name="path">target path
		/// </param>
		/// <param name="configuration">the configuration 
		/// </param>
		/// <param name="categories">the logging category directives 
		/// </param>
		public TargetDirective(System.String path, IConfiguration configuration, CategoriesDirective categories)
		{
			m_path = path;
			m_config = configuration;
			m_categories = categories;
		}
		
		//========================================================================
		// implementation
		//========================================================================
		
		/// <summary> Return a string representation of the target.</summary>
		/// <returns> a string representing the target instance
		/// </returns>
		public override System.String ToString()
		{
			return "[target: " + Path + ", " + (Configuration != null) + ", " + (CategoriesDirective != null) + ", " + " ]";
		}

		/// <summary> Return the target path.
		/// 
		/// </summary>
		/// <returns> the target path
		/// </returns>
		public virtual System.String Path
		{
			get
			{
				return m_path;
			}
			
		}

		/// <summary> Return the target configuration.
		/// 
		/// </summary>
		/// <returns> the target configuration
		/// </returns>
		public virtual IConfiguration Configuration
		{
			get
			{
				return m_config;
			}
			
		}

		/// <summary> Return the logging categories directive.
		/// 
		/// </summary>
		/// <returns> the logging categories (possibly null)
		/// </returns>
		public virtual CategoriesDirective CategoriesDirective
		{
			get
			{
				return m_categories;
			}
			
		}

	}
}