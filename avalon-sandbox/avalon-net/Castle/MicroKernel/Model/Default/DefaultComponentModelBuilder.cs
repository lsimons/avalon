// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.MicroKernel.Model.Default
{
	using System;
	using System.Collections;
	using System.Reflection;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for DefaultComponentModelBuilder.
	/// </summary>
	public class DefaultComponentModelBuilder : IComponentModelBuilder
	{
		private Kernel m_kernel;

		public DefaultComponentModelBuilder(Kernel kernel)
		{
			m_kernel = kernel;
		}

		#region IComponentModelBuilder Members

		public IComponentModel BuildModel(String key, Type service, Type implementation)
		{
			// TODO: This code sucks. Refactor it!

			ArrayList dependencies = new ArrayList();

			ConstructorInfo constructor = InspectConstructors(implementation, dependencies);
			PropertyInfo[] properties = InspectSetMethods(service, dependencies);
			InspectAvalonAttributes( implementation, dependencies );

			IDependencyModel[] dependenciesArray = 
				(IDependencyModel[]) dependencies.ToArray( typeof(IDependencyModel) );

			IConstructionModel constructionModel = 
				new DefaultConstructionModel( implementation, constructor, properties );

			// TODO: Consoler. Context and Configuration should be created by 
			//   a separated entity - how to reach it? Kernel?

			DefaultComponentModel model = new DefaultComponentModel( 
				service, 
				Avalon.Framework.Lifestyle.Transient, 
				new ConsoleLogger( service.Name, LoggerLevel.Debug ), 
				new DefaultConfiguration(), 
				new DefaultContext(), 
				dependenciesArray, 
				constructionModel );

			return model;
		}

		#endregion

		protected void InspectAvalonAttributes( Type implementation, IList dependencies )
		{
			if (!implementation.IsDefined( typeof(AvalonComponentAttribute), false ))
			{
				return;
			}

			AvalonComponentAttribute componentAttribute = GetComponentAttribute( implementation );
			AvalonLoggerAttribute loggerAttribute = GetLoggerAttribute( implementation );
			AvalonDependencyAttribute[] dependencyAttributes = GetDependencyAttributes( implementation );

			foreach( AvalonDependencyAttribute dependency in dependencyAttributes )
			{
				AddDependency( dependencies, dependency.DependencyType, 
					dependency.Key, dependency.IsOptional );
			}
		}

		protected AvalonComponentAttribute GetComponentAttribute( Type implementation )
		{
			return (AvalonComponentAttribute) GetAttribute( implementation, typeof( AvalonComponentAttribute ) );
		}

		protected AvalonDependencyAttribute[] GetDependencyAttributes( Type implementation )
		{
			return (AvalonDependencyAttribute[]) GetAttributes( implementation, typeof( AvalonDependencyAttribute ) );
		}

		protected AvalonLoggerAttribute GetLoggerAttribute( Type implementation )
		{
			return (AvalonLoggerAttribute) GetAttribute( implementation, typeof( AvalonLoggerAttribute ) );
		}

		protected object GetAttribute( Type implementation, Type attribute )
		{
			object[] attrs = implementation.GetCustomAttributes( attribute, false );

			if (attrs.Length != 0)
			{
				return attrs[0];
			}

			return null;
		}

		protected object[] GetAttributes( Type implementation, Type attribute )
		{
			return implementation.GetCustomAttributes( attribute, false );
		}

		protected ConstructorInfo InspectConstructors( Type implementation, IList dependencies )
		{
			ConstructorInfo constructor = null;

			ConstructorInfo[] constructors = implementation.GetConstructors();

			// TODO: Try to sort the array 
			// by the arguments lenght in descendent order

			foreach(ConstructorInfo item in constructors)
			{
				if (IsEligible( item ))
				{
					constructor = item;

					ParameterInfo[] parameters = constructor.GetParameters();

					foreach(ParameterInfo parameter in parameters)
					{
						if (!parameter.ParameterType.IsInterface)
						{
							continue;
						}

						AddDependency( dependencies, parameter.ParameterType );
					}

					break;
				}
			}

			if ( constructor == null )
			{
				throw new ModelBuilderException( 
					String.Format("Handler could not find an eligible constructor for type {0}", 
					implementation.FullName) );
			}

			return constructor;
		}

		protected PropertyInfo[] InspectSetMethods( Type service, IList dependencies )
		{
			ArrayList selected = new ArrayList();

			PropertyInfo[] properties = service.GetProperties();

			foreach(PropertyInfo property in properties)
			{
				if (IsEligible( property ))
				{
					AddDependency( dependencies, property.PropertyType );
					selected.Add( property );
				}
			}

			return (PropertyInfo[]) selected.ToArray( typeof(PropertyInfo) );
		}

		protected bool IsEligible( PropertyInfo property )
		{
			// TODO: An attribute could say to use
			// that the property is optional.

			if (!property.CanWrite || !property.PropertyType.IsInterface)
			{
				return false;
			}

			return true;
		}

		protected bool IsEligible( ConstructorInfo constructor )
		{
			ParameterInfo[] parameters = constructor.GetParameters();

			foreach(ParameterInfo parameter in parameters)
			{
				if (parameter.ParameterType == typeof(String) &&
					!parameter.Name.Equals("contextdir")) // Just a sample
				{
					return false;
				}
				if (!parameter.ParameterType.IsInterface)
				{
					return false;
				}
			}

			return true;
		}

		protected virtual void AddDependency( IList dependencies, Type type, String key, bool optional )
		{
			if (type == typeof(ILogger) || type == typeof(IContext) || 
				type == typeof(IConfiguration) || type == typeof(ILookupManager))
			{
				return;
			}

			DefaultDependencyModel dependecy = new DefaultDependencyModel( type, key, optional );

			if (!dependencies.Contains( dependecy ))
			{
				dependencies.Add( dependecy );
			}
		}

		protected virtual void AddDependency( IList dependencies, Type type )
		{
			AddDependency( dependencies, type, String.Empty, false );
		}
	}
}
