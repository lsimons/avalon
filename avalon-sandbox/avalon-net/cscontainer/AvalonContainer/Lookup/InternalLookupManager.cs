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

namespace Apache.Avalon.Container.Lookup
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for InternalLookupManager.
	/// </summary>
	internal class InternalLookupManager : ILookupManager
	{
		private Hashtable m_instances;

		public InternalLookupManager()
		{
			m_instances = new Hashtable();
		}

		/// <summary>
		/// Adds component instances to this BlindLookupManager. 
		/// This should be called by the Container Framework in order to
		/// expose dependencies instances to the component.
		/// </summary>
		/// <param name="role">Role name (Could be anything)</param>
		/// <param name="instance">Component instance</param>
		public void Add(String role, object instance )
		{
			if ( role == null || role.Length == 0 )
			{
				throw new ArgumentNullException( "role" );
			}
			if ( instance == null )
			{
				throw new ArgumentNullException( "instance" );
			}

			m_instances.Add( role, instance );
		}

		#region ILookupManager Members

		/// <summary>
		/// Returns the component associated with the given role.
		/// </summary>
		/// <value>The component instance.</value>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		/// <exception cref="Apache.Avalon.Framework.LookupException">If the <c>role</c> could not be resolved.</exception>
		public object this[string role]
		{
			get
			{
				if ( role == null || role.Length == 0 )
				{
					throw new ArgumentNullException("role", "You can't look up using an empty role.");
				}

				object instance = m_instances[ role ];

				if ( instance == null )
				{
					throw new LookupException( role, "Unknown component." );
				}

				return instance;
			}
		}

		/// <summary>
		/// Releases the component associated with the given role.
		/// </summary>
		/// <remarks>
		/// A InternalLookupManager should not release anything as its not 
		/// have ownership of any component.
		/// </remarks>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public void Release(object resource)
		{
			
		}

		/// <summary>
		/// Checks to see if a component exists for a role.
		/// </summary>
		/// <param name="role">A String identifying the lookup name to check.</param>
		/// <returns>True if the resource exists; otherwise, false.</returns>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public bool Contains(string role)
		{
			return m_instances.Contains( role );
		}

		#endregion
	}
}
