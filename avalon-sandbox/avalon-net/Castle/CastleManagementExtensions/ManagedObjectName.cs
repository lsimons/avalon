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

namespace Apache.Avalon.Castle.ManagementExtensions
{
	using System;
	using System.Text;
	using System.Collections;
	using System.Runtime.Serialization;

	/// <summary>
	/// Represents a ManagedObject's Name. 
	/// TODO: Supports query semantic.
	/// </summary>
	[Serializable]
	public class ManagedObjectName : ISerializable
	{
		protected String domain;
		protected String literalProperties = String.Empty;
		protected Hashtable properties;

		/// <summary>
		/// Creates a ManagedObjectName using a name pattern like
		/// "domain:key=value,key2=value2"
		/// </summary>
		/// <param name="name">Complete name</param>
		public ManagedObjectName(String name)
		{
			Setup(name);
		}

		/// <summary>
		/// Creates a ManagedObjectName with specified domain and 
		/// properties.
		/// </summary>
		/// <param name="domain">Domain name</param>
		/// <param name="properties">Property list.</param>
		public ManagedObjectName(String domain, String properties)
		{
			SetupDomain(domain);
			SetupProperties(properties);
		}

		/// <summary>
		/// Creates a ManagedObjectName with specified domain and 
		/// properties.
		/// </summary>
		/// <param name="domain">Domain name</param>
		/// <param name="properties">Property list.</param>
		public ManagedObjectName(String domain, Hashtable properties)
		{
			SetupDomain(domain);
			SetupProperties(properties);
		}

		/// <summary>
		/// Serialization constructor.
		/// </summary>
		/// <param name="info"></param>
		/// <param name="context"></param>
		public ManagedObjectName(SerializationInfo info, StreamingContext context)
		{
			String domain = info.GetString("domain");
			String props  = info.GetString("props");
			SetupDomain(domain);
			SetupProperties(props);
		}

		/// <summary>
		/// Parses the full name extracting the domain and properties.
		/// </summary>
		/// <param name="name">Full name.</param>
		protected virtual void Setup(String name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}

			if (name.IndexOf(':') != -1)
			{
				String[] splitted = name.Split(new char[] { ':' });
				
				SetupDomain(splitted[0]);
				SetupProperties(splitted[1]);
			}
			else
			{
				SetupDomain(name);
			}
		}

		/// <summary>
		/// Sets up the domain. Can be empty but can't be null.
		/// </summary>
		/// <param name="domain"></param>
		protected virtual void SetupDomain(String domain)
		{
			if (domain == null)
			{
				throw new ArgumentNullException("domain");
			}

			this.domain = domain;
		}

		/// <summary>
		/// Parses and validate a properties list string like 
		/// "key=value,key2=value2" and so on.
		/// </summary>
		/// <param name="properties">Property list.</param>
		protected virtual void SetupProperties(String properties)
		{
			if (properties == null)
			{
				throw new ArgumentNullException("properties");
			}

			String [] props = properties.Split( new char[] { ',' } );

			Hashtable propsHash = new Hashtable(
				CaseInsensitiveHashCodeProvider.Default, 
				CaseInsensitiveComparer.Default);

			foreach(String chunk in props)
			{
				if (chunk.IndexOf('=') == -1)
				{
					throw new InvalidManagedObjectName("Invalid properties.");
				}

				String[] keyvalue = chunk.Split( new char[] { '=' } );

				String key = keyvalue[0];
				String value = keyvalue[1];

				propsHash.Add(key, value);
			}

			SetupProperties(propsHash);
		}

		/// <summary>
		/// Validates a properties Hashtable.
		/// </summary>
		/// <param name="properties">Property list.</param>
		protected virtual void SetupProperties(Hashtable properties)
		{
			StringBuilder sb = new StringBuilder();

			foreach(DictionaryEntry entry in properties)
			{
				if (sb.Length != 0)
				{
					sb.Append(",");
				}

				String key = null;

				try
				{
					key = (String) entry.Key;
				}
				catch(InvalidCastException)
				{
					throw new InvalidManagedObjectName("Key is not a String.");
				}

				String value = null;

				try
				{
					value = (String) entry.Value;
				}
				catch(InvalidCastException)
				{
					throw new InvalidManagedObjectName("Value is not a String.");
				}

				sb.AppendFormat("{0}={1}", key, value);
			}

			this.literalProperties = sb.ToString();
			this.properties = new Hashtable(properties);
		}

		public String Domain
		{
			get
			{
				return domain;
			}
		}

		public String LiteralProperties
		{
			get
			{
				return literalProperties;
			}
		}

		public String this[ String key ]
		{
			get
			{
				if (key == null)
				{
					throw new ArgumentNullException("key");
				}

				return (String) this.properties[key];
			}
		}

		public override bool Equals(object obj)
		{
			ManagedObjectName other = obj as ManagedObjectName;

			if (other != null)
			{
				return other.domain.Equals(domain) && 
					other.literalProperties.Equals(literalProperties);
			}

			return false;
		}
	
		public override int GetHashCode()
		{
			return domain.GetHashCode() ^ literalProperties.GetHashCode();
		}
	
		public override string ToString()
		{
			return 
				String.Format("Domain: {0} Properties: {1}", 
					domain, literalProperties);
		}

		#region ISerializable Members

		public void GetObjectData(SerializationInfo info, StreamingContext context)
		{
			info.AddValue("domain", domain);
			info.AddValue("props", literalProperties);
		}

		#endregion
	}
}
