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
	
	/// <summary> <p>An library directive.</p>
	/// <p><b>XML</b></p>
	/// <p>An library element is normally contained within a scoping structure such as a
	/// classloader directive. The library element may contain any number of "include" 
	/// or "group" elements.</p>
	/// <pre>
	/// <font color="gray">&lt;library&gt;</font>
	/// &lt;include&gt;lib&lt;/include&gt;
	/// &lt;group&gt;avalon-framework&lt;/group&gt;
	/// <font color="gray">&lt;/library&gt;</font>
	/// </pre>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.3 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	[Serializable]
	public class LibraryDirective
	{
		private static readonly System.String[] EMPTY_SET = new System.String[0];
		
		/// <summary> The include paths</summary>
		private System.String[] m_includes;
		
		/// <summary> The groups</summary>
		private System.String[] m_groups;
		
		/// <summary> Create a new LibraryDirective instance.</summary>
		public LibraryDirective():this(null, null)
		{
		}
		
		/// <summary> Create a new LibraryDirective instance.
		/// 
		/// </summary>
		/// <param name="includes">the set of include paths
		/// </param>
		/// <param name="groups">the set of group identifiers
		/// </param>
		public LibraryDirective(System.String[] includes, System.String[] groups)
		{
			if (includes == null)
			{
				m_includes = EMPTY_SET;
			}
			else
			{
				m_includes = includes;
			}
			
			if (groups == null)
			{
				m_groups = EMPTY_SET;
			}
			else
			{
				m_groups = groups;
			}
		}
		
		/// <summary> Return the set of optional extension locations as a File[] 
		/// relative to a supplied base directory.
		/// 
		/// </summary>
		/// <param name="base">a base directory against which relatve references shall be resolved 
		/// </param>
		/// <returns> an array of extension library locations
		/// </returns>
		/// <exception cref=""> IOException if a path cannot be resolved to a directory
		/// </exception>
		public virtual System.IO.FileInfo[] getOptionalExtensionDirectories(System.IO.FileInfo baseObj)
		{
			if (baseObj == null)
			{
				throw new System.NullReferenceException("base");
			}
			System.String[] includes = Includes;
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < includes.Length; i++)
			{
				System.String path = includes[i];
				list.Add(getDirectory(baseObj, path));
			}
			return (System.IO.FileInfo[]) list.ToArray( typeof(System.IO.FileInfo) );
		}
		
		/// <summary> Return the empty status of this directive.</summary>
		public virtual bool Empty
		{
			get
			{
				int n = m_includes.Length + m_groups.Length;
				return n == 0;
			}
			
		}
		/// <summary> Return the set of include path entries.
		/// 
		/// </summary>
		/// <returns> the include paths
		/// </returns>
		public virtual System.String[] Includes
		{
			get
			{
				return m_includes;
			}
			
		}
		/// <summary> Return the set of group identifiers.
		/// 
		/// </summary>
		/// <returns> the group identifiers
		/// </returns>
		public virtual System.String[] Groups
		{
			get
			{
				return m_groups;
			}
			
		}

		private System.IO.FileInfo getDirectory(System.IO.FileInfo baseObj, System.String path)
		{
			System.IO.FileInfo file = new System.IO.FileInfo(path);
			return new System.IO.FileInfo( System.IO.Path.GetFullPath( file.FullName ) );
			/*if (file.A())
			{
				return verifyDirectory(file);
			}
			return verifyDirectory(new System.IO.FileInfo(baseObj.FullName + "\\" + path));*/
		}
		
		/*
		private System.IO.FileInfo verifyDirectory(System.IO.FileInfo dir)
		{
			if (System.IO.Directory.Exists(dir.FullName))
			{
				return dir.getCanonicalFile();
			}
			
			System.String error = "Path does not correspond to a directory: " + dir;
			throw new System.IO.IOException(error);
		}*/
	}
}