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

namespace Apache.Avalon.Castle.Controller.Config
{
	using System;
	using System.Xml;

	/// <summary>
	/// Summary description for CastleConfig.
	/// </summary>
	internal class CastleConfig
	{
		ComponentDescriptorCollection components = new ComponentDescriptorCollection();

		public CastleConfig(CastleConfig parent, XmlNode section)
		{
			// TODO: Shall we care about parent configurations? 

			ParseServices(section);
		}

		private void ParseServices(XmlNode section)
		{
			foreach(XmlNode component in section.ChildNodes)
			{
				if (component.NodeType != XmlNodeType.Element)
				{
					continue;
				}

				ParseComponent(component);
			}
		}

		private void ParseComponent(XmlNode component)
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
				else if (String.Compare(inner.Name, "depends", true) == 0)
				{
					ParseDependency(desc, inner);
				}
			}

			components.Add(desc);
		}

		private void ParseAttribute(ComponentDescriptor component, XmlNode att)
		{
			String name  = GetAttribute( att.Attributes["name"] );
			String value = GetAttribute( att.Attributes["value"] );

			AttributeDescriptor desc = 
				new AttributeDescriptor(name, value);

			component.Attributes.Add(desc);
		}

		private void ParseDependency(ComponentDescriptor component, XmlNode att)
		{
			String name   = GetAttribute( att.Attributes["name"] );
			bool optional = bool.Parse( GetAttribute( att.Attributes["optional"] ) );

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

		public ComponentDescriptorCollection Components
		{
			get
			{
				return components;
			}
		}
	}
}
