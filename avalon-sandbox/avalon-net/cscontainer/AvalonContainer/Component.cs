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

namespace Apache.Avalon.Container
{
	using System;
	using System.Xml;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Util;
	using Apache.Avalon.Container.Services;

	using Apache.Avalon.Meta;

	/// <summary>
	/// 
	/// </summary>
	public sealed class ComponentEntry : IDisposable
	{
		private TypeDescriptor m_descriptor;
		private IConfiguration m_configuration;
		private Type m_type;
		private IComponentHandler m_handler;

		public ComponentEntry(TypeDescriptor descriptor)
		{
			m_descriptor = descriptor;
			m_type = Type.GetType( descriptor.Info.Typename, true, true );
			m_configuration = DefaultConfiguration.EmptyConfiguration;
		}

		public Lifestyle Lifestyle
		{
			get
			{
				return m_descriptor.Info.Lifestyle;
			}
		}

		public Type ComponentType
		{
			get
			{
				return m_type;
			}
		}

		public IConfiguration Configuration
		{
			get
			{
				return m_configuration;
			}
		}

		public String ConfigurationName
		{
			get
			{
				return Name;
			}
		}

		public String LoggerName
		{
			get
			{
				if (m_descriptor.Categories != null && m_descriptor.Categories.Length != 0)
				{	
					return m_descriptor.Categories[0].Name;
				}
				return Name;
			}
		}

		public String Name
		{
			get
			{
				return m_descriptor.Info.Name;
			}
		}

		public IComponentHandler Handler
		{
			get
			{
				return m_handler;
			}
			set
			{
				m_handler = value;
			}
		}

		public DependencyDescriptor[] Dependencies
		{
			get
			{
				return m_descriptor.Dependencies;
			}
		}

		public void ExtractConfigurationNode(IDictionary configuration)
		{
			XmlNode configNode = (XmlNode) configuration[ConfigurationName];

			if (configNode != null)
			{
				m_configuration = ConfigurationUtil.GetConfiguration(configNode);
			}
		}

		#region IDisposable Members

		public void Dispose()
		{
			ContainerUtil.Dispose( m_handler );
		}

		#endregion
	}

	/// <summary>
	/// Summary description for ComponentCollection.
	/// </summary>
	internal class ComponentCollection : IEnumerable
	{
		private Hashtable m_components;

		public ComponentCollection()
		{
			m_components = new Hashtable();
		}

		public void Add(String role, ComponentEntry entry)
		{
			// TODO : Perform check before adding..
			// Role already exists?

			m_components.Add(role, entry);
		}

		public bool Contains(String role)
		{
			return m_components.Contains(role);
		}

		public int Count
		{
			get
			{
				return m_components.Count;
			}
		}

		public ComponentEntry this [String role]
		{
			get
			{
				return m_components[role] as ComponentEntry;
			}
		}

		#region IEnumerable Members

		public IEnumerator GetEnumerator()
		{
			return m_components.Values.GetEnumerator();
		}

		#endregion

		internal IEnumerable GetEntries()
		{
			return new EntriesEnumerable(m_components);
		}

		internal class EntriesEnumerable : IEnumerable
		{
			IDictionary m_entries;

			public EntriesEnumerable(IDictionary entries)
			{
				m_entries = entries;
			}

			#region IEnumerable Members

			public IEnumerator GetEnumerator()
			{
				return m_entries.GetEnumerator();
			}

			#endregion
		}
	}
}
