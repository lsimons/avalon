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
	
	/// <summary> A DependencyDirective contains information describing how a 
	/// dependency should be resolved.  
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
	/// </author>
	/// <version>  CVS $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public sealed class DependencyDirective
	{
		/// <summary> Return the dependency key.</summary>
		/// <returns> the key
		/// </returns>
		public System.String Key
		{
			get
			{
				return m_key;
			}
			
		}
		/// <summary> Return the dependency source path.</summary>
		/// <returns> the path
		/// </returns>
		public System.String Source
		{
			get
			{
				return m_source;
			}
			
		}
		/// <summary> Return the set of selection directive constraints.</summary>
		/// <returns> the selection directive set
		/// </returns>
		public SelectionDirective[] SelectionDirectives
		{
			get
			{
				return m_features;
			}
			
		}
		
		/// <summary> The dependency key that the directive refers to.</summary>
		private System.String m_key;
		
		/// <summary> The dependency source (possibly null)</summary>
		private System.String m_source;
		
		/// <summary> The set of features used during selection.</summary>
		private SelectionDirective[] m_features;
		
		/// <summary> Creation of a new dependency directive.
		/// 
		/// </summary>
		/// <param name="key">the dependency key
		/// </param>
		/// <param name="source">path to the source provider component
		/// </param>
		public DependencyDirective(System.String key, System.String source)
		{
			m_key = key;
			m_source = source;
			m_features = new SelectionDirective[0];
		}
		
		/// <summary> Creation of a new dependency directive.
		/// 
		/// </summary>
		/// <param name="key">the dependency key
		/// </param>
		/// <param name="features">the set of selection directives
		/// </param>
		public DependencyDirective(System.String key, SelectionDirective[] features)
		{
			m_key = key;
			m_features = features;
			m_source = null;
		}
	}
}