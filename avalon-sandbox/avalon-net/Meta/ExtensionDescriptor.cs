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

namespace Apache.Avalon.Meta
{
	using System;
	using Apache.Avalon.Framework;
	
	/// <summary> A descriptor that describes a name and inteface of a lifecycle stage.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/01/19 01:19:41 $
	/// </version>
	[Serializable]
	public sealed class ExtensionDescriptor : Descriptor
	{
		
		/// <summary> The extension identifier.</summary>
		private System.String m_urn;
		
		/// <summary> Creation of an extension descriptor without attributes.</summary>
		/// <param name="urn">the extension identifier
		/// </param>
		/// <exception cref=""> NullPointerException if the urn identifer is null
		/// </exception>
		public ExtensionDescriptor(System.String urn):this(urn, null)
		{
		}
		
		/// <summary> Creation of a extension descriptor with attributes.</summary>
		/// <param name="urn">the extension identifier
		/// </param>
		/// <param name="attributes">a set of attributes to associate with the extension
		/// </param>
		/// <exception cref=""> NullPointerException if the supplied urn is null
		/// </exception>
		public ExtensionDescriptor(System.String urn, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			
			if (null == (System.Object) urn)
			{
				throw new System.NullReferenceException("urn");
			}
			
			m_urn = urn;
		}

		/// <summary> Return the interface reference
		/// 
		/// </summary>
		/// <returns> the reference.
		/// </returns>
		public System.String Key
		{
			get
			{
				return m_urn;
			}
			
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			if (other is ExtensionDescriptor)
			{
				if (base.Equals(other))
				{
					return m_urn.Equals(((ExtensionDescriptor) other).m_urn);
				}
			}
			return false;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_urn.GetHashCode();
			return hash;
		}
		
		/// <summary> Return a stringified representation of the instance.</summary>
		/// <returns> the string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[extension " + Key + "]";
		}
	}
}