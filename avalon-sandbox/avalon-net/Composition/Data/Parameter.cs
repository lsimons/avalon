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

namespace Apache.Avalon.Composition.Data
{
	using System;
	using System.Runtime.InteropServices;
	
	/// <summary> A <code>Parameter</code> represents a single constructor typed argument value.  A parameter
	/// container a classname (default value of <code>java.lang.String</code>) and possible sub-parameters.
	/// A parameter's value is established by creating a new instance using the parameter's classname,
	/// together with the values directived from the sub-sidiary parameters as constructor arguments.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A param is a nested structure containing a string value or contructor parameter arguments.</p>
	/// <pre>
	/// <font color="gray">&lt;-- Simple string param declaration --&gt;</font>
	/// 
	/// &lt;param&gt;<font color="darkred">London</font>&lt;/param&gt;
	/// 
	/// <font color="gray">&lt;-- Typed param declaration --&gt;</font>
	/// 
	/// &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">./home</font>&lt;/param&gt;
	/// 
	/// <font color="gray">&lt;-- Typed parameter declaration referencing a context value --&gt;</font>
	/// 
	/// &lt;param class="<font color="darkred">java.lang.ClassLoader</font>"&gt;<font color="darkred">${my-classloader-import-key}</font>&lt;/param&gt;
	/// 
	/// <font color="gray">&lt;-- Multi-argument parameter declaration --&gt;</font>
	/// 
	/// &lt;param class="<font color="darkred">MyClass</font>"&gt;
	/// &lt;param class="<font color="darkred">java.io.File</font>"><font color="darkred">./home</font>&lt;/param&gt;
	/// &lt;param&gt;<font color="darkred">London</font>&lt;/param&gt;
	/// &lt;/param&gt;
	/// </pre>
	/// 
	/// <p>TODO: Fix usage of basic type (int, float, long, etc.) - how do we return 
	/// basic types - can't use getValue() becuase it returns an Object unless
	/// have some way of packing the basic type into a carrier</p>
	/// 
	/// </summary>
	/// <seealso cref="EntryDirective">
	/// </seealso>
	/// <seealso cref="ImportDirective">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public class Parameter
	{
		/// <summary> Return the classname of the parameter implementation to use.</summary>
		/// <returns> the classname
		/// </returns>
		public virtual System.String Classname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the argument (may be null).</summary>
		public virtual System.String Argument
		{
			get
			{
				return m_argument;
			}
			
		}
		/// <summary> Return the constructor parameters for this parameter.</summary>
		public virtual Parameter[] Parameters
		{
			get
			{
				return m_parameters;
			}
			
		}
		
		/// <summary> The classname to use as the parameter implementation class (defaults to java.lang.String)</summary>
		private System.String m_classname;
		
		/// <summary> The supplied argument.</summary>
		private System.String m_argument;
		
		/// <summary> The sub-parameters from which the value for this parameter may be derived.</summary>
		private Parameter[] m_parameters;
		
		/// <summary> The derived value.</summary>
		// [NonSerialized()]
		// private System.Object m_value;
		
		/// <summary> Creation of a new parameter using the default <code>java.lang.String</code>
		/// type and a supplied value.
		/// 
		/// </summary>
		/// <param name="value">the string value
		/// </param>
		public Parameter(System.String valueObj)
		{
			m_classname = "System.String";
			m_parameters = new Parameter[0];
			m_argument = valueObj;
		}
		
		/// <summary> Creation of a new entry directive using a supplied classname and value.</summary>
		/// <param name="classname">the classname of the parameter
		/// </param>
		/// <param name="value">the parameter constructor value
		/// </param>
		public Parameter(System.String classname, System.String valueObj)
		{
			if (null == (System.Object) classname)
			{
				throw new System.NullReferenceException("classname");
			}
			
			m_classname = classname;
			m_parameters = new Parameter[0];
			m_argument = valueObj;
		}
		
		/// <summary> Creation of a new entry directive.</summary>
		/// <param name="classname">the classname of the entry implementation
		/// </param>
		/// <param name="parameters">implementation class constructor parameter directives
		/// </param>
		public Parameter(System.String classname, Parameter[] parameters)
		{
			if (null == (System.Object) classname)
			{
				throw new System.NullReferenceException("classname");
			}
			if (null == parameters)
			{
				throw new System.NullReferenceException("parameters");
			}
			
			m_classname = classname;
			m_parameters = parameters;
		}
	}
}