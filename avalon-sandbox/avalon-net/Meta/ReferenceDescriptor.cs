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
	
	/// <summary> This reference defines the type of interface required
	/// by a component. The type corresponds to the class name of the
	/// interface implemented by component. Associated with each
	/// classname is a version object so that different versions of same
	/// interface can be represented.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/01/13 00:59:28 $
	/// </version>
	[Serializable]
	public sealed class ReferenceDescriptor
	{
		
		/// <summary> The name of service class.</summary>
		private System.String m_typename;
		
		/// <summary> The version of service class.</summary>
		private Version m_version;
		
		/// <summary> Construct a service with specified type.
		/// 
		/// </summary>
		/// <param name="type">the service type spec
		/// </param>
		/// <exception cref=""> NullPointerException if the classname is null
		/// </exception>
		public ReferenceDescriptor(System.String type) : this(ParseClassname(type), ParseVersion(type))
		{
		}
		
		/// <summary> Construct a service with specified name, version and attributes.
		/// 
		/// </summary>
		/// <param name="classname">the name of the service
		/// </param>
		/// <param name="version">the version of service
		/// </param>
		/// <exception cref=""> NullPointerException if the classname or version is null
		/// </exception>
		/// <exception cref=""> IllegalArgumentException if the classname string is invalid
		/// </exception>
		public ReferenceDescriptor(System.String typename, Version version)
		{
			if (null == (System.Object) typename)
			{
				throw new System.NullReferenceException("typename");
			}
			if (typename.Equals(String.Empty))
			{
				throw new System.ArgumentException("typename");
			}
			if (typename.IndexOf("/") > - 1)
			{
				throw new System.ArgumentException("typename");
			}
			
			m_typename = typename;
			
			if (null == version)
			{
				m_version = new Version();
			}
			else
			{
				m_version = version;
			}
		}

		/// <summary> 
		/// Return classname of interface this reference refers to.
		/// </summary>
		/// <returns> the classname of the Service
		/// </returns>
		public System.String Typename
		{
			get
			{
				return m_typename;
			}
			
		}

		/// <summary> Return the version of interface.
		/// 
		/// </summary>
		/// <returns> the version of interface
		/// </returns>
		public Version Version
		{
			get
			{
				return m_version;
			}
			
		}
		
		/// <summary> Determine if specified service will match this service.
		/// To match a service has to have same name and must comply with version.
		/// 
		/// </summary>
		/// <param name="other">the other ServiceInfo
		/// </param>
		/// <returns> true if matches, false otherwise
		/// </returns>
		public bool Matches(ReferenceDescriptor other)
		{
			return m_typename.Equals(other.m_typename) && other.Version.Equals(Version);
		}
		
		/// <summary> Convert to a string of format name:version
		/// 
		/// </summary>
		/// <returns> string describing service
		/// </returns>
		public override System.String ToString()
		{
			return Typename + ":" + Version;
		}
		
		/// <summary> Compare this object with another for equality.</summary>
		/// <param name="other">the object to compare this object with
		/// </param>
		/// <returns> TRUE if the supplied object is a reference, service, or service
		/// descriptor that matches this objct in terms of classname and version
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool match = false;
			
			//
			// TODO: check validity of the following - this is 
			// assuming the equality is equivalent to compliance
			// which is not true
			//
			
			if (other is ReferenceDescriptor)
			{
				match = ((ReferenceDescriptor) other).Matches(this);
			}
			else if (other is Service)
			{
				match = ((Service) other).Matches(this);
			}
			else if (other is ServiceDescriptor)
			{
				match = ((ServiceDescriptor) other).Reference.Matches(this);
			}
			
			return match;
		}
		
		/// <summary> Returns the cashcode.</summary>
		/// <returns> the hascode value
		/// </returns>
		public override int GetHashCode()
		{
			return Typename.GetHashCode() ^ Version.GetHashCode();
		}
		
		private static System.String ParseClassname(System.String type)
		{
			if ((System.Object) type == null)
				throw new System.NullReferenceException("type");
			
			int index = type.IndexOf(":");
			if (index == - 1)
			{
				return type;
			}
			else
			{
				return type.Substring(0, (index) - (0));
			}
		}
		
		private static Version ParseVersion(System.String type)
		{
			if (type.IndexOf(":") == - 1)
			{
				return new Version();
			}
			else
			{
				return new Version( type.Substring( GetColonIndex(type)+1 ) ) ;
			}
		}
		
		private static int GetColonIndex(System.String type)
		{
			if (null == (System.Object) type)
				throw new System.NullReferenceException("type");
			return System.Math.Min(type.Length, System.Math.Max(0, type.IndexOf(":")));
		}
	}
}