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

namespace Apache.Avalon.Castle.ManagementExtensions
{
	using System;

	/// <summary>
	/// Summary description for MServer.
	/// </summary>
	public interface MServer
	{
		/// <summary>
		/// Instantiates the specified type using the server domain.
		/// </summary>
		/// <param name="typeName"></param>
		/// <returns></returns>
		Object Instantiate(String typeName);

		/// <summary>
		/// Instantiates the specified type using the server domain.
		/// </summary>
		/// <param name="typeName"></param>
		/// <param name="typeName"></param>
		/// <returns></returns>
		Object Instantiate(String assemblyName, String typeName);

		/// <summary>
		/// Instantiates the specified managed object.
		/// </summary>
		/// <param name="typeName"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		ManagedInstance CreateManagedObject(String typeName, ManagedObjectName name);

		/// <summary>
		/// Instantiates the specified managed object.
		/// </summary>
		/// <param name="typeName"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		ManagedInstance CreateManagedObject(String assemblyName, String typeName, ManagedObjectName name);

		/// <summary>
		/// Registers the specified managed object instance.
		/// </summary>
		/// <param name="instance"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		ManagedInstance RegisterManagedObject(Object instance, ManagedObjectName name);
		
		/// <summary>
		/// Returns a <see cref="ManagedInstance"/> representing 
		/// a managed object instance.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		ManagedInstance GetManagedInstance(ManagedObjectName name);

		/// <summary>
		/// Unregister a managed object from the domain.
		/// </summary>
		/// <param name="name"></param>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		void UnregisterManagedObject(ManagedObjectName name);

		/// <summary>
		/// Invokes an action in managed object
		/// </summary>
		/// <param name="name"></param>
		/// <param name="action"></param>
		/// <param name="args"></param>
		/// <param name="signature"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		Object Invoke(ManagedObjectName name, String action, Object[] args, Type[] signature);

		/// <summary>
		/// Returns the info (attributes and operations) about the specified object.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		ManagementInfo GetManagementInfo(ManagedObjectName name);

		/// <summary>
		/// Gets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <returns></returns>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		Object GetAttribute(ManagedObjectName name, String attributeName);

		/// <summary>
		/// Sets an attribute value of the specified managed object.
		/// </summary>
		/// <param name="name"></param>
		/// <param name="attributeName"></param>
		/// <param name="attributeValue"></param>
		/// <exception cref="InvalidDomainException">If domain name is not found.</exception>
		void SetAttribute(ManagedObjectName name, String attributeName, Object attributeValue);
	}
}
