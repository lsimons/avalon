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
	
	/// <summary> A descriptor describing an Avalon Logger
	/// child instances that the component will create using the
	/// <code>org.apache.avalon.framework.logger.Logger#getChildLogger</code>
	/// method. The name of each category is relative to the component.  For
	/// example, a component with an internal logging category named "data"
	/// would aquire a logger for that category using the
	/// <code>m_logger.getChildLogger( "data" );</code>. The establishment
	/// of logging channels and targets for the returned channel is container
	/// concern facilities by type-level category declarations.
	/// 
	/// <p>Also associated with each Logger is a set of arbitrary
	/// attributes that can be used to store extra information
	/// about Logger requirements.</p>
	/// </summary>
	[Serializable]
	public class CategoryDescriptor : Descriptor
	{
		public const System.String SEPARATOR = ".";
		
		private System.String m_name;
		
		/// <summary> Create a descriptor for logging category.
		/// 
		/// </summary>
		/// <param name="name">the logging category name
		/// </param>
		/// <param name="attributes">a set of attributes associated with the declaration
		/// 
		/// </param>
		/// <exception cref=""> NullPointerException if name argument is null
		/// </exception>
		public CategoryDescriptor(System.String name, System.Collections.Specialized.NameValueCollection attributes):base(attributes)
		{
			if (null == (System.Object) name)
			{
				throw new System.NullReferenceException("name");
			}
			
			m_name = name;
		}

		/// <summary> Return the name of logging category.
		/// 
		/// </summary>
		/// <returns> the category name.
		/// </returns>
		virtual public System.String Name
		{
			get
			{
				return m_name;
			}
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = other is CategoryDescriptor;
			if (isEqual)
			{
				isEqual = isEqual && m_name.Equals(((CategoryDescriptor) other).m_name);
			}
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			hash ^= m_name.GetHashCode();
			return hash;
		}
	}
}