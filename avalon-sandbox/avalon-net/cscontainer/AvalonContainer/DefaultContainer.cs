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

namespace Apache.Avalon.Container
{
	using System;
	using System.Collections;
	using System.Reflection;
	using System.Configuration;
	using System.Diagnostics;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Configuration;
	using Apache.Avalon.Container.Lookup;
	using Apache.Avalon.Container.Services;
	using Apache.Avalon.Container.Handler;
	using Apache.Avalon.Container.Factory;
	using Apache.Avalon.Container.Logger;
	using Apache.Avalon.Container.Util;
	using Apache.Avalon.Container.Util.Dag;

	/// <summary>
	/// This is the default implementation of <b>Avalon Container</b>. It 
	/// can be extended to meet your requirements.
	/// </summary>
	/// <remarks>
	/// <para>The container initialization is the most important phase you have
	/// to deal with. A 
	/// <see cref="Apache.Avalon.Container.Configuration.ContainerConfiguration"/>
	/// must be correctly loaded and passed on to DefaultContainer through its
	/// constructor or implicitly, through Configuration files.
	/// </para>
	/// </remarks>
	/// <example>
	/// <code>
	///	<![CDATA[
	///	
	///	<!-- Sample configuration file -->
	///		
	///	<?xml version="1.0" encoding="utf-8" ?>
	///	<configuration>
	///		
	///	    <avalon.container>
	///	    
	///	      <extensionModules>
	///	        <add type="Namespace.Class, AssemblyName" />
	///	      </extensionModules>
	///	      
	///	      <logger>
	///	      </logger>
    ///	      
	///	      <components>
	///	        
	///	        <assembly type="Components.Assembly.Name1" />
	///	        <assembly type="Components.Assembly.Name2" />
	///	      
	///	        <component configurationName="Authentication" >
	///	          <!-- component configuration here -->
	///	        </component>
	///	        
	///	      </components>
	///	      
	///	    </avalon.container>
	///	      
	///	</configuration>
	///	]]>
	///	</code>
	/// </example>
	public class DefaultContainer : MarshalByRefObject, IDisposable
	{
		#region Fields
		protected ILoggerManager          m_loggerManager;
		protected LifecycleManager        m_lifecycleManager;
		protected Vertex[]                m_shutDownOrder;
		private ComponentCollection       m_components;
		private IComponentFactoryManager  m_factoryManager;
		private ILookupManager            m_lookupManager;
		private ILogger                   m_baseLogger;
		#endregion

		#region Constructors
		
		/// <summary>
		/// Constructs a <b>DefaultContainer</b>.
		/// </summary>
		/// <remarks>
		/// This constructor searchs for a &lt;avalon.container&gt; section
		/// in the AppDomain configuration file. If the configuration file
		/// is not found, an exception is throwed.
		/// </remarks>
		/// <exception cref="ContainerException">If the configuration file is not found.</exception>
		public DefaultContainer()
		{
			ContainerConfiguration config = (ContainerConfiguration) 
				ConfigurationSettings.GetConfig(ContainerConfigurationSectionHandler.Section);

			Setup(config);
		}

		/// <summary>
		/// Constructs a <b>DefaultContainer</b>.
		/// </summary>
		/// <remarks>
		/// This constructor needs a valid
		/// <see cref="Apache.Avalon.Container.Configuration.ContainerConfiguration"/>.
		/// <seealso cref="Apache.Avalon.Container.Configuration.ContainerConfiguration"/>
		/// </remarks>
		/// <param name="config"></param>
		public DefaultContainer(ContainerConfiguration config)
		{
			Setup(config);
		}
		#endregion

		#region Methods
		private void Setup(ContainerConfiguration config)
		{
			if (config == null)
			{
				MissingConfigurationError();
			}

			InitializeHooks();
			InitializeLogger(config);
			InitializeFactoryManager();
			InitializeLifestyleManager(config);
			FindComponents(config.Assemblies);
			VerifyComponentsDependencies();
			SetupComponents(config.ComponentConfiguration);
		}

		private void InitializeHooks()
		{
			AppDomain.CurrentDomain.DomainUnload += new EventHandler(OnDomainUnload);
		}

		private void OnDomainUnload(object sender, EventArgs e)
		{
			ContainerUtil.Shutdown(this);
		}

		/// <summary>
		/// Initialize LoggerManager for the container.
		/// </summary>
		/// <remarks>
		/// <para>
		/// <see cref="ContainerConfiguration"/> exposes the configuration 
		/// for the logger manager in the <c>&lt;logger&gt;</c> xml element.
		/// </para>
		/// </remarks>
		/// <example>
		/// <code>
		///	<![CDATA[
		///	
		///	<!-- Sample configuration file -->
		///		
		///	<?xml version="1.0" encoding="utf-8" ?>
		///	<configuration>
		///		
		///	  <configSections>
		///	    <section 
		///		   name="avalon.container" 
		///		   type="Apache.Avalon.Container.Configuration.ContainerConfigurationSectionHandler, Apache.Avalon.Container" />
		///	  </configSections>
		///		
		///   <avalon.container>
		///	    <logger>
		///	      <manager type="fullTypeName, AssemblyName" />
		///	    </logger>
 		///	  </avalon.container>
 		///	      
		///	</configuration>
		///	]]>
		///	</code>
		///		
		///	The <c>manager</c> element should be used to
		///	override the default <see cref="LoggerManager"/>. If the element
		///	isn't present, the default <see cref="LoggerManager"/> will
		///	be used.
		/// </example>
		/// <param name="config"></param>
		protected virtual void InitializeLogger(ContainerConfiguration config)
		{
			m_baseLogger = new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG);

			Type loggerType = null;

			IConfiguration loggerConfiguration = 
				ConfigurationUtil.GetConfiguration(config.LoggerNode);

			IConfiguration managerConfig = loggerConfiguration.GetChild("manager", false);

			if (managerConfig == null)
			{
				// If nothing different was specified, we instantiate 
				// or well know LoggerManager implementantion

				loggerType = typeof( LoggerManager );
			}
			else
			{
				String typeName = (String) managerConfig.Attributes["type"];
				loggerType = Type.GetType( typeName, true, true );
			}

			IComponentHandler handler = 
				new InternalComponentHandler( m_baseLogger, loggerConfiguration, loggerType );
			
			m_loggerManager = (ILoggerManager) handler.GetInstance();
		}

		protected virtual void InitializeFactoryManager()
		{
			IConfiguration extensionsConfiguration = 
				ConfigurationUtil.GetConfiguration( null );

			ILogger logger = m_loggerManager["FactoryManager"];

			Type factoryManagerType = typeof( ComponentFactoryManager );

			InternalComponentHandler handler = 
				new InternalComponentHandler( logger, extensionsConfiguration, factoryManagerType );

			m_factoryManager = (IComponentFactoryManager) handler.GetInstance();
		}

		/// <summary>
		/// TODO: Add summary
		/// </summary>
		/// <param name="config"></param>
		protected virtual void InitializeLifestyleManager(ContainerConfiguration config)
		{
			IConfiguration extensionsConfiguration = 
				ConfigurationUtil.GetConfiguration( config.ExtensionsNode );
			ILogger logger = m_loggerManager["LifecycleManager"];

			Type lifecycleManager = typeof( LifecycleManager );

			InternalComponentHandler handler = 
				new InternalComponentHandler( logger, extensionsConfiguration, lifecycleManager );

			BlindLookupManager lookUpManager = new BlindLookupManager(LookupManager);
			lookUpManager.Add( typeof(IComponentFactoryManager).FullName, m_factoryManager );
			lookUpManager.Add( typeof(ILoggerManager).FullName, m_loggerManager );
			lookUpManager.Add( "Container", this );
			handler.LookupManager = lookUpManager;

			m_lifecycleManager = (LifecycleManager) handler.GetInstance();
		}

		protected virtual void FindComponents(Assembly[] assemblies)
		{
			foreach(Assembly assembly in assemblies)
			{
				Pair[] pairs = AssemblyUtil.FindTypesUsingAttribute(
					assembly, typeof( AvalonServiceAttribute ), true);

				foreach(Pair pair in pairs)
				{
					Type componentType = (Type) pair.First;
					AvalonServiceAttribute serviceAttribute = 
						(AvalonServiceAttribute) pair.Second;
					
					object[] dependencies = componentType.GetCustomAttributes(
						typeof( AvalonDependencyAttribute ), true);
					
					object[] avalonComponent = componentType.GetCustomAttributes(
						typeof( AvalonComponentAttribute ), false);

					Debug.Assert(serviceAttribute != null, "Component doesnt specified a AvalonServiceAttribute attribute");
					Debug.Assert(avalonComponent.Length != 0, "Component doesnt specified a AvalonComponentAttribute attribute");
					Debug.Assert(avalonComponent.Length == 1, "Component specified more than one AvalonComponentAttribute attribute");
					
					AddComponent(serviceAttribute, componentType, avalonComponent, 
						dependencies);
				}
			}
		}

		protected virtual void AddComponent(AvalonServiceAttribute serviceAttribute, Type type, 
			object[] componentAttribute, object[] dependencies)
		{
			if ( serviceAttribute == null )
			{
				throw new ArgumentNullException( "serviceAttribute" );
			}
			if ( type == null )
			{
				throw new ArgumentNullException( "type" );
			}
			if ( componentAttribute == null || componentAttribute.Length == 0 )
			{
				throw new ArgumentNullException( "componentAttribute" );
			}

			String role = serviceAttribute.ServiceType.FullName;

			AvalonComponentAttribute attribute = null;
			attribute = (AvalonComponentAttribute) componentAttribute[0];

			AvalonDependencyAttribute[] dependenciesAttribute = null;

			if (dependencies.Length != 0)
			{
				dependenciesAttribute = new AvalonDependencyAttribute[dependencies.Length];
				dependencies.CopyTo(dependenciesAttribute, 0L);
			}

			ComponentEntry entry = new ComponentEntry(
				attribute, type, dependenciesAttribute);

			m_lifecycleManager.PrepareComponent( entry );

			Components.Add(role, entry);
		}

		protected virtual void VerifyComponentsDependencies()
		{
			Hashtable vertexMap = 
				new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);

			foreach(DictionaryEntry dicEntry in Components.GetEntries())
			{
				String role = (String) dicEntry.Key;
				ComponentEntry entry = (ComponentEntry) dicEntry.Value;

				Vertex vertex = vertexMap[role] as Vertex;

				if (vertex == null)
				{
					vertex = new Vertex(role, entry);
					vertexMap.Add(role, vertex);
				}

				foreach(AvalonDependencyAttribute dependency in entry.Dependencies)
				{
					String dependencyRole = dependency.DependencyType.FullName;
					ComponentEntry depEntry = Components[dependencyRole];

					if (depEntry == null && dependency.IsOptional == false)
					{
						MissingDependencyError(entry, dependencyRole);
					}

					Vertex child = new Vertex(dependencyRole, entry);
					vertex.AddDependency(child);
				}
			}

			Vertex[] vertices = new Vertex[vertexMap.Count];
			vertexMap.Values.CopyTo(vertices, 0);

			DirectedAcyclicGraphVerifier.TopologicalSort( vertices );

			Array.Reverse( vertices );

			m_shutDownOrder = vertices;
		}

		protected virtual void SetupComponents(IDictionary componentConfiguration)
		{
			foreach(ComponentEntry entry in Components)
			{
				entry.ExtractConfigurationNode(componentConfiguration);
			}
		}

		internal IComponentHandler GetComponentHandler(String role)
		{
			IComponentHandler handler = null;

			ComponentEntry entry = Components[role];

			if (entry != null)
			{
				handler = entry.Handler;
			}

			return handler;
		}
		#endregion

		#region ErrorMethod
		private void MissingDependencyError(ComponentEntry entry, String dependentRole)
		{
			String message = String.Format("Component {0} depends on {1} which has not been found.", entry.Name, dependentRole);
			throw new ContainerException(message);
		}

		private void MissingConfigurationError()
		{
			String message = "Impossible to start up the container while a configuration is not supplied. "
				+ "The configuration can be supplied using a configuration file or creating manually a ContainerConfiguration.";
			throw new ContainerException(message);
		}
		#endregion

		#region Properties
		internal ComponentCollection Components
		{
			get
			{
				if (m_components == null)
				{
					m_components = new ComponentCollection();
				}
				return m_components;
			}
		}

		public ILookupManager LookupManager
		{
			get
			{
				if (m_lookupManager == null)
				{
					m_lookupManager = new DefaultLookupManager(this);
				}

				return m_lookupManager;
			}
		}
		#endregion

		#region IDisposable Members
		public void Dispose()
		{
			if (m_baseLogger.IsDebugEnabled)
			{
				m_baseLogger.Debug("DefaultContainer: Dispose");
			}

			foreach(Vertex vertex in m_shutDownOrder)
			{
				if (m_baseLogger.IsDebugEnabled)
				{
					m_baseLogger.Debug("DefaultContainer: Disposing handler of {0} ...", 
						vertex.Entry.ComponentType.FullName );
				}

				ContainerUtil.Shutdown(vertex.Entry);
			}

			if (m_baseLogger.IsDebugEnabled)
			{
				m_baseLogger.Debug("DefaultContainer: Disposing ComponentFactoryManager");
			}
			ContainerUtil.Shutdown(m_factoryManager);

			if (m_baseLogger.IsDebugEnabled)
			{
				m_baseLogger.Debug("DefaultContainer: Disposing LifecycleManager");
			}
			ContainerUtil.Shutdown(m_lifecycleManager);

			if (m_baseLogger.IsDebugEnabled)
			{
				m_baseLogger.Debug("DefaultContainer: Disposing LoggerManager");
			}
			ContainerUtil.Shutdown(m_loggerManager);

			if (m_baseLogger.IsDebugEnabled)
			{
				m_baseLogger.Debug("DefaultContainer: Disposing Logger - bye");
			}
			ContainerUtil.Shutdown(m_baseLogger);
		}
		#endregion
	}
}
