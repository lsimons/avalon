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
	
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public class ResourceDirective
	{
		/// <summary> Group identifier.</summary>
		private System.String m_group;
		
		/// <summary> The name identifier.</summary>
		private System.String m_name;
		
		/// <summary> The type identifier.</summary>
		private System.String m_type;
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="group">the artifact group
		/// </param>
		/// <param name="name">the artifact name
		/// </param>
		public ResourceDirective(System.String group, System.String name):this(group, name, "assembly")
		{
		}
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="group">the artifact group
		/// </param>
		/// <param name="name">the artifact name
		/// </param>
		/// <param name="version">the artifact version
		/// </param>
		public ResourceDirective(System.String group, System.String name, System.String type)
		{
			if ((System.Object) group == null)
			{
				throw new System.NullReferenceException("group");
			}
			if ((System.Object) name == null)
			{
				throw new System.NullReferenceException("name");
			}
			if ((System.Object) type == null)
			{
				throw new System.NullReferenceException("type");
			}
			
			m_group = group;
			m_name = name;
			m_type = type;
		}

		/// <summary> Return the composite identifier.</summary>
		/// <returns> the identifier
		/// </returns>
		public virtual System.String Id
		{
			get
			{
				return m_group + ":" + m_name;
			}
			
		}
		/// <summary> Return the artifact name</summary>
		/// <returns> the artifact name
		/// </returns>
		public virtual System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		/// <summary> Return the group of the artifact.</summary>
		/// <returns> the artifact group
		/// </returns>
		public virtual System.String Group
		{
			get
			{
				return m_group;
			}
			
		}

		/// <summary> Return the type of the artifact.</summary>
		/// <returns> the artifact type
		/// </returns>
		public virtual System.String Type
		{
			get
			{
				return m_type;
			}
			
		}
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="id">the artifact id
		/// </param>
		/// <param name="version">the artifact version
		/// </param>
		public static ResourceDirective CreateResourceDirective(System.String id)
		{
			return CreateResourceDirective(id, "assembly");
		}
		
		/// <summary> Creation of a new resource directive.</summary>
		/// <param name="id">the artifact id
		/// </param>
		/// <param name="version">the artifact version
		/// </param>
		public static ResourceDirective CreateResourceDirective(System.String id, System.String type)
		{
			if ((System.Object) id == null)
			{
				throw new System.NullReferenceException("id");
			}
			if ((System.Object) type == null)
			{
				throw new System.NullReferenceException("type");
			}
			
			System.String group = null;
			System.String name = null;
			int n = id.IndexOf(":");
			if (id.IndexOf(":") > 0)
			{
				group = id.Substring(0, (n) - (0));
				name = id.Substring(n + 1, (id.Length) - (n + 1));
			}
			else
			{
				group = id;
				name = id;
			}
			return new ResourceDirective(group, name, type);
		}
	}
}