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

	/// <summary>	/// An enumeration used to mark a dependency as optional or not.	/// </summary>	public enum Optional	{		/// <summary>		/// Use "True" if the dependency is not required for the component		/// to run properly.		/// </summary>		True,		/// <summary>		/// Use "False" if the component will not work without the dependnecy.		/// </summary>		False	}	///<summary>	///  Attribute to mark the dependencies for a component.	///</summary>	[AttributeUsage(		 AttributeTargets.Property|AttributeTargets.Method|AttributeTargets.Class,		 AllowMultiple=true,Inherited=true)]	public sealed class AvalonDependencyAttribute : Attribute	{		private Type m_type;		private bool m_optional;		private string m_name;		///<summary>		///  Constructor to initialize the dependency's name.		///</summary>		///<param name="type">The type for the dependency</param>		///<param name="key">The dependency's lookup key</param>		///<param name="optional">Whether or not the dependency is optional</param>		///<exception cref="ArgumentException">If the "type" value is not an interface</exception>		public AvalonDependencyAttribute(Type type, string key, Optional optional)		{			if (!type.IsInterface)			{				throw new ArgumentException(					"The type passed in does not represent an interface",					"type" );			}			m_name = (null == key) ? type.Name : key;			m_optional = (optional == Optional.True);			m_type = type;		}		///<summary>		///  The lookup name of the dependency		///</summary>		public string Key		{			get			{				return m_name;			}		}		///<summary>		///  Is this dependency optional?		///</summary>		public bool IsOptional		{			get			{				return m_optional;			}		}		/// <summary>		///   The dependency type		/// </summary>		public Type DependencyType		{			get			{				return m_type;			}		}	}
}
