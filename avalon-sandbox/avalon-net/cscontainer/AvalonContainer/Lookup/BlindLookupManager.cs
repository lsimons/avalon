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
