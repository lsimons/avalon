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

namespace Apache.Avalon.Composition.Data.Builder
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;

	/// <summary>
	/// Summary description for ComponentProfileCreator.
	/// </summary>
	public class ComponentProfileCreator : ProfileCreator
	{
		public ComponentProfileCreator()
		{
		}

		/// <summary>
		/// Creation of a ComponentProfile from an XML configuration.
		/// </summary>
		/// <param name="config">the configuration instance describing the component deployment scenario </param>
		/// <returns>the deployment profile</returns>
		public ComponentProfile CreateComponentProfile( IConfiguration config )
		{
			String typename = (String) config.GetAttribute( "type", null );
			if( null == typename )
			{
				// String c = IConfigurationUtil.list( config );
				String error = 
				"Missing 'type' attribute in component declaration:\n";// + c;
				throw new ConfigurationException( error );
			}
			return CreateComponentProfile( null, typename, config );
		}

		/// <summary>
		/// Creation of a ComponentProfile from an XML configuration.
		/// </summary>
		/// <param name="baseName">the default name</param>
		/// <param name="classname">the name of the class identifying the underlying component type</param>
		/// <param name="config">the configuration describing the component deployment scenario </param>
		/// <returns></returns>
		public ComponentProfile CreateComponentProfile( String baseName, 
			String classname, IConfiguration config )
		{
			String name = GetName( baseName, config, "untitled" );
			return CreateComponentProfile( classname, config, name );
		}

		/// <summary>
		/// Creation of a ComponentProfile from an XML configuration.
		/// </summary>
		/// <param name="classname">the name of the class identifying the underlying component type</param>
		/// <param name="config">the configuration describing the component deployment scenario </param>
		/// <param name="name"></param>
		/// <returns>the deployment profile</returns>
		public ComponentProfile CreateComponentProfile( 
			String classname, IConfiguration config, String name )
		{
			ActivationPolicy activation = GetActivationPolicy( config, ActivationPolicy.Lazy );
			CollectionPolicy collection = GetCollectionPolicy( config );

			CategoriesDirective categories = 
				GetCategoriesDirective( config.GetChild( "categories", false ), name );
			ContextDirective context = 
				GetContextDirective( config.GetChild( "context", false ) );
			DependencyDirective[] dependencies = 
				GetDependencyDirectives( config.GetChild( "dependencies", false ) );
			StageDirective[] stages = 
				GetStageDirectives( config.GetChild( "stages", false ) );
			// Parameters params = 
			// 	getParameters( config.GetChild( "parameters", false ) );
			IConfiguration configuration = 
				config.GetChild( "configuration", true );

			return new ComponentProfile( 
				name, activation, collection, classname, categories, context, dependencies, 
				stages, configuration, Mode.Explicit );
		}

		/// <summary>
		/// Get the collection policy from a configuration.  If the collection
		/// policy is not declared a null is returned indicating that the collection 
		/// policy shall default to the component type collection policy. 
		/// </summary>
		/// <param name="config">a configuration fragment holding a collection attribute</param>
		/// <returns>policy</returns>
		protected CollectionPolicy GetCollectionPolicy( IConfiguration config )
		{
			object collection = config.GetAttribute("collection", null);

			if ( collection == null )
			{
				return CollectionPolicy.Liberal;
			}

			return (CollectionPolicy) Enum.Parse( typeof(CollectionPolicy), 
				collection.ToString(), true ); 
		}

		protected DependencyDirective[] GetDependencyDirectives( IConfiguration config )
		{
			if( config != null )
			{
				ArrayList list = new ArrayList();
				ConfigurationCollection deps = config.GetChildren( "dependency" );
				foreach(IConfiguration dep in deps)
				{
					list.Add( GetDependencyDirective( dep ) );
				}
				return (DependencyDirective[]) list.ToArray( typeof(DependencyDirective) );
			}
			return new DependencyDirective[0];
		}

		protected DependencyDirective GetDependencyDirective( IConfiguration config )
		{
			String key = (String) config.GetAttribute( "key", null );
			String source = (String) config.GetAttribute( "source", null );
			if( source != null )
			{
				return new DependencyDirective( key, source );
			}
			else
			{
				ConfigurationCollection children = config.GetChildren( "select" );
				ArrayList list = new ArrayList();
				foreach( IConfiguration child in children )
				{
					list.Add( GetSelectionDirective( child ) );
				}
				SelectionDirective[] features = 
					(SelectionDirective[]) list.ToArray( typeof(SelectionDirective) );
				return new DependencyDirective( key, features );
			}
		}

		protected StageDirective[] GetStageDirectives( IConfiguration config )
		{
			if( config != null )
			{
				ArrayList list = new ArrayList();
				ConfigurationCollection deps = config.GetChildren( "stage" );
				foreach( IConfiguration dep in deps )
				{
					list.Add( GetStageDirective( dep ) );
				}
				return (StageDirective[]) list.ToArray( typeof(StageDirective) );
			}
			return new StageDirective[0];
		}

		protected StageDirective GetStageDirective( IConfiguration config )
		{
			String key = (String) config.GetAttribute( "key", null );
			String source = (String) config.GetAttribute( "source", null );
			if( source != null )
			{
				return new StageDirective( key, source );
			}
			else
			{
				ConfigurationCollection children = config.GetChildren( "select" );
				ArrayList list = new ArrayList();
				foreach( IConfiguration child in children )
				{
					list.Add( GetSelectionDirective( child ) );
				}
				SelectionDirective[] features = 
					(SelectionDirective[]) list.ToArray( typeof(SelectionDirective) );
				return new StageDirective( key, features );
			}
		}

		protected SelectionDirective GetSelectionDirective( IConfiguration config )
		{
			String feature = (String) config.GetAttribute( "feature", null );
			String value = (String) config.GetAttribute( "value", null );
			String match = (String) config.GetAttribute( "match", "required" );
			bool optional = (bool) config.GetAttribute( "optional", typeof(bool), false );
			return new SelectionDirective( feature, value, match, optional );
		}

		/*protected Parameters getParameters( IConfiguration config )
		throws ConfigurationException
		{
			if( config != null )
			{
				return Parameters.fromIConfiguration( config );
			}
			return null;
		}*/

		/// <summary>
		/// Utility method to create a new context directive.
		/// </summary>
		/// <param name="config">the context directive configuration</param>
		/// <returns> the context directive</returns>
		public ContextDirective GetContextDirective( IConfiguration config )
		{
			if( config == null )
			{
				return new ContextDirective( null );
			}

			String classname = (String) config.GetAttribute( "type", null );
			String source = (String) config.GetAttribute( "source", null );
			EntryDirective[] entries = GetEntries( config.GetChildren( "entry" ) );
			return new ContextDirective( classname, entries, source );
		}

		/// <summary>
		/// Utility method to create a set of entry directives.
		/// </summary>
		/// <param name="configs">configs the entry directive configurations</param>
		/// <returns>the entry directives</returns>
		protected EntryDirective[] GetEntries( ConfigurationCollection configs )
		{
			ArrayList list = new ArrayList();
			foreach( IConfiguration conf in configs )
			{
				String key = (String) conf.GetAttribute( "key", null );
				ConfigurationCollection children = conf.Children;
				if( children.Count != 1 )
				{
					String error = 
					"Entry '" + key + "' does not contain one child element.";
					throw new ConfigurationException( error );
				}

				IConfiguration child = children[0];
				String name = child.Name;
				if( name.Equals( "import" ) )
				{
					String importKey = (String) child.GetAttribute( "key", null );
					list.Add( new ImportDirective( key, importKey ) );
				}
				else if( name.Equals( "constructor" ) )
				{
					String classname = (String)
						child.GetAttribute( "type", typeof(String) );
					/*
					IConfiguration[] paramsConf = child.GetChildren( "param" );
					if( paramsConf.Length > 0 )
					{
						Parameter[] params = getParameters( paramsConf );
						ConstructorDirective constructor = 
						new ConstructorDirective( key, classname, params );
						list.add( constructor );
					}*/
					// else
					{
						ConstructorDirective constructor = 
						new ConstructorDirective( 
							key, classname, (String) child.Value );
						list.Add( constructor );
					}
				}
				else
				{
					String error = 
					"Entry child unrecognized: " + name;
					throw new ConfigurationException( error );
				}
			}

			return (EntryDirective[])list.ToArray( typeof(EntryDirective) );
		}

		/**
		* Utility method to create a set of parameter directive.
		*
		* @param configs the parameter directive configurations
		* @return the parameter directives
		* @throws ConfigurationException if an error occurs
		*/
		/*
		protected Parameter[] getParameters( IConfiguration[] configs )
		{
			ArrayList list = new ArrayList();
			for( int i = 0; i < configs.length; i++ )
			{
				Parameter parameter = getParameter( configs[ i ] );
				list.add( parameter );
			}
			return (Parameter[])list.toArray( new Parameter[ 0 ] );
		}*/

		/**
		* Utility method to create a new parameter directive.
		*
		* @param config the parameter directive configuration
		* @return the parameter directive
		* @throws ConfigurationException if an error occurs
		*/
		/*
		protected Parameter getParameter( IConfiguration config )
		{
			String classname = config.getAttribute( "class", "java.lang.String" );
			IConfiguration[] params = config.GetChildren( "param" );
			if( params.length == 0 )
			{
				String value = config.getValue( null );
				return new Parameter( classname, value );
			}
			else
			{
				Parameter[] parameters = getParameters( params );
				return new Parameter( classname, parameters );
			}
		}*/
	}
}
