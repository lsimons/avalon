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

namespace Apache.Avalon.Castle.Controller.Config
{
	using System;
	using System.Xml;

	/// <summary>
	/// 
	/// </summary>
	internal enum LoggerType
	{
		Null,
		Console,
		Log4net
	}

	/// <summary>
	/// Summary description for CastleConfig.
	/// </summary>
	internal class CastleConfig
	{
		private ComponentDescriptorCollection components = new ComponentDescriptorCollection();

		public CastleConfig(CastleConfig parent, XmlNode section)
		{
			// TODO: Shall we care about parent configurations? 

			ParseServices(section);
		}

		#region Parsing

		private void ParseServices(XmlNode section)
		{
			foreach(XmlNode component in section.ChildNodes)
			{
				if (component.NodeType != XmlNodeType.Element)
				{
					continue;
				}

				ParseComponent(components, component);
			}
		}

		private void ParseComponent(ComponentDescriptorCollection comps, XmlNode component)
		{
			String typeName = GetAttribute( component.Attributes["type"] );
			String name = GetAttribute( component.Attributes["name"] );

			ComponentDescriptor desc = new ComponentDescriptor(typeName, name);

			foreach(XmlNode inner in component.ChildNodes)
			{
				if (inner.NodeType != XmlNodeType.Element)
				{
					continue;
				}

				if (String.Compare(inner.Name, "attribute", true) == 0)
				{
					ParseAttribute(desc, inner);
				}
				else if (String.Compare(inner.Name, "dependencies", true) == 0)
				{
					ParseDependencies(desc, inner);
				}
			}

			comps.Add(desc);
		}

		private void ParseAttribute(ComponentDescriptor component, XmlNode att)
		{
			String name  = GetAttribute( att.Attributes["name"] );
			String value = att.InnerText;

			AttributeDescriptor desc = 
				new AttributeDescriptor(name, value);

			component.Attributes.Add(desc);
		}

		private void ParseDependencies(ComponentDescriptor component, XmlNode node)
		{
			foreach(XmlNode dependency in node.ChildNodes)
			{
				if (dependency.NodeType != XmlNodeType.Element)
				{
					continue;
				}

				if (String.Compare(dependency.Name, "mcomponent", true) == 0)
				{
					ParseComponent( component.Dependencies.Components, dependency );
				}
				else if (String.Compare(dependency.Name, "depends", true) == 0)
				{
					ParseDependency( component, node );
				}
			}
		}

		private void ParseDependency(ComponentDescriptor component, XmlNode node)
		{
			String name   = GetAttribute( node.Attributes["name"] );
			bool optional = bool.Parse( GetAttribute( node.Attributes["optional"] ) );

			DependencyDescriptor desc = 
				new DependencyDescriptor(name, optional);

			component.Dependencies.Add(desc);
		}

		private String GetAttribute( XmlAttribute att )
		{
			if (att == null)
			{
				return null;
			}

			return att.Value;
		}

		#endregion

		public ComponentDescriptorCollection Components
		{
			get
			{
				return components;
			}
		}
	}
}
