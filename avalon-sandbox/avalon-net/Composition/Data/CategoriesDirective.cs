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
	
	
	/// <summary> Description of a set of categories.
	/// 
	/// </summary>
	/// <seealso cref="CategoryDirective">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public class CategoriesDirective : CategoryDirective
	{
		/// <summary> Return the set of logging categories.
		/// 
		/// </summary>
		/// <returns> the set of category declarations
		/// </returns>
		public virtual CategoryDirective[] Categories
		{
			get
			{
				return m_categories;
			}
			
		}
		/// <summary> The root category hierachy.</summary>
		private CategoryDirective[] m_categories;
		
		/// <summary> Create a CategoriesDirective instance.</summary>
		public CategoriesDirective():this("")
		{
		}
		
		/// <summary> Create a CategoriesDirective instance.
		/// 
		/// </summary>
		/// <param name="name">the base category name
		/// </param>
		public CategoriesDirective(System.String name):this(name, null, null, new CategoryDirective[0])
		{
		}
		
		/// <summary> Create a CategoriesDirective instance.
		/// 
		/// </summary>
		/// <param name="categories">the categories to include in the directive
		/// </param>
		public CategoriesDirective(CategoryDirective[] categories):this("", null, null, categories)
		{
		}
		
		
		/// <summary> Create a CategoriesDirective instance.
		/// 
		/// </summary>
		/// <param name="name">the base category name
		/// </param>
		/// <param name="priority">the default logging priority
		/// </param>
		/// <param name="target">the default logging target
		/// </param>
		/// <param name="categories">the logging category descriptors
		/// </param>
		public CategoriesDirective(System.String name, System.String priority, System.String target, CategoryDirective[] categories):base(name, priority, target)
		{
			if (categories == null)
			{
				m_categories = new CategoryDirective[0];
			}
			else
			{
				m_categories = categories;
			}
		}
		
		/// <summary> Return a named category.
		/// 
		/// </summary>
		/// <param name="name">the category name
		/// </param>
		/// <returns> the category declaration
		/// </returns>
		public virtual CategoryDirective getCategoryDirective(System.String name)
		{
			for (int i = 0; i < m_categories.Length; i++)
			{
				CategoryDirective category = m_categories[i];
				if (category.Name.ToUpper().Equals(name.ToUpper()))
				{
					return category;
				}
			}
			return null;
		}
		
		/// <summary> Test this object for equality with the suppplied object.
		/// 
		/// </summary>
		/// <returns> TRUE if this object equals the supplied object
		/// else FALSE
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = other is CategoriesDirective;
			if (isEqual)
				isEqual = base.Equals(other);
			
			if (isEqual)
			{
				CategoriesDirective cat = (CategoriesDirective) other;
				if (isEqual)
					isEqual = m_categories.Length == cat.m_categories.Length;
				if (isEqual)
				{
					for (int i = 0; i < m_categories.Length && isEqual; i++)
					{
						isEqual = m_categories[i].Equals(cat.m_categories[i]);
					}
				}
			}
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the cashcode
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			for (int i = 0; i < m_categories.Length; i++)
			{
				hash ^= m_categories[i].GetHashCode();
			}
			return hash;
		}
	}
}