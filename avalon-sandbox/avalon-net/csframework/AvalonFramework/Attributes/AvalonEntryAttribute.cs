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
	/// The AvalonEntryAttribute Attribute declares a context entry 
	/// required by a component.
	/// </summary>
	[AttributeUsage(
		 AttributeTargets.Method|AttributeTargets.Property,
		 AllowMultiple=false,Inherited=true)]
	public class AvalonEntryAttribute : Attribute
	{
		private String m_alias;
		private String m_key;
		private bool   m_volatile = false;
		private bool   m_optinal = false;
		private Type   m_type = typeof(String);

		/// <summary>
		/// Constructor to initialize Entry info.
		/// </summary>
		/// <param name="key"></param>
		public AvalonEntryAttribute(String key)
		{
			if (key == null || key.Length == 0)
			{
				throw new ArgumentNullException("key", "Entry's key can't be null");
			}

			m_key  = key;
		}

		/// <summary>
		/// Constructor to initialize Entry info.
		/// </summary>
		/// <param name="key"></param>
		/// <param name="type"></param>
		public AvalonEntryAttribute(String key, Type type) : this(key)
		{
			if (type == null)
			{
				throw new ArgumentNullException("type", "Entry's type can't be null");
			}

			m_type = type;
		}

		/// <summary>
		/// Constructor to initialize Entry info.
		/// </summary>
		/// <param name="key"></param>
		/// <param name="type"></param>
		/// <param name="optional"></param>
		public AvalonEntryAttribute(String key, Type type, bool optional) : this(key, type)
		{
			m_optinal = optional;
		}

		/// <summary>
		/// The "official" key of the entry.
		/// </summary>
		public String Key
		{
			get
			{
				return m_key;
			}
			set
			{
				m_key = value;
			}
		}

		/// <summary>
		/// The alias that can be used by the component.
		/// </summary>
		public String Alias
		{
			get
			{
				return m_alias;
			}
			set
			{
				m_alias = value;
			}
		}

		/// <summary>
		/// The Type of the entry
		/// </summary>
		public Type EntryType
		{
			get
			{
				return m_type;
			}
			set
			{
				m_type = value;
			}
		}

		/// <summary>
		/// Is this entry optional? Defaults to false
		/// </summary>
		public bool Optional
		{
			get
			{
				return m_optinal;
			}
			set
			{
				m_optinal = value;
			}
		}

		/// <summary>
		/// Is this entry volatile? Defaults to false
		/// </summary>
		public bool Volatile
		{
			get
			{
				return m_volatile;
			}
			set
			{
				m_volatile = value;
			}
		}
	}
}
