// Copyright 2003-2004 The Apache Software Foundation
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
	
	
	/// <summary> A entry descriptor declares the context entry import or creation criteria for
	/// a single context entry instance.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A entry may contain either (a) a single nested import directive, or (b) a single param constructor directives.</p>
	/// <pre>
	/// <font color="gray">&lt;context&gt;</font>
	/// 
	/// &lt!-- option (a) nested import -->
	/// &lt;entry key="<font color="darkred">my-home-dir</font>"&gt;
	/// &lt;include key="<font color="darkred">urn:avalon:home</font>"/&gt;
	/// &lt;/entry&gt;
	/// 
	/// &lt!-- option (b) param constructors -->
	/// &lt;entry key="<font color="darkred">title</font>"&gt;
	/// &lt;param&gt;<font color="darkred">Lord of the Rings</font>&lt;/&gt;
	/// &lt;/entry&gt;
	/// &lt;entry key="<font color="darkred">home</font>"&gt;
	/// &lt;param class="<font color="darkred">java.io.File</font>"&gt;<font color="darkred">../home</font>&lt;/param&gt;
	/// &lt;/entry&gt;
	/// 
	/// <font color="gray">&lt;/context&gt;</font>
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="ImportDirective">
	/// </seealso>
	/// <seealso cref="Parameter">
	/// </seealso>
	/// <seealso cref="ContextDirective">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	public class ConstructorDirective : EntryDirective
	{

		/// <summary> The constructor classname.</summary>
		private System.String m_classname;
		
		/// <summary> The constructor param.</summary>
		private Parameter[] m_params;
		
		/// <summary> The alternative argument.</summary>
		private System.String m_argument;
		
		/// <summary> Creation of a new entry directive using a constructor
		/// classname and single argument value.
		/// </summary>
		/// <param name="key">the entry key
		/// </param>
		/// <param name="value">the single argument value
		/// </param>
		public ConstructorDirective(System.String key, System.String valueObj):this(key, "java.lang.String", valueObj)
		{
		}
		
		/// <summary> Creation of a new entry directive using a constructor
		/// classname and single argument value.
		/// </summary>
		/// <param name="key">the entry key
		/// </param>
		/// <param name="classname">the classname of the entry implementation
		/// </param>
		/// <param name="value">the single argument value
		/// </param>
		public ConstructorDirective(System.String key, System.String classname, System.String valueObj):base(key)
		{
			
			if (null == (System.Object) classname)
			{
				throw new System.NullReferenceException("classname");
			}
			
			m_params = new Parameter[0];
			m_classname = classname;
			m_argument = valueObj;
		}
		
		/// <summary> Creation of a new entry directive using a parameter.</summary>
		/// <param name="key">the entry key
		/// </param>
		/// <param name="parameters">implementation class constructor parameter directives
		/// </param>
		public ConstructorDirective(System.String key, Parameter[] parameters):this(key, "java.lang.String", parameters)
		{
		}
		
		/// <summary> Creation of a new entry directive using a parameter.</summary>
		/// <param name="key">the entry key
		/// </param>
		/// <param name="classname">the classname of the entry implementation
		/// </param>
		/// <param name="params">implementation class constructor parameter directives
		/// </param>
		public ConstructorDirective(System.String key, System.String classname, Parameter[] paramsObj):base(key)
		{
			
			if (null == paramsObj)
			{
				throw new System.NullReferenceException("parameters");
			}
			if (null == (System.Object) classname)
			{
				throw new System.NullReferenceException("classname");
			}
			
			m_classname = classname;
			m_params = paramsObj;
			m_argument = null;
		}

		/// <summary> Return the constructor classname</summary>
		/// <returns> the classname
		/// </returns>
		public virtual System.String Classname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the parameter directive if the mode is PARAM else null.</summary>
		/// <returns> the directive
		/// </returns>
		public virtual Parameter[] Parameters
		{
			get
			{
				return m_params;
			}
			
		}
		/// <summary> Return the constructor single argument</summary>
		/// <returns> the costructor argument
		/// </returns>
		public virtual System.String Argument
		{
			get
			{
				return m_argument;
			}
			
		}
	}
}