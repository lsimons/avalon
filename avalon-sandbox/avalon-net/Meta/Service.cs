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
	
	/// <summary> This class contains the meta information about a particular
	/// service. It contains a set of attributes qualifying the service;
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/01/13 00:59:28 $
	/// </version>
	[Serializable]
	public class Service : Descriptor
	{
		//=========================================================================
		// state
		//=========================================================================
		
		/// <summary> The service reference.</summary>
		private ReferenceDescriptor m_reference;
		
		/// <summary> The optional context entry criteria.</summary>
		private EntryDescriptor[] m_entries;
		
		//=========================================================================
		// constructor
		//=========================================================================
		
		/// <summary> Creation of a new Service instance using a classname and
		/// supplied properties argument.
		/// 
		/// </summary>
		/// <param name="reference">the versioned classname
		/// </param>
		public Service(ReferenceDescriptor reference):this(reference, null, null)
		{
		}
		
		/// <summary> Creation of a new Service instance using a classname and
		/// supplied properties argument.
		/// 
		/// </summary>
		/// <param name="reference">the versioned classname
		/// </param>
		/// <param name="entries">the set of attributes to assign to the descriptor
		/// </param>
		public Service(ReferenceDescriptor reference, EntryDescriptor[] entries):this(reference, entries, null)
		{
		}
		
		/// <summary> Creation of a new Service instance using a classname and
		/// supplied properties argument.
		/// 
		/// </summary>
		/// <param name="reference">the versioned classname
		/// </param>
		/// <param name="attributes">the set of attributes to assign to the descriptor
		/// </param>
		public Service(ReferenceDescriptor reference, System.Collections.Specialized.NameValueCollection attributes):this(reference, null, attributes)
		{
		}
		
		/// <summary> Creation of a new Service instance using a classname and
		/// supplied properties argument.
		/// 
		/// </summary>
		/// <param name="reference">the versioned classname
		/// </param>
		/// <param name="entries">the set of optional context entries
		/// </param>
		/// <param name="attributes">the set of attributes to assign to the descriptor
		/// </param>
		public Service(ReferenceDescriptor reference, EntryDescriptor[] entries, System.Collections.Specialized.NameValueCollection attributes):base(attributes)
		{
			if (reference == null)
			{
				throw new System.NullReferenceException("reference");
			}
			m_reference = reference;
			if (entries == null)
			{
				m_entries = new EntryDescriptor[0];
			}
			else
			{
				m_entries = entries;
			}
		}
		
		//=========================================================================
		// implementation
		//=========================================================================
		
		/// <summary> Determine if supplied reference will match this service.
		/// To match a service has to have same classname and must comply with version.
		/// 
		/// </summary>
		/// <param name="reference">the reference descriptor
		/// </param>
		/// <returns> true if matches, false otherwise
		/// </returns>
		public virtual bool Matches(ReferenceDescriptor reference)
		{
			return m_reference.Matches(reference);
		}
		
		/// <summary> Return the hashcode for this service defintion.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			return m_reference.GetHashCode();
		}
		
		/// <summary> Compare this object to the supplied object for equality.</summary>
		/// <param name="other">the object to compare to this object
		/// </param>
		/// <returns> true if this object matches the supplied object
		/// in terms of service classname and version
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool match = false;
			
			if (other is ReferenceDescriptor)
			{
				match = Matches((ReferenceDescriptor) other);
			}
			else if (other is Service)
			{
				Service ref_Renamed = (Service) other;
				match = ref_Renamed.Typename.Equals(Typename) && ref_Renamed.Version.Equals(Version);
			}
			
			return match;
		}
		
		/// <summary> Returns a string representation of the service.</summary>
		/// <returns> a string representation
		/// </returns>
		public override System.String ToString()
		{
			//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
			return Reference.ToString();
		}

		/// <summary> Return the service classname key.</summary>
		/// <returns> the service classname
		/// </returns>
		virtual public System.String Typename
		{
			get
			{
				return m_reference.Typename;
			}
			
		}
		/// <summary> Return the service version.</summary>
		/// <returns> the version
		/// </returns>
		virtual public Version Version
		{
			get
			{
				return m_reference.Version;
			}
			
		}
		/// <summary> Return the service reference.</summary>
		/// <returns> the reference
		/// </returns>
		virtual public ReferenceDescriptor Reference
		{
			get
			{
				return m_reference;
			}
			
		}
		/// <summary> Return the entries declared by the service.
		/// 
		/// </summary>
		/// <returns> the entry descriptors
		/// </returns>
		virtual public EntryDescriptor[] Entries
		{
			get
			{
				return m_entries;
			}
			
		}
	}
}