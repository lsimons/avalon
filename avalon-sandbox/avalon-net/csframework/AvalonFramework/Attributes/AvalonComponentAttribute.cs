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

	/// <summary>	/// Enumeration used to mark the component's lifestyle.	/// </summary>	public enum Lifestyle	{		/// <summary>		/// Singleton components are instantiated once, and shared		/// between all clients.		/// </summary>		Singleton,		/// <summary>		/// Thread components have a unique instance per thread.		/// </summary>		Thread,		/// <summary>		/// Pooled components have a unique instance per client,		/// but they are managed in a pool.		/// </summary>		Pooled,		/// <summary>		/// Transient components are created on demand.		/// </summary>		Transient,		/// <summary>		/// Custom lifestyle components should be managed by custom component factories.		/// </summary>		Custom	}	///<summary>	///  Attribute used to mark a component as an Avalon component.	///</summary>	[AttributeUsage(AttributeTargets.Class,AllowMultiple=false,Inherited=false)]	public sealed class AvalonComponentAttribute : Attribute	{		private Lifestyle m_lifestyle;		private string m_name;		/// <summary>		/// Marks a class as a component, providing a configuration name and preferred lifestyle		/// </summary>		/// <param name="name">The component logical name (may be used for configuration elements)</param>		/// <param name="lifestyle">The lifestyle used for the component</param>		public AvalonComponentAttribute( string name, Lifestyle lifestyle )		{			m_lifestyle = lifestyle;			m_name      = name;		}		/// <summary>		/// The component name assigned to this component.		/// </summary>		public string Name		{			get			{				return m_name;			}		}		/// <summary>		/// The lifestyle associated with the component		/// </summary>		public Lifestyle Lifestyle		{			get			{				return m_lifestyle;			}		}	}
}
