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

	/// <summary>
	/// This is an abstract <see cref="IConfiguration"/> implementation
	/// that deals with methods that can be abstracted away
	/// from underlying implementations.
	/// </summary>
	/// <remarks>
	/// <para><b>AbstractConfiguration</b> makes easier to implementers 
	/// to create a new version of <see cref="IConfiguration"/></para>
	/// </remarks>
	public abstract class AbstractConfiguration : IConfiguration
	{
		private bool readOnly;
		private string name;
		private string location;
		private string val;
		private string ns;
		private string prefix;
		private Hashtable attributes = new Hashtable();
		private ConfigurationCollection children = new ConfigurationCollection();

		/// <summary>
		/// Gets a value indicating whether the <see cref="IConfiguration"/> is read-only.
		/// </summary>
		/// <value>
		/// <see langword="true"/> if the <see cref="IConfiguration"/> is read-only;
		/// otherwise, <see langword="false"/>.
		/// </value>
		public bool IsReadOnly
		{
			get
			{
				return readOnly;
			}
		}

		/// <summary>
		/// Gets the name of the <see cref="IConfiguration"/>.
		/// </summary>
		/// <value>
		/// The Name of the <see cref="IConfiguration"/>.
		/// </value>
		public string Name
		{
			get
			{
				return name;
			}
			set
			{
				CheckReadOnly();

				name = value;
			}
		}

		/// <summary>
		/// Gets a string describing location of the <see cref="IConfiguration"/>.
		/// </summary>
		/// <value>
		/// A String describing location of the <see cref="IConfiguration"/>.
		/// </value>
		public string Location
		{
			get
			{
				return location;
			}
			set
			{
				CheckReadOnly();

				location = value;
			}
		}

		/// <summary>
		/// Gets the value of <see cref="IConfiguration"/>.
		/// </summary>
		/// <value>
		/// The Value of the <see cref="IConfiguration"/>.
		/// </value>
		public string Value
		{
			get
			{
				return val;
			}
			set
			{
				CheckReadOnly();

				val = value;
			}
		}

		/// <summary>
		/// Gets the namespace of the <see cref="IConfiguration"/>.
		/// </summary>
		/// <value>
		/// The Namespace of the <see cref="IConfiguration"/>.
		/// </value>
		public string Namespace
		{
			get
			{
				return ns;
			}

			set
			{
				CheckReadOnly();

				ns = value;
			}
		}

		/// <summary>
		/// Gets the prefix of the <see cref="IConfiguration"/>.
		/// </summary>
		/// <value>
		/// The prefix of the <see cref="IConfiguration"/>.
		/// </value>
		public string Prefix
		{
			get
			{
				return prefix;
			}

			set
			{
				CheckReadOnly();

				prefix = value;
			}
		}


		/// <summary>
		/// Gets all child nodes.
		/// </summary>
		/// <value>The <see cref="ConfigurationCollection"/> of child nodes.</value>
		public ConfigurationCollection Children
		{
			get
			{
				if (children == null)
				{
					children = new ConfigurationCollection();
				}

				return children;
			}

			set
			{
				CheckReadOnly();

				children = value;
			}
		}

		/// <summary>
		/// Gets node attributes.
		/// </summary>
		/// <value>
		/// All attributes of the node.
		/// </value>
		public  IDictionary Attributes
		{
			get
			{
				if (attributes == null)
				{
					attributes = new Hashtable();
				}

				return attributes;
			}

			set
			{
				CheckReadOnly();

				attributes = new Hashtable(value);
			}
		}

		/// <summary>
		///	Gets a <see cref="IConfiguration"/> instance encapsulating the specified
		/// child node.
		/// </summary>
		/// <param name="child">The Name of the child node.</param>
		/// <returns>
		///	The <see cref="IConfiguration"/> instance encapsulating the specified
		///	child node.
		/// </returns>
		public IConfiguration GetChild(string child)
		{
			return GetChild(child, false);
		}

		/// <summary>
		///	Gets a <see cref="IConfiguration"/> instance encapsulating the specified
		/// child node.
		/// </summary>
		/// <param name="child">The Name of the child node.</param>
		/// <param name="createNew">
		///	If <see langword="true"/>, a new <see cref="IConfiguration"/>
		/// will be created and returned if the specified child does not exist.
		/// If <see langword="false"/>, <see langword="null"/> will be returned when the specified
		/// child doesn't exist.
		/// </param>
		/// <returns>
		///	The <see cref="IConfiguration"/> instance encapsulating the specified
		///	child node.
		/// </returns>
		public abstract IConfiguration GetChild(string child, bool createNew);

		/// <summary>
		/// Return an <see cref="ConfigurationCollection"/> of <see cref="IConfiguration"/>
		/// elements containing all node children with the specified name.
		/// </summary>
		/// <param name="name">The Name of the children to get.</param>
		/// <returns>
		/// All node children with the specified name
		/// </returns>
		public abstract ConfigurationCollection GetChildren(string name);

		/// <summary>
		/// Gets the value of the node and converts it
		/// into specified <see cref="System.Type"/>.
		/// </summary>
		/// <param name="type">The <see cref="System.Type"/></param>
		/// <returns>The Value converted into the specified type.</returns>
		/// <exception cref="InvalidCastException">
		/// If the convertion fails, an exception will be thrown.
		/// </exception>
		public object GetValue(Type type)
		{
			return GetValue(type, null);
		}

		/// <summary>
		/// Gets the value of the node and converts it
		/// into specified <see cref="System.Type"/>.
		/// </summary>
		/// <param name="type">The <see cref="System.Type"/></param>
		/// <param name="defaultValue">
		/// The Default value returned if the convertion fails.
		/// </param>
		/// <returns>The Value converted into the specified type.</returns>
		public object GetValue(Type type, object defaultValue)
		{

			return Converter.ChangeType(Value, type, defaultValue);
		}

		/// <summary>
		/// Gets the value of specified attribute and
		/// converts it into specified <see cref="System.Type"/>.
		/// </summary>
		/// <param name="name">The Name of the attribute you ask the value of.</param>
		/// <param name="type">The <see cref="System.Type"/></param>
		/// <returns>The Value converted into the specified type.</returns>
		/// <exception cref="InvalidCastException">
		/// If the convertion fails, an exception will be thrown.
		/// </exception>
		public object GetAttribute(string name, Type type)
		{

			return GetAttribute(name, type, null);
		}

		/// <summary>
		/// Gets the value of specified attribute and
		/// converts it into specified <see cref="System.Type"/>.
		/// </summary>
		/// <param name="name">The Name of the attribute you ask the value of.</param>
		/// <param name="type">The <see cref="System.Type"/></param>
		/// <param name="defaultValue">
		/// The Default value returned if the convertion fails.
		/// </param>
		/// <returns>The Value converted into the specified type.</returns>
		public object GetAttribute(string name, Type type, object defaultValue)
		{

			return Converter.ChangeType(Attributes[name], type, defaultValue);
		}

		/// <summary>
		/// Make the configuration read only.
		/// </summary>
		public void MakeReadOnly()
		{
			readOnly = true;
		}

		/// <summary>
		/// Check whether this node is readonly or not.
		/// </summary>
		/// <exception cref="ConfigurationException">
		/// If this node is readonly then an exception will be thrown.
		/// </exception>
		protected void CheckReadOnly()
		{
			if( IsReadOnly )
			{
				throw new ConfigurationException( "Configuration is read only and can not be modified." );
			}
		}
	}
}
