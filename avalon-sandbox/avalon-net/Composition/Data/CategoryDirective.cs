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
	
	/// <summary> A logging category descriptor hierachy.  The descriptor contains a category name, a
	/// optional priority value, and an optional target.  If the priority or target values
	/// null, the resulting value will be derived from the parent category desciptor. A
	/// category descriptor may 0-n subsidiary categories.  CategoryDirective names are relative.
	/// For example, the category "orb" will appear as "my-app.orb" if the parent category
	/// name is "my-app".
	/// 
	/// <p><b>XML</b></p>
	/// <pre>
	/// &lt;categories priority="<font color="darkred">INFO</font>"&gt;
	/// &lt;category priority="<font color="darkred">DEBUG</font>"  name="<font color="darkred">loader</font>" /&gt;
	/// &lt;category priority="<font color="darkred">WARN</font>"  name="<font color="darkred">types</font>" /&gt;
	/// &lt;category priority="<font color="darkred">ERROR</font>"  name="<font color="darkred">types.builder</font>" target="<font color="darkred">default</font>"/&gt;
	/// &lt;category name="<font color="darkred">profiles</font>" /&gt;
	/// &lt;category name="<font color="darkred">lifecycle</font>" /&gt;
	/// &lt;category name="<font color="darkred">verifier</font>" /&gt;
	/// &lt;/categories&gt;
	/// </pre>
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public class CategoryDirective
	{
		/// <summary> Return the category name.
		/// 
		/// </summary>
		/// <returns> the category name
		/// </returns>
		public virtual System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		/// <summary> Return the logging priority for the category.
		/// 
		/// </summary>
		/// <returns> the logging priority for the category
		/// </returns>
		public virtual System.String Priority
		{
			get
			{
				return m_priority;
			}
			
		}
		/// <summary> Return the default log target for the category.
		/// 
		/// </summary>
		/// <returns> the default target name
		/// </returns>
		public virtual System.String Target
		{
			get
			{
				return m_target;
			}
			
		}
		
		/// <summary> Constant category priority value for debug mode.</summary>
		public const System.String DEBUG = "DEBUG";
		
		/// <summary> Constant category priority value for info mode.</summary>
		public const System.String INFO = "INFO";
		
		/// <summary> Constant category priority value for warning mode.</summary>
		public const System.String WARN = "WARN";
		
		/// <summary> Constant category priority value for error mode.</summary>
		public const System.String ERROR = "ERROR";
		
		/// <summary> The logging category name.</summary>
		private System.String m_name;
		
		/// <summary> The default logging priority.</summary>
		private System.String m_priority;
		
		/// <summary> The default logging target.</summary>
		private System.String m_target;
		
		/// <summary> Creation of a new CategoryDirective using a supplied name.
		/// 
		/// </summary>
		/// <param name="name">the category name
		/// </param>
		public CategoryDirective(System.String name):this(name, null, null)
		{
		}
		
		/// <summary> Creation of a new CategoryDirective using a supplied name and priority.
		/// 
		/// </summary>
		/// <param name="name">the category name
		/// </param>
		/// <param name="priority">the category priority - DEBUG, INFO, WARN, or ERROR
		/// </param>
		public CategoryDirective(System.String name, System.String priority):this(name, priority, null)
		{
		}
		
		/// <summary> Creation of a new CategoryDirective using a supplied name, priority, target and
		/// collection of subsidiary categories.
		/// 
		/// </summary>
		/// <param name="name">the category name
		/// </param>
		/// <param name="priority">the category priority - DEBUG, INFO, WARN, or ERROR
		/// </param>
		/// <param name="target">the name of a logging category target
		/// 
		/// </param>
		public CategoryDirective(System.String name, System.String priority, System.String target)
		{
			m_name = name;
			m_target = target;
			if ((System.Object) priority != null)
			{
				m_priority = priority.Trim().ToUpper();
			}
			else
			{
				m_priority = null;
			}
		}
		
		public  override bool Equals(System.Object other)
		{
			if (null == other)
			{
				return false;
			}
			
			if (!(other is CategoryDirective))
			{
				return false;
			}
			
			CategoryDirective test = (CategoryDirective) other;
			return (equalName(test.Name) && equalPriority(test.Priority) && equalTarget(test.Target));
		}
		
		private bool equalName(System.String other)
		{
			if ((System.Object) m_name == null)
			{
				return (System.Object) other == null;
			}
			else
			{
				return m_name.Equals(other);
			}
		}
		
		private bool equalPriority(System.String other)
		{
			if ((System.Object) m_priority == null)
			{
				return (System.Object) other == null;
			}
			else
			{
				return m_priority.Equals(other);
			}
		}
		
		private bool equalTarget(System.String other)
		{
			if ((System.Object) m_target == null)
			{
				return (System.Object) other == null;
			}
			else
			{
				return m_target.Equals(other);
			}
		}
		
		public override int GetHashCode()
		{
			int hash = m_name.GetHashCode();
			if ((System.Object) m_priority != null)
			{
				hash ^= m_priority.GetHashCode();
			}
			if ((System.Object) m_target != null)
			{
				hash ^= m_target.GetHashCode();
			}
			return hash;
		}
	}
}