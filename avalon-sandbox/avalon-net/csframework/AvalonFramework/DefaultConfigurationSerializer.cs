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

namespace Apache.Avalon.Framework
{
	using System;
	using System.Collections;
	using System.IO;
	using System.Xml;

	/// <summary>
	/// A Serializer/Deserializer of a <see cref="DefaultConfiguration"/>.
	/// </summary>
	public class DefaultConfigurationSerializer
	{
		/// <summary>
		/// Makes a serialization of a <see cref="DefaultConfiguration"/> instance.
		/// </summary>
		/// <param name="filename">
		/// The File name where <see cref="DefaultConfiguration"/> instance will be
		/// serialized to.
		/// </param>
		/// <param name="configuration">A <see cref="DefaultConfiguration"/> instance to serialize</param>
		public static void Serialize(string filename, DefaultConfiguration configuration)
		{
			XmlTextWriter writer = new XmlTextWriter(new StreamWriter(filename));

			//Use indentation for readability.
			writer.Formatting = Formatting.Indented;
			writer.Indentation = 4;
			
			writer.WriteStartDocument(true);

			Serialize(writer, configuration);				

			writer.WriteEndDocument(); 
			writer.Close(); 
		}

		/// <summary>
		/// Makes a serialization of a <see cref="DefaultConfiguration"/> instance.
		/// </summary>
		/// <param name="writer"></param>
		/// <param name="configuration">A <see cref="DefaultConfiguration"/> instance to serialize.</param>
		public static void Serialize(XmlWriter writer, DefaultConfiguration configuration)
		{
			// serialize the configuration
			writer.WriteStartElement(configuration.Prefix, configuration.Name, configuration.Namespace); 
			
			// attribute serialization
			foreach (DictionaryEntry attr in configuration.Attributes) 
			{
				writer.WriteAttributeString(attr.Key.ToString(), attr.Value.ToString()); 
			}  

			if (configuration.Value != null)
			{
				writer.WriteString(configuration.Value.ToString());
			}

			// child serialization
			foreach(IConfiguration child in configuration.Children)
			{
				Serialize(writer, (DefaultConfiguration) child);
			}

			writer.WriteEndElement();
		}

		/// <summary>
		/// Makes a deserialization of a <see cref="DefaultConfiguration"/> instance.
		/// </summary>
		/// <param name="fileName">The Name of the file, containing the XML document to deserialize.</param>
		/// <returns>A Deserialized <see cref="DefaultConfiguration"/> instance.</returns>
		public static DefaultConfiguration Deserialize(string fileName)
		{
			DefaultConfiguration configuration = null;

			XmlDocument document = new XmlDocument();
			document.Load(fileName);
			
			XmlNode root = document.DocumentElement; 

			if (root != null)
			{
				configuration = Deserialize(root, null);
			}

			return configuration;
		}

		/// <summary>
		/// Makes a deserialization of <see cref="XmlNode"/> instance.
		/// </summary>
		/// <param name="node">The Node to deserialize.</param>
		/// <param name="parent">A Deserialized <see cref="DefaultConfiguration"/> parent instance.</param>
		/// <returns>A Deserialized <see cref="DefaultConfiguration"/> instance.</returns>
		public static DefaultConfiguration Deserialize(XmlNode node, DefaultConfiguration parent)
		{
			// node deserialization
			DefaultConfiguration configuration = null;

			if ((node.NodeType  == XmlNodeType.CDATA) || (node.NodeType == XmlNodeType.Text))
			{
				if (parent != null)
				{
					parent.Value = string.Concat(parent.Value, node.Value);
				}
			}

			if ((node.NodeType  == XmlNodeType.Document) || (node.NodeType == XmlNodeType.Element))
			{
				configuration = new DefaultConfiguration(node.LocalName, "-", node.NamespaceURI, node.Prefix);
	
				// attribute deserialization
				if (node.Attributes != null)
				{
					foreach (XmlAttribute attr in node.Attributes)
					{
						if (string.Compare(attr.Prefix, string.Empty) == 0)
						{
							configuration.Attributes[attr.Name] = attr.Value;  
						}
					}
				}

				// child deserialization
				foreach (XmlNode child in node.ChildNodes)
				{
					DefaultConfiguration childConfiguration = Deserialize(child, configuration);
					
					if (childConfiguration != null)
					{
						configuration.Children.Add(childConfiguration); 
					}
				}
			}

			return configuration;
		}

		/// <summary>
		/// Makes a deserialization of <see cref="XmlNode"/> instance.
		/// </summary>
		/// <param name="node">The Node to deserialize.</param>
		/// <returns>A Deserialized <see cref="DefaultConfiguration"/> instance.</returns>
		public static DefaultConfiguration Deserialize(XmlNode node)
		{
			return Deserialize(node, null);
		}
	}
}
