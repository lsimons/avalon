// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Configuration.Default
{
	using System;
	using System.Xml;
	using System.IO;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Keeps the container configuration.
	/// </summary>
	public class ContainerConfiguration
	{
		private IConfiguration m_configuration;

		/// <summary>
		/// Constructs a ContainerConfiguration with an optional parent.
		/// </summary>
		/// <param name="parent">The ContainerConfiguration parent. Can be null</param>
		public ContainerConfiguration(ContainerConfiguration parent)
		{
		}

		/// <summary>
		/// Constructs a ContainerConfiguration with an optional parent.
		/// </summary>
		/// <param name="parent">The ContainerConfiguration parent. Can be null</param>
		/// <param name="section">XmlNode to be parsed.</param>
		public ContainerConfiguration(ContainerConfiguration parent, XmlNode section) : this( parent )
		{
			if ( section == null )
			{
				throw new ArgumentNullException( "section" );
			}

			Deserialize( section );
		}

		/// <summary>
		/// Constructs a ContainerConfiguration with the
		/// <see cref="XmlNode"/> to be parsed.
		/// </summary>
		/// <param name="parent">The ContainerConfiguration parent. Can be null</param>
		/// <param name="section">XmlNode to be parsed.</param>
		public ContainerConfiguration(XmlNode section) : this(null, section)
		{
		}

		/// <summary>
		/// Constructs a ContainerConfiguration with the filename containing
		/// the xml to be parsed.
		/// </summary>
		/// <param name="filename">The filename to be parsed.</param>
		public ContainerConfiguration(String filename) : this(null as ContainerConfiguration)
		{
			ParseFromFile(filename);
		}

		/// <summary>
		/// Parses a configuration file. Looks for a node 
		/// 'configuration/avalon.container'
		/// </summary>
		/// <param name="filename">The xml full file name</param>
		private void ParseFromFile(String filename)
		{
			XmlTextReader reader = new XmlTextReader(
				new FileStream(filename, FileMode.Open, FileAccess.Read));

			XmlDocument doc = new XmlDocument() ;
			doc.Load(reader);

			XmlNode avalonNode = 
				doc.SelectSingleNode("configuration/" + AvalonConfigurationSectionHandler.Section);

			Deserialize( avalonNode );
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="section"></param>
		private void Deserialize( XmlNode section )
		{
			m_configuration = DefaultConfigurationSerializer.Deserialize( section );
		}

		/// <summary>
		/// 
		/// </summary>
		public IConfiguration Configuration
		{
			get
			{
				return m_configuration;
			}
		}
	}
}
