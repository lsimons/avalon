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
	/// Summary description for ProfileCreator.
	/// </summary>
	public abstract class ProfileCreator
	{
		public ProfileCreator()
		{
		}

		/// <summary>
		/// Get the profile name.
		/// </summary>
		/// <param name="baseName"></param>
		/// <param name="config">a configuration fragment describing the profile.</param>
		/// <param name="defaultName"></param>
		/// <returns></returns>
		protected String GetName( String baseName, IConfiguration config, String defaultName )
		{
			String name = (String) config.GetAttribute( "name", defaultName );

			if( baseName == null )
			{
				return name;
			}
			else
			{
				return baseName + "-" + name; 
			}
		}

		/// <summary>
		/// Get the activation policy from a configuration. If no activation attribute
		/// is present the value return defaults to FALSE (i.e. activation is deferred).
		/// </summary>
		/// <param name="config">a configuration fragment holding a activation attribute</param>
		/// <returns>is the value of the activation attribute is 'startup'
		/// otherwise the return value is FALSE</returns>
		protected ActivationPolicy GetActivationPolicy( IConfiguration config )
		{
			return GetActivationPolicy( config, ActivationPolicy.Lazy );
		}

		/// <summary>
		/// Get the activation policy from a configuration. 
		/// </summary>
		/// <param name="config">a configuration fragment holding a activation attribute</param>
		/// <param name="fallback">the default policy</param>
		/// <returns>activation policy</returns>
		protected ActivationPolicy GetActivationPolicy( IConfiguration config, ActivationPolicy fallback )
		{
			String value = (String) config.GetAttribute( "activation", null );
			
			if( value == null )
			{
				return fallback;
			}

			value = value.ToLower();

			if( value.Equals( "startup" ) || value.Equals( "true" ) )
			{
				return ActivationPolicy.Startup;
			}
			else if( value.Equals( "lazy" ) || value.Equals( "false" ) )
			{
				return ActivationPolicy.Lazy;
			}
			else
			{
				return fallback ;
			}
		}

		public CategoriesDirective GetCategoriesDirective( 
			IConfiguration config, String name )
		{
			if( config != null )
			{
				String priority = (String) config.GetAttribute( "priority", null );
				String target = (String) config.GetAttribute( "target", null );
				CategoryDirective[] categories = 
					GetCategoryDirectives( config.GetChildren( "category" ) );
				return new CategoriesDirective( name, priority, target, categories );
			}
			return null;
		}

		private CategoryDirective[] GetCategoryDirectives( ConfigurationCollection children )
		{
			ArrayList list = new ArrayList();
			foreach( IConfiguration config in children )
			{
				CategoryDirective category = GetCategoryDirective( config );
				list.Add( category );
			}
			return (CategoryDirective[]) list.ToArray( typeof(CategoryDirective) );
		}

		public CategoryDirective GetCategoryDirective( IConfiguration config )
		{
			try
			{
				String name = (String) config.GetAttribute( "name", null );
				String priority = (String) config.GetAttribute( "priority", null );
				String target = (String) config.GetAttribute( "target", null );
				return new CategoryDirective( name, priority, target );
			}
			catch( ConfigurationException e )
			{
				String error = 
					"Invalid category descriptor.";
						// + ConfigurationUtil.list( config );
				throw new ConfigurationException( error, e );
			}
		}
	}
}
