// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
//
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
//
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
//
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
//    must not be used to endorse or promote products derived from this  software
//    without  prior written permission. For written permission, please contact
//    apache@apache.org.
//
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
//
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

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
