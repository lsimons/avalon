// Copyright 2004 Apache Software Foundation
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
