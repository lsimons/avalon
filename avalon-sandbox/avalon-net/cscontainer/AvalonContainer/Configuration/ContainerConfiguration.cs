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

namespace Apache.Avalon.Container.Configuration
{
	using System;
	using System.Collections;
	using System.Collections.Specialized;
	using System.Xml;
	using System.Reflection;
	using System.IO;
	using System.Security;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for ContainerConfiguration.
	/// </summary>
	public sealed class ContainerConfiguration
	{
		private static readonly String ComponentNodeName   = "component";
		private static readonly String ComponentsNodeName  = "components";
		private static readonly String LoggerNodeName      = "component";
		private static readonly String AssemblyNodeName    = "assembly";
		private static readonly String ExtensionNodeName   = "extensionModule";

		private static readonly String TypeAttributeName   = "type";
		private static readonly String ConfigAttributeName = "configurationName";
		
		private ArrayList m_assemblies;
		private Hashtable m_componentConfig;
		private XmlNode   m_extensionsNode;
		private XmlNode   m_loggerNode;

		private ContainerConfiguration(ContainerConfiguration parent)
		{
			m_loggerNode = null;

			if (parent == null)
			{
				m_assemblies = new ArrayList();

				m_componentConfig = new Hashtable(
					CaseInsensitiveHashCodeProvider.Default, 
					CaseInsensitiveComparer.Default);

				m_extensionsNode = m_loggerNode = null;
			}
			else
			{
				m_assemblies = new ArrayList(parent.Assemblies);
				
				m_componentConfig = new Hashtable(parent.ComponentConfiguration, 
					CaseInsensitiveHashCodeProvider.Default, 
					CaseInsensitiveComparer.Default);

				m_extensionsNode = parent.ExtensionsNode;
				m_loggerNode = parent.LoggerNode;
			}
		}

		public ContainerConfiguration(ContainerConfiguration parent, XmlNode node) : this(parent)
		{
			if (parent != null)
			{
				m_assemblies.AddRange( parent.Assemblies );
			}

			Parse(node);
		}

		public ContainerConfiguration(XmlNode node) : this(null, node)
		{
		}

		public ContainerConfiguration(String filename) : this(null as ContainerConfiguration)
		{
			ParseFromFile(filename);
		}

		private void ParseFromFile(String filename)
		{
			XmlTextReader reader = new XmlTextReader(
				new FileStream(filename, FileMode.Open, FileAccess.Read));

			XmlDocument doc = new XmlDocument() ;
			doc.Load(reader);

			XmlNode avalonNode = 
				doc.SelectSingleNode("configuration/" + ContainerConfigurationSectionHandler.Section);

			Parse(avalonNode);
		}

		private void Parse(XmlNode node)
		{
			foreach(XmlNode childNode in node)
			{
				if (childNode.NodeType != XmlNodeType.Element)
				{
					continue;
				}

				if (childNode.Name.Equals( ComponentsNodeName ))
				{
					foreach(XmlNode componentChild in childNode)
					{
						ParseChildNode( componentChild );
					}
				}
				else if ( childNode.Name.Equals( ExtensionNodeName ) )
				{
					this.m_extensionsNode = childNode;
				}
				else if ( childNode.Name.Equals( LoggerNodeName ) )
				{
					this.m_loggerNode = childNode;
				}
			}
		}

		private void ParseChildNode(XmlNode childNode)
		{
			if ( childNode.Name.Equals( AssemblyNodeName ) )
			{
				ParseAssemblyNode( childNode );
			}
			else if ( childNode.Name.Equals( ComponentNodeName ) )
			{
				ParseComponentNode( childNode );
			}
		}

		private void ParseAssemblyNode(XmlNode childNode)
		{
			XmlAttribute typeAtt = childNode.Attributes[TypeAttributeName];

			if (typeAtt == null)
			{
				MissingAttributeError(TypeAttributeName, AssemblyNodeName);
			}

			String assemblyType = typeAtt.Value;

			try
			{
				Assembly assembly = Assembly.Load(assemblyType);
				
				m_assemblies.Add(assembly);
			}
			catch(FileNotFoundException inner)
			{
				throw new ConfigurationException("Can't load assembly. File not found.", inner);
			}
			catch(BadImageFormatException inner)
			{
				throw new ConfigurationException("The assembly file was found, but was corrupted.", inner);
			}
			catch(SecurityException inner)
			{
				throw new ConfigurationException("Due a security exception, we cannot load the assembly.", inner);
			}
		}

		private void ParseComponentNode(XmlNode childNode)
		{
			XmlAttribute configNameAtt = childNode.Attributes[ConfigAttributeName];

			if (configNameAtt == null)
			{
				MissingAttributeError(ConfigAttributeName, ComponentNodeName);
			}

			m_componentConfig[configNameAtt.Value] = childNode;
		}

		private void MissingAttributeError(String attribute, String parentElement)
		{
			String message = 
				String.Format("Missing attribute '{0}' in element '{1}'.", 
				attribute, parentElement);

			throw new ConfigurationException(message);
		}

		private void UnexpectedNodeError(String expected, String current, String parent)
		{
			String message = 
				String.Format("Unexpected node '{0}' found instead of '{1}' in element '{2}'.", 
				current, expected, parent);

			throw new ConfigurationException(message);
		}

		internal Assembly[] Assemblies
		{
			get
			{
				Assembly[] assemblies = new Assembly[m_assemblies.Count];
				m_assemblies.CopyTo( assemblies, 0 );
				return assemblies;
			}
		}

		internal IDictionary ComponentConfiguration
		{
			get
			{
				return m_componentConfig;
			}
		}

		internal XmlNode ExtensionsNode
		{
			get
			{
				return m_extensionsNode;
			}
		}

		internal XmlNode LoggerNode
		{
			get
			{
				return m_loggerNode;
			}
		}
	}
}
