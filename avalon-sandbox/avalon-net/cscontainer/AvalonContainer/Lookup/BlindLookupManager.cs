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

namespace Apache.Avalon.Container.Lookup
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;

	/// <summary>
	/// A blind LookupManager should exposes only dependencies instances.
	/// </summary>
	/// <remarks>
	/// Unfortunatelly there are situations where look-up only
	/// dependents components is not enough. So a BlindLookupManager can
	/// receive a parent <see cref="Apache.Avalon.Framework.ILookupManager"/> 
	/// used to delegate the look-up.
	/// </remarks>
	public class BlindLookupManager : ILookupManager
	{
		private Hashtable      m_instances;
		private ILookupManager m_parent;

		/// <summary>
		/// Constructs a BlindLookupManager.
		/// </summary>
		/// <param name="parent">The parent 
		/// <see cref="Apache.Avalon.Framework.ILookupManager"/>. 
		/// This can be null.
		/// </param>
		public BlindLookupManager(ILookupManager parent)
		{
			// Shall we use a case insensitive hashtable?
			m_instances = new Hashtable();

			// This can be null
			m_parent = parent;
		}

		/// <summary>
		/// Adds component instances to this BlindLookupManager. 
		/// This should be called by the Container Framework in order to
		/// expose dependencies instances to the component.
		/// </summary>
		/// <param name="role">Role name (Could be anything)</param>
		/// <param name="instance">Component instance</param>
		public void Add(String role, object instance)
		{
			if ( role == null || role.Length == 0 )
			{
				throw new ArgumentNullException( "role" );
			}
			if ( instance == null )
			{
				throw new ArgumentNullException( "instance" );
			}

			// TODO: Support for Selectors

			m_instances.Add( role, instance );
		}

		#region ILookupManager Members

		/// <summary>
		/// Returns the component associated with the given role.
		/// </summary>
		/// <value>The component instance.</value>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		/// <exception cref="Apache.Avalon.Framework.LookupException">If the <c>role</c> could not be resolved.</exception>
		public object this[String role]
		{
			get
			{
				if ( role == null || role.Length == 0 )
				{
					throw new ArgumentNullException( "role" );
				}

				object instance = m_instances[role];

				if ( instance == null && m_parent != null )
				{
					instance = m_parent[role];
				}

				if ( instance == null )
				{
					throw new LookupException( role, "Instance not found." );
				}

				return instance;
			}
		}

		/// <summary>
		/// Releases the component associated with the given role.
		/// </summary>
		/// <remarks>
		/// A BlindLookupManager should not release anything as its not 
		/// have ownership of any component.
		/// </remarks>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public void Release(object resource)
		{
			// Sanity check
			if ( resource == null )
			{
				throw new ArgumentNullException( "resource" );
			}

			// TODO: We shall not release dependent components
			// but we must release components returned by the parent
			// ILookupManager. While this "to do" is not resolved,
			// Dispose probabily is not been called for a lot of 
			// components
		}

		/// <summary>
		/// Checks to see if a component exists for a role.
		/// </summary>
		/// <param name="role">A String identifying the lookup name to check.</param>
		/// <returns>True if the resource exists; otherwise, false.</returns>
		/// <exception cref="ArgumentNullException">If the <c>role</c> argument is null.</exception>
		public bool Contains(String role)
		{
			if ( role == null || role.Length == 0 )
			{
				throw new ArgumentNullException( "role" );
			}

			bool contains = m_instances.Contains( role );

			if (!contains && m_parent != null)
			{
				contains = m_parent.Contains(role);
			}

			return contains;
		}

		#endregion
	}
}
