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
	
	/// <summary> Description of typeloader.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public sealed class TypeLoaderDirective
	{
		private static readonly LibraryDirective EMPTY_LIBRARY = new LibraryDirective();
		private static readonly ClasspathDirective EMPTY_CLASSPATH = new ClasspathDirective();
		
		/// <summary> The library directive.</summary>
		private LibraryDirective m_library;
		
		/// <summary> The root category hierachy.</summary>
		private ClasspathDirective m_classpath;
		
		/// <summary> Create an empty TypeLoaderDirective.</summary>
		public TypeLoaderDirective():this(null, null)
		{
		}
		
		/// <summary> Create a TypeLoaderDirective instance.
		/// 
		/// </summary>
		/// <param name="library">the library descriptor
		/// </param>
		/// <param name="classpath">the classpath descriptor
		/// </param>
		public TypeLoaderDirective(LibraryDirective library, ClasspathDirective classpath)
		{
			if (library == null)
			{
				m_library = EMPTY_LIBRARY;
			}
			else
			{
				m_library = library;
			}
			
			if (classpath == null)
			{
				m_classpath = EMPTY_CLASSPATH;
			}
			else
			{
				m_classpath = classpath;
			}
		}

		/// <summary> Return true if the library and classpath declarations are empty.
		/// If the function returns true, this directive is in an effective 
		/// default state and need not be externalized.
		/// 
		/// </summary>
		/// <returns> the empty status of this directive
		/// </returns>
		public bool Empty
		{
			get
			{
				return (m_library.Empty && m_classpath.Empty);
			}
			
		}

		/// <summary> Return the library directive.
		/// 
		/// </summary>
		/// <returns> the library directive.
		/// </returns>
		public LibraryDirective Library
		{
			get
			{
				return m_library;
			}
			
		}

		/// <summary> Return the classpath directive.
		/// 
		/// </summary>
		/// <returns> the classpath directive.
		/// </returns>
		public ClasspathDirective ClasspathDirective
		{
			get
			{
				return m_classpath;
			}
			
		}

	}
}