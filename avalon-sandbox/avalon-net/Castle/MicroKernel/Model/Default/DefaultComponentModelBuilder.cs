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
			AssertUtil.ArgumentNotNull( key, "key" );
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( implementation, "implementation" );

			ComponentData data = new ComponentData(implementation);

			InspectConstructors(data);
			InspectSetMethods(service, data);
			InspectAvalonAttributes(data);

			IConstructionModel constructionModel = 
				new DefaultConstructionModel( implementation, data.Constructor, data.PropertiesInfo );

			DefaultComponentModel model = new DefaultComponentModel( 
				data.Name,
				service, 
				data.SupportedLifestyle, 
				new ConsoleLogger( service.Name, LoggerLevel.Debug ), 
				new DefaultConfiguration(), 
				new DefaultContext(), 
				data.DependencyModel, 
				constructionModel );

			return model;
		}

		#endregion

		protected void InspectAvalonAttributes( ComponentData componentData )
		{
			if (!componentData.Implementation.IsDefined( typeof(AvalonComponentAttribute), false ))
			{
				return;
			}

			componentData.AvalonComponent = GetComponentAttribute( componentData.Implementation );
			componentData.AvalonLogger = GetLoggerAttribute( componentData.Implementation );
			AvalonDependencyAttribute[] dependencyAttributes = GetDependencyAttributes( componentData.Implementation );

			foreach( AvalonDependencyAttribute dependency in dependencyAttributes )
			{
				AddDependency( componentData.Dependencies, dependency.DependencyType, 
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

		protected void InspectConstructors( ComponentData componentData )
		{
			ConstructorInfo constructor = null;

			ConstructorInfo[] constructors = componentData.Implementation.GetConstructors();

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

						AddDependency( componentData.Dependencies, parameter.ParameterType );
					}

					break;
				}
			}

			if ( constructor == null )
			{
				throw new ModelBuilderException( 
					String.Format("Handler could not find an eligible constructor for type {0}", 
					componentData.Implementation.FullName) );
			}

			componentData.Constructor = constructor;
		}

		protected void InspectSetMethods( Type service, ComponentData componentData )
		{
			PropertyInfo[] properties = service.GetProperties();

			foreach(PropertyInfo property in properties)
			{
				if (IsEligible( property ))
				{
					AddDependency( componentData.Dependencies, property.PropertyType );
					componentData.Properties.Add( property );
				}
			}
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

	/// <summary>
	/// Holds a component data during inspect phase.
	/// </summary>
	public class ComponentData
	{
		private ArrayList m_dependencies = new ArrayList();
		private ArrayList m_properties = new ArrayList();
		private ConstructorInfo m_constructor;
		private AvalonComponentAttribute m_componentAttribute;
		private AvalonLoggerAttribute m_loggerAttribute;
		private Type m_implementation;

		public ComponentData( Type implementation )
		{
			this.m_implementation = implementation;
		}

		public Type Implementation
		{
			get
			{
				return m_implementation;
			}
		}

		public IList Dependencies
		{
			get
			{
				return m_dependencies;
			}
		}

		public IDependencyModel[] DependencyModel
		{
			get
			{
				return (IDependencyModel[]) m_dependencies.ToArray( typeof(IDependencyModel) );
			}
		}

		public ConstructorInfo Constructor
		{
			get
			{
				return m_constructor;
			}
			set
			{
				m_constructor = value;
			}
		}

		public IList Properties
		{
			get
			{
				return m_properties;
			}
		}

		public PropertyInfo[] PropertiesInfo
		{
			get
			{
				return (PropertyInfo[]) m_properties.ToArray( typeof(PropertyInfo) );
			}
		}

		public AvalonComponentAttribute AvalonComponent
		{
			get
			{
				return m_componentAttribute;
			}
			set
			{
				m_componentAttribute = value;
			}
		}

		public AvalonLoggerAttribute AvalonLogger
		{
			get
			{
				return m_loggerAttribute;
			}
			set
			{
				m_loggerAttribute = value;
			}
		}

		public Apache.Avalon.Framework.Lifestyle SupportedLifestyle
		{
			get
			{
				if (AvalonComponent == null)
				{
					return Apache.Avalon.Framework.Lifestyle.Transient;
				}

				return AvalonComponent.Lifestyle;
			}
		}

		public String Name
		{
			get
			{
				if (AvalonComponent == null)
				{
					return Implementation.Name;
				}

				return AvalonComponent.Name;
			}
		}
	}
}
