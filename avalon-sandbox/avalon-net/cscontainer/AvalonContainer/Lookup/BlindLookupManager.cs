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

namespace Apache.Avalon.Container.Lookup
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;

	/// <summary>
	/// A Blind <see cref="ILookupManager"/> should exposes only dependencies instances.
	/// </summary>
	public class BlindLookupManager : DefaultLookupManager
	{
		private Hashtable m_instances;

		/// <summary>
		/// Constructs a BlindLookupManager.
		/// </summary>
		public BlindLookupManager( DefaultContainer container ) : base(container)
		{
			// Shall we use a case insensitive hashtable?
			m_instances = new Hashtable();
		}

		/// <summary>
		/// Adds component instances to this BlindLookupManager. 
		/// This should be called by the Container Framework in order to
		/// expose dependencies instances to the component.
		/// </summary>
		/// <param name="role">Role name (Could be anything)</param>
		/// <param name="serviceType">Interface service type.</param>
		public void Add(String role, Type serviceType )
		{
			if ( role == null || role.Length == 0 )
			{
				throw new ArgumentNullException( "role" );
			}
			if ( serviceType == null )
			{
				throw new ArgumentNullException( "serviceType" );
			}

			// TODO: Support for Selectors

			m_instances.Add( role, serviceType );
		}

		#region ILookupManager Members

		/// <summary>
		/// Returns the component associated with the given role.
		/// </summary>
		/// <value>The component instance.</value>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		/// <exception cref="Apache.Avalon.Framework.LookupException">If the <c>role</c> could not be resolved.</exception>
		public override object this[String role]
		{
			get
			{
				if ( role == null || role.Length == 0 )
				{
					throw new ArgumentNullException( "role" );
				}

				Type service = (Type) m_instances[role];

				if ( service == null )
				{
					throw new LookupException( role );
				}

				return base[ service.FullName ];
			}
		}

		/// <summary>
		/// Releases the component associated with the given role.
		/// </summary>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public override void Release(object resource)
		{
			base.Release( resource );
		}

		/// <summary>
		/// Checks to see if a component exists for a role.
		/// </summary>
		/// <param name="role">A String identifying the lookup name to check.</param>
		/// <returns>True if the resource exists; otherwise, false.</returns>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public override bool Contains(String role)
		{
			if ( role == null || role.Length == 0 )
			{
				throw new ArgumentNullException( "role" );
			}

			bool contains = m_instances.Contains( role );

			return contains;
		}

		#endregion
	}
}
