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
	
	/// <summary> A DependencyDirective contains information describing how a 
	/// depedency should be resolved.  
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
	/// </author>
	/// <version>  CVS $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public sealed class SelectionDirective
	{
		public const System.String EXISTS = "exists";
		public const System.String EQUALS = "equals";
		public const System.String INCLUDES = "includes";
		
		/// <summary> The feature name.</summary>
		private System.String m_feature;
		
		/// <summary> The value attributed to the feature selection criteria.</summary>
		private System.String m_value;
		
		/// <summary> The criteria to be applied with respect to the feature criteria.</summary>
		private System.String m_criteria;
		
		/// <summary> The optional status of the selection directive.</summary>
		private bool m_optional;
		
		/// <summary> Creation of a new dependency directive.
		/// 
		/// </summary>
		/// <param name="feature">the selection feature
		/// </param>
		/// <param name="value">the value to asses
		/// </param>
		/// <param name="criteria">the selection criteria
		/// </param>
		/// <param name="optional">the optional status
		/// </param>
		public SelectionDirective(System.String feature, System.String valueObj, System.String criteria, bool optional)
		{
			m_feature = feature;
			m_value = valueObj;
			m_criteria = criteria;
			m_optional = optional;
		}

		/// <summary> Return the feature name.</summary>
		/// <returns> the name
		/// </returns>
		public System.String Feature
		{
			get
			{
				return m_feature;
			}
			
		}
		/// <summary> Return the feature value.</summary>
		/// <returns> the name
		/// </returns>
		public System.String Value
		{
			get
			{
				return m_value;
			}
			
		}
		/// <summary> Return the feature selection criteria.</summary>
		/// <returns> the criteria
		/// </returns>
		public System.String Criteria
		{
			get
			{
				return m_criteria;
			}
			
		}
		/// <summary> Return the required status of this directive.</summary>
		/// <returns> the required status
		/// </returns>
		public bool Required
		{
			get
			{
				return !m_optional;
			}
			
		}
		/// <summary> Return the optional status of this directive. This 
		/// is equivalent to !isRequired()
		/// </summary>
		/// <returns> the optional status
		/// </returns>
		public bool Optional
		{
			get
			{
				return m_optional;
			}
			
		}

	}
}