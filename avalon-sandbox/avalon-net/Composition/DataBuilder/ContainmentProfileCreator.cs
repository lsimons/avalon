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

namespace Apache.Avalon.Composition.Data.Builder
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;

	/// <summary>
	/// Summary description for ContainmentProfileCreator.
	/// </summary>
	public class ContainmentProfileCreator : ProfileCreator
	{
		private static ComponentProfileCreator COMPONENT_CREATOR = new ComponentProfileCreator();

		private static TargetsCreator TARGETS_CREATOR = new TargetsCreator();

		public ContainmentProfileCreator()
		{
		}

		public ContainmentProfile CreateContainmentProfile( IConfiguration config )
		{
			//
			// build the containment description 
			//

			String name = GetName( null, config, "untitled" );

			ServiceDirective[] exports = null;
			// 	CreateServiceDirectives( config.GetChild( "services", false ) );

			//
			// check for any legacy "implementation" tags and if it exists
			// then run with it, otherwise continue with the container defintion
			//

			IConfiguration implementation = config;

			TypeLoaderDirective typeloader = CreateTypeLoaderDirective( 
				implementation.GetChild( "typeloader", false ) );

			//
			// build any logging category directives
			// 

			CategoriesDirective categories = 
				GetCategoriesDirective( implementation.GetChild( "categories", false ), name );

			//
			// build nested profiles
			// 

			ComponentProfile[] profiles = CreateProfiles( implementation );

			//
			// return the containment profile
			// 

			return new ContainmentProfile( name, typeloader, exports, categories, profiles );
		}

		private TypeLoaderDirective CreateTypeLoaderDirective( IConfiguration config )
		{
			if( config == null )
			{
				return null;
			}

			LibraryDirective library = 
				CreateLibraryDirective( config.GetChild( "library", false ) );
			ClasspathDirective classpath = 
				CreateClasspathDirective( config.GetChild( "classpath", false ) );
			return new TypeLoaderDirective( library, classpath );
		}

		private ClasspathDirective CreateClasspathDirective( IConfiguration config )
		{
			if( config == null )
			{
				return null;
			}

			FilesetDirective[] filesets = CreateFilesetDirectives( config );
			RepositoryDirective[] repositories = CreateRepositoryDirectives( config );
			return new ClasspathDirective( filesets, repositories );
		}

		private LibraryDirective CreateLibraryDirective( IConfiguration config )
		{
			if( config == null )
			{
				return null;
			}

			ConfigurationCollection includes = config.GetChildren( "include" );
			String[] inc = new String[ includes.Count ];
			int i=0;
			foreach( IConfiguration conf in includes )
			{
				inc[i++] = GetIncludeValue( conf );
			}

			ConfigurationCollection groups = config.GetChildren( "group" );
			String[] grp = new String[ groups.Count ];
			i=0;
			foreach( IConfiguration conf in groups )
			{
				grp[i++] = conf.Value;
			}

			return new LibraryDirective( inc, grp );
		}

		private RepositoryDirective[] CreateRepositoryDirectives( IConfiguration config )
		{
			if( config == null )
			{
				throw new ArgumentNullException( "config" );
			}

			ConfigurationCollection children = config.GetChildren( "repository" );
			RepositoryDirective[] repositories = new RepositoryDirective[ children.Count ];
			int i=0;
			foreach( IConfiguration conf in children )
			{
				ResourceDirective[] resources = CreateResourceDirectives( conf );
				repositories[i++] = new RepositoryDirective( resources );
			}
			return repositories;
		}

		private ResourceDirective[] CreateResourceDirectives( IConfiguration config )
		{
			if( config == null )
			{
				throw new ArgumentNullException( "config" );
			}

			ArrayList res = new ArrayList();
			ConfigurationCollection resources = config.GetChildren( "resource" );
			foreach( IConfiguration resource in resources )
			{
				res.Add( CreateResourceDirective( resource ) );
			}

			return (ResourceDirective[]) res.ToArray( typeof(ResourceDirective) );
		}

		private ResourceDirective CreateResourceDirective( IConfiguration config )
		{
			String id = (String) config.GetAttribute( "id", null );
			String type = (String) config.GetAttribute( "type", null );
			if( type == null )
			{
				return ResourceDirective.CreateResourceDirective( id );
			}
			else
			{
				return ResourceDirective.CreateResourceDirective( id, type );
			}
		}

		private FilesetDirective[] CreateFilesetDirectives( IConfiguration config )
		{
			ArrayList list = new ArrayList();
			ConfigurationCollection children = config.GetChildren( "fileset" );
			foreach( IConfiguration conf in children )
			{
				list.Add( CreateFilesetDirective( conf ) );
			}
			return (FilesetDirective[]) list.ToArray( typeof(FilesetDirective) );
		}

		/// <summary>
		/// Utility method to create a new fileset descriptor from a
		/// configuration instance.
		/// </summary>
		/// <param name="config"> a configuration defining the fileset</param>
		/// <returns>the fileset descriptor</returns>
		public FilesetDirective CreateFilesetDirective( IConfiguration config )
		{
			String baseDir = (String) config.GetAttribute( "dir", "." );
			IncludeDirective[] includes = CreateIncludeDirectives( config );
			return new FilesetDirective( baseDir, includes );
		}

		/// <summary>
		/// Utility method to create a set in include directives.
		/// </summary>
		/// <param name="config">a configuration defining the fileset</param>
		/// <returns>the includes</returns>
		protected IncludeDirective[] CreateIncludeDirectives( IConfiguration config )
		{
			if( config == null )
			{
				return new IncludeDirective[0];
			}

			ArrayList list = new ArrayList();
			ConfigurationCollection children = config.GetChildren( "include" );
			foreach( IConfiguration conf in children )
			{
				list.Add( CreateIncludeDirective( conf ) );
			}

			return (IncludeDirective[]) list.ToArray( typeof(IncludeDirective) );
		}

		/// <summary>
		/// Utility method to create a new include directive from a
		/// configuration instance.
		/// </summary>
		/// <param name="config"> a configuration defining the include directive</param>
		/// <returns>the include directive</returns>
		protected IncludeDirective CreateIncludeDirective( IConfiguration config )
		{
			return new IncludeDirective( GetIncludeValue( config ) );
		}

		private String GetIncludeValue( IConfiguration config ) 
		{
			if( config.GetAttribute( "path", null ) != null )
			{
				return (String) config.GetAttribute( "path", null );
			}
			else if( config.GetAttribute( "name", null ) != null )
			{
				return (String) config.GetAttribute( "name", null );
			}
			else
			{
				return config.Value;
			}
		}

		/// <summary>
		/// Return the set of profiles embedded in the supplied 
		/// configuration.
		/// </summary>
		/// <param name="config"> a container or implementation configutation</param>
		/// <returns>the set of profile</returns>
		protected ComponentProfile[] CreateProfiles( IConfiguration config )
		{
			ArrayList list = new ArrayList();
			ConfigurationCollection children = config.Children;
			foreach( IConfiguration child in children )
			{
				if( !child.Name.Equals( "typeloader" ) )
				{
					if( child.Name.Equals( "container" ) )
					{
						list.Add( CreateContainmentProfile( child ) );
					}
					else if( child.Name.Equals( "component" ) )
					{
						if( child.GetAttribute( "profile", null ) != null )
						{
							list.Add( CreateNamedComponentProfile( child ) );
						}
						else
						{                    
							list.Add( 
								COMPONENT_CREATOR.CreateComponentProfile( child ) );
						}
					}
					else if( child.Name.Equals( "include" ) )
					{
						list.Add( CreateFromInclude( child ) );
					}
				}
			}
			return (ComponentProfile[]) list.ToArray( typeof(ComponentProfile) );
		}

		/// <summary>
		/// Create a profile using a packaged deployment profile.
		/// </summary>
		/// <param name="config">the component configuration</param>
		/// <returns>the named profile</returns>
		private NamedComponentProfile CreateNamedComponentProfile( IConfiguration config )
		{
			String name = (String) config.GetAttribute( "name", null );
			String classname = (String) config.GetAttribute( "class", null );
			String key = (String) config.GetAttribute( "profile", null );
			ActivationPolicy activation = GetActivationPolicy( config ); 
			return new NamedComponentProfile( name, classname, key, activation );
		}

		/// <summary>
		/// Resolve the logical services declared by a block directive.
		/// </summary>
		/// <param name="config"> the services configuration fragment</param>
		/// <returns>the set of declared service descriptors</returns>
		public ServiceDirective[] CreateServiceDirectives( IConfiguration config )
		{
			/*
			if( config == null )
			{
				return new ServiceDirective[0];
			}

			ConfigurationCollection children = config.GetChildren( "service" );
			ArrayList list = new ArrayList();
			foreach( IConfiguration child in children )
			{
				list.Add( CreateServiceDirective( child ) );
			}
			return (ServiceDirective[]) list.ToArray( typeof(ServiceDirective) );
			*/
			return null;
		}

		/// <summary>
		/// Resolve a service directive declared by a block directive.
		/// </summary>
		/// <param name="config">the service configuration fragment</param>
		/// <returns>the set of declared services directives</returns>
		private ServiceDirective CreateServiceDirective( IConfiguration config )
		{
			/*
			try
			{
				ServiceDescriptor service = TYPE_CREATOR.buildService( config );
				IConfiguration source = config.GetChild( "source", false );
				if( source == null ) 
				{
					String error = 
						"Service configuration must contain a source directive.";
					throw new MetaDataException( error );
				}
				String path = source.Value;
				return new ServiceDirective( service, path );
			}
			catch( Exception ce )
			{
				String error =
					"Invalid service declaration in block specification:\n";
					// + ConfigurationUtil.list( config );
				throw new MetaDataException( error, ce );
			}*/
			return null;
		}

		/// <summary>
		/// Create a containment defintion for an include statement. Two variant
		/// of include are supported - include by resource reference, and include
		/// of a source container defintion.
		/// </summary>
		/// <param name="config">the include description</param>
		/// <returns>the containment directive</returns>
		private ComponentProfile CreateFromInclude( IConfiguration config )
		{
			String name = GetBlockIncludeName( config );
			if( config.GetAttribute( "id", null ) != null )
			{
				ResourceDirective resource = CreateResourceDirective( config );
				TargetDirective[] targets = CreateTargetDirectives( config );
				return new BlockCompositionDirective( name, resource, targets );
			}
			else
			{
				String path = GetBlockIncludePath( config );
				return new BlockIncludeDirective( name, path );
			}
		}

		private TargetDirective[] CreateTargetDirectives( IConfiguration config )
		{
			try
			{
				Targets targets = TARGETS_CREATOR.CreateTargets( config );
				return targets.getTargets();
			}
			catch( Exception e )
			{
				String error = 
					"Unexpected error while attempting to build target directives.";
				throw new MetaDataException( error, e );
			}
		}

		private String GetBlockIncludeName( IConfiguration config ) 
		{
			try
			{
				return (String) config.GetAttribute( "name", String.Empty );
			}
			catch( ConfigurationException e )
			{
				String error =
					"Missing 'name' attribute in the block include statement:\n";
				//+ ConfigurationUtil.list( config );
				throw new MetaDataException( error, e );
			}
		}

		private String GetBlockIncludePath( IConfiguration config ) 
		{
			try
			{
				IConfiguration source = config.GetChild( "source", false );
				if( null == source )
				{
					String error =
					"Missing 'source' element in the block include statement:\n";
						// + ConfigurationUtil.list( config );
					throw new MetaDataException( error );
				}
				return source.Value;
			}
			catch( ConfigurationException e )
			{
				String error =
					"Missing source value in the block include statement:\n";
				// + ConfigurationUtil.list( config );
				throw new MetaDataException( error, e );
			}
		}
	}
}
