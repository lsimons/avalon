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

namespace Apache.Avalon.Meta.Builder
{
	using System;
	using System.Reflection;
	using System.Collections;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for TypeMetaInformation.
	/// </summary>
	public sealed class TypeMetaInformation
	{
		private Type m_type;

		private AvalonComponentAttribute m_component;
		private AvalonContextAttribute m_context;
		private AvalonLoggerAttribute m_logger;
		private AvalonConfigurationAttribute m_configuration;

		private ArrayList m_services   = new ArrayList();
		private ArrayList m_stages     = new ArrayList();
		private ArrayList m_extensions = new ArrayList();
		private ArrayList m_entries    = new ArrayList();
		private ArrayList m_dependency = new ArrayList();

		private Hashtable m_attribute2Info = new Hashtable();

		private TypeMetaInformation( Type target )
		{
			m_type = target;

			QueryMainAttributes( target );
		}

		/// <summary>
		/// Constructs TypeMetaInformation gathering information about
		/// the specified type
		/// </summary>
		/// <param name="target">Type to be inspected.</param>
		/// <returns>An immutable TypeMetaInformation</returns>
		public static TypeMetaInformation Build( Type target )
		{
			return new TypeMetaInformation( target );
		}

		private void QueryMainAttributes( Type target )
		{
			object[] attributes = target.GetCustomAttributes( true );

			InspectAttributes( target, attributes );
			
			ConstructorInfo[] constructors = 
				target.GetConstructors( BindingFlags.Public|BindingFlags.Instance );

			foreach( ConstructorInfo info in constructors )
			{
				attributes = info.GetCustomAttributes( true );
				InspectAttributes( info, attributes );
			}

			PropertyInfo[] properties = 
				target.GetProperties( BindingFlags.Public|BindingFlags.SetProperty|BindingFlags.Instance );

			foreach( PropertyInfo info in properties )
			{
				attributes = info.GetCustomAttributes( true );
				InspectAttributes( info, attributes );
			}

			MethodInfo[] methods = 
				target.GetMethods( BindingFlags.Public|BindingFlags.Instance );

			foreach( MethodInfo info in methods )
			{
				attributes = info.GetCustomAttributes( true );
				InspectAttributes( info, attributes );
			}
		}

		private void InspectAttributes(object source, object[] attributes)
		{
			if (attributes == null || attributes.Length == 0)
			{
				return;
			}

			foreach(object attribute in attributes)
			{
				m_attribute2Info.Add( attribute, source );

				if (attribute is AvalonComponentAttribute)
				{
					if (m_component != null)
					{
						throw new Exception("Can't have more than one AvalonComponentAttribute declared");
					}
					m_component = (AvalonComponentAttribute) attribute;
				}
				else if (attribute is AvalonDependencyAttribute)
				{
					m_dependency.Add(attribute);
				}
				else if (attribute is AvalonServiceAttribute)
				{
					m_services.Add(attribute);
				}
				else if (attribute is AvalonStageAttribute)
				{
					m_stages.Add(attribute);
				}
				else if (attribute is AvalonExtensionAttribute)
				{
					m_extensions.Add(attribute);
				}
				else if (attribute is AvalonLoggerAttribute)
				{
					if (m_logger != null)
					{
						throw new Exception("Can't have more than one AvalonLoggerAttribute declared");
					}
					m_logger = (AvalonLoggerAttribute) attribute;
				}
				else if (attribute is AvalonConfigurationAttribute)
				{
					if (m_configuration != null)
					{
						throw new Exception("Can't have more than one AvalonConfigurationAttribute declared");
					}
					m_configuration = (AvalonConfigurationAttribute) attribute;
				}
				else if (attribute is AvalonContextAttribute)
				{
					if (m_context != null)
					{
						throw new Exception("Can't have more than one AvalonContextAttribute declared");
					}
					m_context = (AvalonContextAttribute) attribute;
				}
				else if (attribute is AvalonEntryAttribute)
				{
					m_entries.Add(attribute);
				}
			}
		}

		public AvalonComponentAttribute ComponentAttribute
		{
			get
			{
				return m_component;
			}
		}

		public AvalonConfigurationAttribute ConfigurationAttribute
		{
			get
			{
				return m_configuration;
			}
		}

		public AvalonLoggerAttribute LoggerAttribute
		{
			get
			{
				return m_logger;
			}
		}

		public AvalonContextAttribute ContextAttribute
		{
			get
			{
				return m_context;
			}
		}

		public AvalonDependencyAttribute[] DependenciesAttribute
		{
			get
			{
				return (AvalonDependencyAttribute[]) 
					m_dependency.ToArray( typeof(AvalonDependencyAttribute) );
			}
		}

		public AvalonEntryAttribute[] EntriesAttribute
		{
			get
			{
				return (AvalonEntryAttribute[]) 
					m_entries.ToArray( typeof(AvalonEntryAttribute) );
			}
		}

		public AvalonStageAttribute[] StagesAttribute
		{
			get
			{
				return (AvalonStageAttribute[]) 
					m_stages.ToArray( typeof(AvalonStageAttribute) );
			}
		}

		public AvalonExtensionAttribute[] ExtensionsAttribute
		{
			get
			{
				return (AvalonExtensionAttribute[]) 
					m_extensions.ToArray( typeof(AvalonExtensionAttribute) );
			}
		}

		public AvalonServiceAttribute[] ServicesAttribute
		{
			get
			{
				return (AvalonServiceAttribute[]) 
					m_services.ToArray( typeof(AvalonServiceAttribute) );
			}
		}

		public Type TargetType
		{
			get
			{
				return m_type;
			}
		}

		public MemberInfo this [ Object attribute ]
		{
			get
			{
				return (MemberInfo) m_attribute2Info[ attribute ];
			}
		}
	}
}
