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

namespace Apache.Avalon.Meta
{
	using System;
	using Apache.Avalon.Framework;
	
	/// <summary> This class contains the meta information about a particular
	/// component type. It describes;
	/// <ul>
	/// <li>Human presentable meta data such as name, version, description etc
	/// useful when assembling the system.</li>
	/// <li>the context object capabilities that this component requires</li>
	/// <li>the services that this component type is capable of providing</li>
	/// <li>the services that this component type requires to operate (and the
	/// names via which services are accessed)</li>
	/// <li>extended lifecycle stages that this component uses</li>
	/// </ul>
	/// </summary>
	[Serializable]
	public class TypeDescriptor
	{
		private InfoDescriptor m_descriptor;
		private ContextDescriptor m_context;
		private IConfiguration m_configuration;
		private ServiceDescriptor[] m_services;
		private DependencyDescriptor[] m_dependencies;
		private CategoryDescriptor[] m_loggers;
		private StageDescriptor[] m_stages;
		private ExtensionDescriptor[] m_extensions;
		
		/// <summary> Creation of a new Type instance using a supplied component descriptor,
		/// logging, cotext, services, depedencies, stages and extension descriptors.
		/// </summary>
		/// <param name="descriptor">a component descriprot that contains information about
		/// the component type
		/// </param>
		/// <param name="loggers">a set of logger descriptors the declare the logging channels
		/// required by the type
		/// </param>
		/// <param name="context">a component context descriptor that declares the context type
		/// and context entry key and value classnames
		/// </param>
		/// <param name="services">a set of service descriptors that detail the service that
		/// this component type is capable of supplying
		/// </param>
		/// <param name="dependencies">a set of depedency descriptors that detail the service
		/// that this component type is depedent on
		/// </param>
		/// <param name="stages">a set of stage descriprors that detail the extension stage
		/// interfaces that this component requires a handler for
		/// </param>
		/// <param name="extensions">a set of lifecycle extension capabilities that this
		/// componet can provide to its container during the process of stage
		/// suppier resolution
		/// </param>
		/// <exception cref=""> NullPointerException if the descriptor, loggers, context, services,
		/// dependencies, stages, or extensions argument is null
		/// @since 1.1
		/// </exception>
		public TypeDescriptor(InfoDescriptor descriptor, CategoryDescriptor[] loggers, ContextDescriptor context, ServiceDescriptor[] services, DependencyDescriptor[] dependencies, StageDescriptor[] stages, ExtensionDescriptor[] extensions):this(descriptor, loggers, context, services, dependencies, stages, extensions, null)
		{
		}
		
		/// <summary> Creation of a new Type instance using a supplied component descriptor,
		/// logging, cotext, services, depedencies, stages and extension descriptors.
		/// </summary>
		/// <param name="descriptor">a component descriprot that contains information about
		/// the component type
		/// </param>
		/// <param name="loggers">a set of logger descriptors the declare the logging channels
		/// required by the type
		/// </param>
		/// <param name="context">a component context descriptor that declares the context type
		/// and context entry key and value classnames
		/// </param>
		/// <param name="services">a set of service descriprors that detail the service that
		/// this component type is capable of supplying
		/// </param>
		/// <param name="dependencies">a set of depedency descriprors that detail the service
		/// that this component type is depedent on
		/// </param>
		/// <param name="stages">a set of stage descriprors that detail the extensiuon stage
		/// interfaces that this component requires a handler for
		/// </param>
		/// <param name="extensions">a set of lifecycle extension capabilities that this
		/// componet can provide to its container during the process of stage
		/// suppier resolution
		/// </param>
		/// <exception cref=""> NullPointerException if the descriptor, loggers, context, services,
		/// dependencies, stages, or extensions argument is null
		/// </exception>
		public TypeDescriptor(InfoDescriptor descriptor, CategoryDescriptor[] loggers, ContextDescriptor context, ServiceDescriptor[] services, DependencyDescriptor[] dependencies, StageDescriptor[] stages, ExtensionDescriptor[] extensions, IConfiguration defaults)
		{
			if (null == descriptor)
			{
				throw new System.NullReferenceException("descriptor");
			}
			if (null == loggers)
			{
				throw new System.NullReferenceException("loggers");
			}
			if (null == context)
			{
				throw new System.NullReferenceException("context");
			}
			if (null == services)
			{
				throw new System.NullReferenceException("services");
			}
			if (null == dependencies)
			{
				throw new System.NullReferenceException("dependencies");
			}
			if (null == stages)
			{
				throw new System.NullReferenceException("stages");
			}
			if (null == extensions)
			{
				throw new System.NullReferenceException("extensions");
			}
			
			m_descriptor = descriptor;
			m_loggers = loggers;
			m_context = context;
			m_services = services;
			m_dependencies = dependencies;
			m_stages = stages;
			m_extensions = extensions;
			m_configuration = defaults;
		}

		/// <summary> Return the Component descriptor.
		/// 
		/// </summary>
		/// <returns> the Component descriptor.
		/// </returns>
		virtual public InfoDescriptor Info
		{
			get
			{
				return m_descriptor;
			}
			
		}
		/// <summary> Return the set of Logger that this Component will use.
		/// 
		/// </summary>
		/// <returns> the set of Logger that this Component will use.
		/// </returns>
		virtual public CategoryDescriptor[] Categories
		{
			get
			{
				return m_loggers;
			}
			
		}
		/// <summary> Return the ContextDescriptor for component, may be null.
		/// If null then this component does not implement Contextualizable.
		/// 
		/// </summary>
		/// <returns> the ContextDescriptor for component, may be null.
		/// </returns>
		virtual public ContextDescriptor Context
		{
			get
			{
				return m_context;
			}
			
		}
		/// <summary> Return the set of Services that this component is capable of providing.
		/// 
		/// </summary>
		/// <returns> the set of Services that this component is capable of providing.
		/// </returns>
		virtual public ServiceDescriptor[] Services
		{
			get
			{
				return m_services;
			}
			
		}
		/// <summary> Return the set of Dependencies that this component requires to operate.
		/// 
		/// </summary>
		/// <returns> the set of Dependencies that this component requires to operate.
		/// </returns>
		virtual public DependencyDescriptor[] Dependencies
		{
			get
			{
				return m_dependencies;
			}
			
		}
		/// <summary> Returns the default configuration supplied with the type.
		/// 
		/// </summary>
		/// <returns> the default configuration or null if no packaged defaults
		/// </returns>
		virtual public IConfiguration Configuration
		{
			get
			{
				return m_configuration;
			}
			
		}
		/// <summary> Return the lifecycle stages extensions required by this component type.
		/// 
		/// </summary>
		/// <returns> an array of stage descriptors.
		/// </returns>
		virtual public StageDescriptor[] Stages
		{
			get
			{
				return m_stages;
			}
			
		}
		/// <summary> Return the stages extension handling provided by this extension.
		/// 
		/// </summary>
		/// <returns> an array of extension descriptors.
		/// </returns>
		virtual public ExtensionDescriptor[] Extensions
		{
			get
			{
				return m_extensions;
			}
			
		}

		
		/// <summary> Return TRUE if the set of Logger descriptors includes the supplied name.
		/// 
		/// </summary>
		/// <param name="name">the logging subcategory name
		/// </param>
		/// <returns> TRUE if the logging subcategory is declared.
		/// </returns>
		public virtual bool isaCategory(System.String name)
		{
			CategoryDescriptor[] loggers = Categories;
			for (int i = 0; i < loggers.Length; i++)
			{
				CategoryDescriptor logger = loggers[i];
				if (logger.Name.Equals(name))
				{
					return true;
				}
			}
			return false;
		}
		
		/// <summary> Retrieve a service with a particular reference.
		/// 
		/// </summary>
		/// <param name="reference">a service reference descriptor
		/// </param>
		/// <returns> the service descriptor or null if it does not exist
		/// </returns>
		public virtual ServiceDescriptor getService(ReferenceDescriptor reference)
		{
			for (int i = 0; i < m_services.Length; i++)
			{
				ServiceDescriptor service = m_services[i];
				if (service.Reference.Matches(reference))
				{
					return service;
				}
			}
			return null;
		}
		
		/// <summary> Retrieve a service with a particular classname.
		/// 
		/// </summary>
		/// <param name="classname">the service classname
		/// </param>
		/// <returns> the service descriptor or null if it does not exist
		/// </returns>
		public virtual ServiceDescriptor getService(System.String classname)
		{
			for (int i = 0; i < m_services.Length; i++)
			{
				ServiceDescriptor service = m_services[i];
				if (service.Reference.Typename.Equals(classname))
				{
					return service;
				}
			}
			return null;
		}
		
		/// <summary> Retrieve a dependency with a particular role.
		/// 
		/// </summary>
		/// <param name="key">the service key
		/// </param>
		/// <returns> the dependency or null if it does not exist
		/// </returns>
		public virtual DependencyDescriptor getDependency(System.String key)
		{
			for (int i = 0; i < m_dependencies.Length; i++)
			{
				if (m_dependencies[i].Key.Equals(key))
				{
					return m_dependencies[i];
				}
			}
			return null;
		}
		
		/// <summary> Return the extension supporting the supplied stage.
		/// 
		/// </summary>
		/// <param name="stage">the lifecycle stage that this type requires a handler for
		/// </param>
		/// <returns> a matching extension or null if no matching extension
		/// </returns>
		public virtual ExtensionDescriptor getExtension(StageDescriptor stage)
		{
			return getExtension(stage.Key);
		}
		
		/// <summary> Return the extension supporting the supplied stage.
		/// 
		/// </summary>
		/// <param name="key">the lifecycle stage that this type requires a handler for
		/// </param>
		/// <returns> a matching extension or null if no matching extension
		/// </returns>
		public virtual ExtensionDescriptor getExtension(System.String key)
		{
			ExtensionDescriptor[] extensions = Extensions;
			for (int i = 0; i < extensions.Length; i++)
			{
				ExtensionDescriptor extension = extensions[i];
				System.String ref_Renamed = extension.Key;
				if (key.Equals(ref_Renamed))
				{
					return extension;
				}
			}
			return null;
		}
		
		/// <summary> Return a string representation of the type.</summary>
		/// <returns> the stringified type
		/// </returns>
		public override System.String ToString()
		{
			//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
			return Info.ToString();
		}
		
		/// <summary> Test is the supplied object is equal to this object.</summary>
		/// <returns> true if the object are equivalent
		/// </returns>
		public  override bool Equals(System.Object other)
		{
			bool isEqual = other is Type;
			isEqual = isEqual && m_descriptor.Equals(((TypeDescriptor) other).m_descriptor);
			isEqual = isEqual && m_configuration.Equals(((TypeDescriptor) other).m_configuration);
			isEqual = isEqual && m_context.Equals(((TypeDescriptor) other).m_context);
			for (int i = 0; i < m_loggers.Length; i++)
			{
				isEqual = isEqual && m_loggers[i].Equals(((TypeDescriptor) other).m_loggers[i]);
			}
			for (int i = 0; i < m_services.Length; i++)
			{
				isEqual = isEqual && m_services[i].Equals(((TypeDescriptor) other).m_services[i]);
			}
			for (int i = 0; i < m_dependencies.Length; i++)
			{
				isEqual = isEqual && m_dependencies[i].Equals(((TypeDescriptor) other).m_dependencies[i]);
			}
			for (int i = 0; i < m_stages.Length; i++)
			{
				isEqual = isEqual && m_stages[i].Equals(((TypeDescriptor) other).m_stages[i]);
			}
			for (int i = 0; i < m_extensions.Length; i++)
			{
				isEqual = isEqual && m_extensions[i].Equals(((TypeDescriptor) other).m_extensions[i]);
			}
			return isEqual;
		}
		
		/// <summary> Return the hashcode for the object.</summary>
		/// <returns> the hashcode value
		/// </returns>
		public override int GetHashCode()
		{
			int hash = m_descriptor.GetHashCode();
			hash ^= m_context.GetHashCode();
			if (m_configuration != null)
			{
				hash ^= m_context.GetHashCode();
			}
			for (int i = 0; i < m_services.Length; i++)
			{
				hash ^= m_services[i].GetHashCode();
			}
			for (int i = 0; i < m_dependencies.Length; i++)
			{
				hash ^= m_dependencies[i].GetHashCode();
			}
			for (int i = 0; i < m_loggers.Length; i++)
			{
				hash ^= m_loggers[i].GetHashCode();
			}
			for (int i = 0; i < m_stages.Length; i++)
			{
				hash ^= m_stages[i].GetHashCode();
			}
			for (int i = 0; i < m_extensions.Length; i++)
			{
				hash ^= m_extensions[i].GetHashCode();
			}
			return hash;
		}
	}
}