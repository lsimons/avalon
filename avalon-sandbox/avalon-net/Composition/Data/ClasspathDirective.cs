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
	
	/// <summary> Description of classpath.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public sealed class ClasspathDirective
	{
		/// <summary> Return the default status of this directive.  If TRUE
		/// the enclosed repository and fileset directives are empty.
		/// </summary>
		public bool Empty
		{
			get
			{
				int n = m_repositories.Length + m_filesets.Length;
				return n == 0;
			}
			
		}
		/// <summary> Return the set of resource directives.
		/// 
		/// </summary>
		/// <returns> the resource directive set
		/// </returns>
		public RepositoryDirective[] RepositoryDirectives
		{
			get
			{
				return m_repositories;
			}
			
		}
		/// <summary> Return the set of fileset directives.
		/// 
		/// </summary>
		/// <returns> the fileset directives
		/// </returns>
		public FilesetDirective[] Filesets
		{
			get
			{
				return m_filesets;
			}
			
			/// <summary> Return an array of files corresponding to the expansion 
			/// of the filesets declared within the directive.
			/// 
			/// </summary>
			/// <param name="base">the base directory against which relative 
			/// file references will be resolved
			/// </param>
			/// <returns> the classpath
			/// </returns>
			/*
			public File[] expandFileSetDirectives( File base ) throws IOException
			{
			ArrayList list = new ArrayList();
			
			//
			// expand relative to fileset
			//
			
			FilesetDirective[] filesets = getFilesets();
			
			for( int i=0; i<filesets.length; i++ )
			{
			FilesetDirective fileset = filesets[i];
			File anchor = getDirectory( base, fileset.getBaseDirectory() );
			IncludeDirective[] includes = fileset.getIncludes();
			if( includes.length > 0 )
			{
			for( int j=0; j<includes.length; j++ )
			{
			File file = new File( anchor, includes[j].getPath() );
			list.add( file );
			}
			}
			else
			{
			list.add( anchor );
			}
			}
			
			return (File[]) list.toArray( new File[0] );
			}
			
			private File getDirectory( File base, String path ) throws IOException
			{
			File file = new File( path );
			if( file.isAbsolute() )
			{
			return verifyDirectory( file );
			}
			return verifyDirectory( new File( base, path ) );
			}
			
			private File verifyDirectory( File dir ) throws IOException
			{
			if( dir.isDirectory() )
			{
			return dir.getCanonicalFile();
			}
			
			final String error = 
			"Path does not correspond to a directory: " + dir;
			throw new IOException( error );
			}
			*/
			
		}
		private static readonly FilesetDirective[] EMPTY_FILESETS;
		private static readonly RepositoryDirective[] EMPTY_REPOSITORIES;
		
		/// <summary> The fileset directives</summary>
		private FilesetDirective[] m_filesets;
		
		/// <summary> The resource references</summary>
		private RepositoryDirective[] m_repositories;
		
		/// <summary> Create a empty ClasspathDirective.</summary>
		public ClasspathDirective():this(null, null)
		{
		}
		
		/// <summary> Create a ClasspathDirective instance.
		/// 
		/// </summary>
		/// <param name="filesets">the filesets to be included in a classloader
		/// </param>
		/// <param name="repositories">the repositories directives to be included in a classloader
		/// </param>
		public ClasspathDirective(FilesetDirective[] filesets, RepositoryDirective[] repositories)
		{
			if (filesets == null)
			{
				m_filesets = EMPTY_FILESETS;
			}
			else
			{
				m_filesets = filesets;
			}
			if (repositories == null)
			{
				m_repositories = EMPTY_REPOSITORIES;
			}
			else
			{
				m_repositories = repositories;
			}
		}
		static ClasspathDirective()
		{
			EMPTY_FILESETS = new FilesetDirective[0];
			EMPTY_REPOSITORIES = new RepositoryDirective[0];
		}
	}
}