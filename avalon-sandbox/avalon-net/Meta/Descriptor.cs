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
	
	/// <summary> This is the Abstract class for all feature feature descriptors.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/01/13 00:59:28 $
	/// </version>
	[Serializable]
	public abstract class Descriptor
	{
		private static readonly System.String[] EMPTY_SET = new System.String[0];
		
		/// <summary> The arbitrary set of attributes associated with Component.</summary>
		private System.Collections.Specialized.NameValueCollection m_attributes;
		
		/// <summary> Creation of an abstract descriptor.</summary>
		/// <param name="attributes">the set of attributes to assign to the descriptor
		/// </param>
		protected internal Descriptor(System.Collections.Specialized.NameValueCollection attributes)
		{
			m_attributes = attributes;
		}
		
		/// <summary> Return the attribute for specified key.
		/// 
		/// </summary>
		/// <param name="key">the attribute key to resolve
		/// </param>
		/// <returns> the attribute for specified key.
		/// </returns>
		public virtual System.String GetAttribute(System.String key)
		{
			if (null == m_attributes)
			{
				return null;
			}
			else
			{
				return m_attributes[key];
			}
		}

		/// <summary> Returns the set of attribute names available under this descriptor.
		/// 
		/// </summary>
		/// <returns> an array of the properties names held by the descriptor.
		/// </returns>
		virtual public System.String[] AttributeNames
		{
			get
			{
				if (null == m_attributes)
				{
					return EMPTY_SET;
				}
				else
				{
					return m_attributes.AllKeys;
				}
			}
			
		}
		/// <summary> Returns the property set.
		/// TODO: check necessity for this operationi and if really needed return 
		/// a cloned equivalent (i.e. disable modification)
		/// 
		/// </summary>
		/// <returns> the property set.
		/// </returns>
		virtual protected internal System.Collections.Specialized.NameValueCollection Properties
		{
			get
			{
				return m_attributes;
			}
			
		}
		
		/// <summary> Return the attribute for specified key.
		/// 
		/// </summary>
		/// <param name="key">the attribute key to resolve
		/// </param>
		/// <param name="defaultValue">the default value to use if the value is not defined
		/// </param>
		/// <returns> the attribute for specified key.
		/// </returns>
		public virtual System.String GetAttribute(System.String key, System.String defaultValue)
		{
			if (null == m_attributes)
			{
				return defaultValue;
			}
			else
			{
				return (m_attributes[key] == null)?defaultValue:m_attributes[key];
			}
		}
		
		/// <summary> Compare this object with another for equality.</summary>
		/// <param name="other">the object to compare this object with
		/// </param>
		/// <returns> TRUE if the supplied object equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			if (other is Descriptor)
			{
				Descriptor descriptor = (Descriptor) other;
				if (null == m_attributes)
					return null == descriptor.m_attributes;
				
				// TODO: Compare keys
				return m_attributes.Count == descriptor.m_attributes.Count;
				// return m_attributes.(descriptor.m_attributes);
			}
			return false;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			if (m_attributes != null)
			{
				return m_attributes.GetHashCode();
			}
			else
			{
				return 1;
			}
		}
	}
}