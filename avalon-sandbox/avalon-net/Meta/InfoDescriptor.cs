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
	
	/// <summary> This class is used to provide explicit information to assembler
	/// and administrator about the Component. It includes information
	/// such as;
	/// 
	/// <ul>
	/// <li>a symbolic name</li>
	/// <li>classname</li>
	/// <li>version</li>
	/// </ul>
	/// 
	/// <p>The InfoDescriptor also includes an arbitrary set
	/// of attributes about component. Usually these are container
	/// specific attributes that can store arbitrary information.
	/// The attributes should be stored with keys based on package
	/// name of container.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/01/19 01:19:41 $
	/// </version>
	[Serializable]
	public sealed class InfoDescriptor : Descriptor
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		/// <summary> The short name of the Component Type. Useful for displaying
		/// human readable strings describing the type in
		/// assembly tools or generators.
		/// </summary>
		private System.String m_name;
		
		/// <summary> The implementation classname.</summary>
		private System.String m_typename;
		
		/// <summary> The version of component that descriptor describes.</summary>
		private Version m_version;
		
		/// <summary> The component lifestyle.</summary>
		private Lifestyle m_lifestyle;
		
		/// <summary> The component configuration schema.</summary>
		private System.String m_schema;
		
		/// <summary> The component garbage collection policy. The value returned is either 
		/// LIBERAL, DEMOCAT or CONSERVATIVE.  A component implementing a LIBERAL policy 
		/// will be decommissioned if no references exist.  A component declaring a 
		/// DEMOCRAT policy will exist without reference so long as memory contention
		/// does not occur.  A component implementing CONSERVATIVE policies will be 
		/// maintained irrespective of usage and memory constraints so long as its 
		/// scope exists (the jvm for a "singleton" and Thread for "thread" lifestyles).  
		/// The default policy is CONSERVATIVE.
		/// </summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_collection '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private CollectionPolicy m_collection;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		/// <summary> Creation of a new info descriptor using a supplied name, key, version
		/// and attribute set.
		/// 
		/// </summary>
		/// <param name="name">the component name
		/// </param>
		/// <param name="classname">the implemetation classname
		/// </param>
		/// <param name="version">the implementation version
		/// </param>
		/// <param name="attributes">a set of attributes associated with the component type
		/// </param>
		/// <exception cref=""> IllegalArgumentException if the implementation key is not a classname
		/// @since 1.2
		/// </exception>
		public InfoDescriptor(System.String name, System.String typename, 
			Version version, Lifestyle lifestyle, CollectionPolicy collection, 
			System.String schema, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
		{
			if (null == (System.Object) typename)
			{
				throw new System.ArgumentNullException("typename");
			}
			if (null == (System.Object) name)
			{
				throw new System.ArgumentNullException("name");
			}
			
			m_name = name;
			m_collection = collection;
			m_typename = typename;
			m_version = version;
			m_schema = schema;
			m_lifestyle = lifestyle;
		}
		
		/// <summary> Return the component termination policy as a String.
		/// 
		/// </summary>
		/// <returns> the policy
		/// </returns>
		public CollectionPolicy CollectionPolicy
		{
			get
			{
				return m_collection;
			}
		}
		
		/// <summary> Return a string representation of the info descriptor.</summary>
		/// <returns> the stringified type
		/// </returns>
		public override System.String ToString()
		{
			return "[" + Name + "] " + Typename + ":" + Version;
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = base.Equals(other) && other is InfoDescriptor;
			
			if (isEqual)
			{
				InfoDescriptor info = (InfoDescriptor) other;
				isEqual = isEqual && m_typename.Equals(info.m_typename);
				isEqual = isEqual && (m_collection == info.m_collection);
				isEqual = isEqual && m_name.Equals(info.m_name);
				isEqual = isEqual && m_lifestyle.Equals(info.m_lifestyle);
				
				if (null == m_version)
				{
					isEqual = isEqual && null == info.m_version;
				}
				else
				{
					isEqual = isEqual && m_version.Equals(info.m_version);
				}
			}
			
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = base.GetHashCode();
			
			hash ^= m_typename.GetHashCode();
			
			if (null != (System.Object) m_name)
			{
				hash ^= m_name.GetHashCode();
			}
			
			if (null != (System.Object) m_lifestyle)
			{
				hash ^= m_lifestyle.GetHashCode();
			}
			
			if (null != m_version)
			{
				hash ^= m_version.GetHashCode();
			}
			
			return hash;
		}
		
		/// <summary> Return the symbolic name of component.
		/// 
		/// </summary>
		/// <returns> the symbolic name of component.
		/// </returns>
		public System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		
		/// <summary> Return the configuration schema.
		/// 
		/// </summary>
		/// <returns> the schema declaration (possibly null)
		/// </returns>
		public System.String ConfigurationSchema
		{
			get
			{
				return m_schema;
			}
			
		}
		/// <summary> Return the implementation class name for the component type.
		/// 
		/// </summary>
		/// <returns> the implementation class name
		/// </returns>
		public System.String Typename
		{
			get
			{
				return m_typename;
			}
			
		}
		/// <summary> Return the version of component.
		/// 
		/// </summary>
		/// <returns> the version of component.
		/// </returns>
		public Version Version
		{
			get
			{
				return m_version;
			}
			
		}
		/// <summary> Return the component lifestyle.
		/// 
		/// </summary>
		/// <returns> the lifestyle
		/// </returns>
		public Lifestyle Lifestyle
		{
			get
			{
				return m_lifestyle;
			}
			
		}
	}
}