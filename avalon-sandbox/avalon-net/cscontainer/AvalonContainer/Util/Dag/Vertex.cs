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

namespace Apache.Avalon.Container.Util.Dag
{
	using System;
	using System.Collections;

	/// <summary>
	/// Summary description for Vertex.
	/// </summary>
	public sealed class Vertex : IComparable
	{
		private String         m_name;
		private ComponentEntry m_entry;
		private bool           m_seen;
		private IList          m_children;
		private int            m_order;

		public Vertex(String name, ComponentEntry entry)
		{
			m_name  = name;
			m_entry = entry;
		}

		public String Name
		{
			get
			{
				return m_name;
			}
		}

		public ComponentEntry Entry
		{
			get
			{
				return m_entry;
			}
		}

		public IList Dependencies
		{
			get
			{
				if (m_children == null)
				{
					m_children = new ArrayList();
				}
				return m_children;
			}
		}

		public int Order
		{
			get
			{
				return m_order;
			}
		}

		public void Reset()
		{
			m_order = 0;
			m_seen  = false;
		}

		public void AddDependency(Vertex vertex)
		{
			if (!Dependencies.Contains(vertex))
			{
				Dependencies.Add(vertex);
			}
		}

		public void ResolveOrder()
		{
			ResolveOrder( Name );
		}

		private int ResolveOrder( String path )
		{
			m_seen = true;

			try
			{
				int highOrder = -1;

				foreach(Vertex vertex in Dependencies)
				{
					if ( vertex.m_seen )
					{
						throw new CyclicDependencyException( path, vertex );
					}
					else
					{
						highOrder = Math.Max(
							highOrder, vertex.ResolveOrder( path + " -> " + vertex.Name ) );
					}
				}
            
				// Set this order so it is one higher than the highest dependency.
				m_order = highOrder + 1;
				return m_order;
			}
			finally
			{
				m_seen = false;
			}
		}

		#region IComparable Members

		public int CompareTo(object obj)
		{
			Vertex rhs = obj as Vertex;
			System.Diagnostics.Debug.Assert(rhs != null, "Comparisson should take place between Vertex objects.");

			int orderInd;

			if ( m_order < rhs.m_order )
			{
				orderInd = -1;
			}
			else if ( m_order > rhs.m_order )
			{
				orderInd = 1;
			}
			else
			{
				orderInd = 0;
			}

			return orderInd;
		}

		#endregion
	}
}
