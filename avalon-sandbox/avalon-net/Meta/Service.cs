// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
	/// <version>  $Revision: 1.4 $ $Date: 2004/02/28 22:15:37 $
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
		public Service(ReferenceDescriptor reference) : 
			this(reference, null, null)
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
		public Service(ReferenceDescriptor reference, EntryDescriptor[] entries) : 
			this(reference, entries, null)
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
		public Service(ReferenceDescriptor reference, System.Collections.Specialized.NameValueCollection attributes) : 
			this(reference, null, attributes)
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
		public Service(ReferenceDescriptor reference, EntryDescriptor[] entries, 
			System.Collections.Specialized.NameValueCollection attributes) : 
			base(attributes, null)
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
				match = ref_Renamed.Type.Equals(Type);
			}
			
			return match;
		}
		
		/// <summary> Returns a string representation of the service.</summary>
		/// <returns> a string representation
		/// </returns>
		public override System.String ToString()
		{
			return Reference.ToString();
		}

		/// <summary> Return the service classname key.</summary>
		/// <returns> the service classname
		/// </returns>
		public virtual System.Type Type
		{
			get
			{
				return m_reference.Type;
			}
			
		}

		/// <summary> Return the service reference.</summary>
		/// <returns> the reference
		/// </returns>
		public virtual ReferenceDescriptor Reference
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
		public virtual EntryDescriptor[] Entries
		{
			get
			{
				return m_entries;
			}
			
		}
	}
}