// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

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
