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

namespace Apache.Avalon.Framework
{
	using System;

	/// <summary>
	/// A <c>ILookupManager</c> selects components based on a
	/// role. The contract is that all the components implement the
	/// differing roles and there is one component per role.
	/// <para>
	/// Roles are usually the full interface name. A role is better understood 
	/// by the analogy of a play. There are many different roles in a script.
	/// Any actor or actress can play any given part and you get 
	/// the same results (phrases said, movements made, etc.). The exact
	/// nuances of the performance is different.
	/// </para>
	/// </summary>
	public interface ILookupManager
	{
		/// <summary>
		/// Gets the resource associated with the given role.
		/// </summary>
		object this[string role] 
		{
			get; 
		}

		/// <summary>
		/// Checks to see if a component exists for a role.
		/// </summary>
		/// <param name="role">A String identifying the lookup name to check.</param>
		/// <returns>True if the resource exists; otherwise, false.</returns>
		bool Contains(string role);

		/// <summary>
		/// Return the resource when you are finished with it.
		/// This allows the <see cref="ILookupManager"/> to handle 
		/// the End-Of-Life Lifecycle events associated with the component.
		/// </summary>
		/// <remarks>
		/// Please note, that no Exceptions should be thrown at this point.
		/// This is to allow easy use of the <see cref="ILookupManager"/> system without
		/// having to trap Exceptions on a release.
		/// </remarks>
		/// <param name="resource">The resource we are releasing.</param>
		void Release(object resource);
	}
}
