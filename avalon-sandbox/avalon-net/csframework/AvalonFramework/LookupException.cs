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
	using System.Runtime.Serialization; 

	/// <summary>
	/// The Exception thrown to indicate a problem with service.
	/// </summary>
	/// <remarks>
	/// It is usually thrown by <see cref="ILookupManager"/>
	/// </remarks> 
	[Serializable]
	public class LookupException : Exception
	{
		private const string ROLE_NAME_SERIALIZATION = "lookup-exception-role";

		private string role;		
		
		/// <summary>
		/// Constructs a new <c>LookupException</c> instance.
		/// </summary>
		public LookupException(): this(null)
		{
		}

		/// <summary>
		/// Constructs a new <c>LookupException</c> instance.
		/// </summary>
		/// <param name="message">The Detail message for this exception.</param>
		public LookupException(string message): this(null, message)
		{
		}

		/// <summary>
		/// Constructs a new <c>LookupException</c> instance.
		/// </summary>
		/// <param name="role">The Role that caused the exception.</param>
		/// <param name="message">The Detail message for this exception.</param>
		public LookupException(string role, string message): this(role, message, null)
		{
		}

		/// <summary>
		/// Constructs a new <c>LookupException</c> instance.
		/// </summary>
		/// <param name="message">The Detail message for this exception.</param>
		/// <param name="inner">The Root cause of the exception.</param>
		public LookupException(string message, Exception inner): this(null, message, inner)
		{
		}

		/// <summary>
		/// Constructs a new <c>LookupException</c> instance.
		/// </summary>
		/// <param name="role">The Role that caused the exception.</param>
		/// <param name="message">The Detail message for this exception.</param>
		/// <param name="inner">The Root cause of the exception.</param>
		public LookupException(string role, string message, Exception inner): base(message, inner)
		{
			this.role = role;
		}

		/// <summary>
		/// Constructs a new <see cref="LookupException"/> instance.
		/// </summary>
		public LookupException(SerializationInfo info, StreamingContext context): base(info, context)
		{
			info.AddValue(ROLE_NAME_SERIALIZATION, role); 
		}

		/// <summary>
		/// Gets the role that caused the exception.
		/// </summary>
		/// <value>The Role that caused the exception.</value> 
		public string Role
		{
			get
			{
				return role;
			}
		}

		/// <summary>
		/// Populates the <see cref="SerializationInfo"/> object with 
		/// the data needed to serialize the <see cref="LookupException"/> object.
		/// </summary>
		/// <param name="info"></param>
		/// <param name="context"></param>
		public override void GetObjectData(SerializationInfo info, StreamingContext context)
		{
			base.GetObjectData(info, context);

			role = info.GetString(ROLE_NAME_SERIALIZATION);
		}
	}
}
