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
	using System.ComponentModel;
	using System.Reflection;
	using System.Collections;
	
	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Configuration;
	using Apache.Avalon.Container.Extension;
	using Apache.Avalon.Container.Lookup;
	using Apache.Avalon.Container.Services;
	using Apache.Avalon.Container.Handler;
	using Apache.Avalon.Container.Factory;
	using Apache.Avalon.Container.Logger;

	/// <summary>
	/// Basic handler to listen to Container events.
	/// </summary>
	public delegate void LifecycleHandler(LifecycleEventArgs e);

	/// <summary>
	/// Summary description for LifecycleManager.
	/// </summary>
	public sealed class LifecycleManager : 
		ILogEnabled, IInitializable, IDisposable, IConfigurable, ILookupEnabled
	{
		private static readonly object BeforeCreationEvent = new object();
		private static readonly object AfterCreationEvent  = new object();
		private static readonly object BeforeReleaseEvent  = new object();
		private static readonly object AfterReleaseEvent   = new object();
		private static readonly object DependenciesEvent   = new object();

		private EventHandlerList         m_events;
		private IComponentFactoryManager m_factoryManager;
		private DefaultContainer         m_container;
		private ILogger                  m_logger;
		private ILoggerManager           m_loggerManager;
		private ArrayList                m_loadedModules;
		private WeakMap                  m_knowReferences;


		public LifecycleManager()
		{
			m_loadedModules  = new ArrayList();
			m_knowReferences = new WeakMap();
		}

		public void PrepareComponent(ComponentEntry entry)
		{
			if (m_logger.IsDebugEnabled)
			{
				m_logger.Debug("Preparing component '{0}'", entry.ComponentType.FullName);
			}

			IComponentFactory factory = m_factoryManager.GetFactory(entry);
			DelegateHandler handler = new DelegateHandler(factory, entry);
			entry.Handler = handler.GetProxy(this);
		}

		internal void BeforeGetInstance(DelegateHandler handler)
		{
			if (m_logger.IsDebugEnabled)
			{
				m_logger.Debug("BeforeGetInstance '{0}'", handler.ComponentEntry.ComponentType.FullName);
			}

			OnBeforeCreation(handler.ComponentEntry);
		}

		internal void AfterGetInstance(DelegateHandler handler, object instance)
		{
			if (m_logger.IsDebugEnabled && instance != null)
			{
				m_logger.Debug("AfterGetInstance '{0}'", instance.GetType().FullName);
			}

			ComponentEntry entry = handler.ComponentEntry;

			OnAfterCreation(ref instance, entry);

			if (ShallSetUpComponent(instance, entry))
			{
				if (m_logger.IsDebugEnabled)
				{
					m_logger.Debug("Setting up component '{0}'", instance.GetType().FullName);
				}

				if (instance is ILogEnabled)
				{
					ContainerUtil.EnableLogging(instance, m_loggerManager[entry.LoggerName]);
				}

				BlindLookupManager lookupManager = new BlindLookupManager(m_container.LookupManager);

				if (entry.Dependencies.Length != 0)
				{
					SetupComponentDependencies(instance, entry, lookupManager);
				}

				if (instance is ILookupEnabled)
				{
					ContainerUtil.Service(instance, lookupManager);
				}

				ContainerUtil.Configure(instance, entry.Configuration);

				ContainerUtil.Initialize(instance);
				
				ContainerUtil.Start(instance);
			}
		}

		internal void BeforePutInstance(DelegateHandler handler, object instance)
		{
			if (m_logger.IsDebugEnabled && instance != null)
			{
				m_logger.Debug("BeforePutInstance '{0}'", instance.GetType().FullName);
			}

			OnBeforeRelease(instance, handler.ComponentEntry);
		}

		internal void AfterPutInstance(DelegateHandler handler, object instance)
		{
			if (m_logger.IsDebugEnabled && instance != null)
			{
				m_logger.Debug("AfterPutInstance '{0}'", instance.GetType().FullName);
			}

			ComponentEntry entry = handler.ComponentEntry;

			OnAfterRelease(instance, entry);

			if (ShallSetUpComponent(instance, entry))
			{
				ContainerUtil.Shutdown(instance);
			}
		}

		#region Events
		public event LifecycleHandler BeforeCreation
		{
			add
			{
				Events.AddHandler(BeforeCreationEvent, value);
			}
			remove
			{
				Events.RemoveHandler(BeforeCreationEvent, value);
			}
		}

		public event LifecycleHandler AfterCreation
		{
			add
			{
				Events.AddHandler(AfterCreationEvent, value);
			}
			remove
			{
				Events.RemoveHandler(AfterCreationEvent, value);
			}
		}

		public event LifecycleHandler BeforeRelease
		{
			add
			{
				Events.AddHandler(BeforeReleaseEvent, value);
			}
			remove
			{
				Events.RemoveHandler(BeforeReleaseEvent, value);
			}
		}

		public event LifecycleHandler AfterRelease
		{
			add
			{
				Events.AddHandler(AfterReleaseEvent, value);
			}
			remove
			{
				Events.RemoveHandler(AfterReleaseEvent, value);
			}
		}

		public event LifecycleHandler SetupDependencies
		{
			add
			{
				Events.AddHandler(DependenciesEvent, value);
			}
			remove
			{
				Events.RemoveHandler(DependenciesEvent, value);
			}
		}

		private EventHandlerList Events
		{
			get
			{
				if (m_events == null)
				{
					m_events = new EventHandlerList();
				}

				return m_events;
			}
		}

		private void OnBeforeCreation(ComponentEntry entry)
		{
			if (Events[BeforeCreationEvent] != null)
			{
				System.Delegate del = Events[BeforeCreationEvent];
				del.Method.Invoke(del.Target, new object[] 
					{new LifecycleEventArgs(null, entry)});
			}									   
		}

		private void OnAfterCreation(ref object instance, ComponentEntry entry)
		{
			if (Events[AfterCreationEvent] != null)
			{
				System.Delegate del = Events[AfterCreationEvent];
				
				LifecycleEventArgs args = new LifecycleEventArgs(instance, entry);
				del.Method.Invoke(del.Target, new object[] {args});
				
				if (args.Component != instance)
				{
					if (m_logger.IsDebugEnabled && instance != null)
					{
						m_logger.Debug(
							"OnAfterCreation - Extension changed component instance " + 
							"from '{0}' to '{1}'", instance.GetType().FullName, args.Component.GetType().FullName);
					}

					instance = args.Component;
				}
			}									   
		}

		private void OnBeforeRelease(object instance, ComponentEntry entry)
		{
			if (Events[BeforeReleaseEvent] != null)
			{
				System.Delegate del = Events[BeforeReleaseEvent];
				del.Method.Invoke(del.Target, new object[] 
					{new LifecycleEventArgs(instance, entry)});
			}									   
		}

		private void OnAfterRelease(object instance, ComponentEntry entry)
		{
			if (Events[AfterReleaseEvent] != null)
			{
				System.Delegate del = Events[AfterReleaseEvent];
				del.Method.Invoke(del.Target, new object[] 
					{new LifecycleEventArgs(instance, entry)});
			}									   
		}

		private void OnSetupDependencies(object instance, ComponentEntry entry, 
			String dependencyRole, String dependencyKey, object dependencyInstance)
		{
			if (Events[DependenciesEvent] != null)
			{
				System.Delegate del = Events[DependenciesEvent];
				del.Method.Invoke(del.Target, new object[] 
					{new LifecycleEventArgs(instance, entry, 
						dependencyRole, dependencyInstance, dependencyKey)});
			}									   
		}
		#endregion

		#region IInitializable Members

		public void Initialize()
		{
		}

		#endregion
	
		#region ILogEnabled Members

		public void EnableLogging(ILogger logger)
		{
			m_logger = logger;
		}

		#endregion
	
		#region IConfigurable Members

		public void Configure(IConfiguration config)
		{
			ConfigurationCollection addNodes = config.GetChildren("add");

			ArrayList loadedTypes = new ArrayList();

			foreach(IConfiguration addNode in addNodes)
			{
				String typeName = (String) addNode.Attributes["type"];
				// String name     = addNode.Attributes["name"];

				try
				{
					Type type = Type.GetType(typeName);

					loadedTypes.Add(type);
				}
				catch(ArgumentNullException)
				{
					throw new ConfigurationException(
						String.Format("Invalid type '{0}'.", typeName));
				}
				catch(TargetInvocationException inner)
				{
					throw new ConfigurationException(
						String.Format("Error loading type '{0}'.", typeName), inner);
				}
			}

			foreach(Type type in loadedTypes)
			{
				IExtensionModule module = (IExtensionModule) Activator.CreateInstance(type);
				
				module.Init(this);
				
				m_loadedModules.Add(module);
			}
		}

		#endregion

		#region IDisposable Members

		public void Dispose()
		{
			foreach(object module in m_loadedModules)
			{
				ContainerUtil.Shutdown(module);
			}

			m_loadedModules.Clear();

			ContainerUtil.Shutdown(m_logger);
			m_logger = null;

			ContainerUtil.Shutdown(m_factoryManager);
			m_factoryManager = null;
		}

		#endregion

		#region ILookupEnabled Members

		public void EnableLookups(ILookupManager manager)
		{
			m_factoryManager = (IComponentFactoryManager)
				manager[ typeof(IComponentFactoryManager).FullName ];

			m_container      = (DefaultContainer) manager[ "Container" ];

			m_loggerManager  = (ILoggerManager)
				manager[ typeof(ILoggerManager).FullName ];
		}

		#endregion

		private void SetupComponentDependencies(object instance, ComponentEntry entry, BlindLookupManager lookupManager)
		{
			if (m_logger.IsDebugEnabled && instance != null)
			{
				m_logger.Debug("SetupComponentDependencies '{0}'", instance.GetType().FullName);
			}

			// TODO: There are a lot of to-dos here. As we are focusing
			// the simplest case, they should not be an issue.

			foreach(AvalonDependencyAttribute dep in entry.Dependencies)
			{
				String depRole = dep.DependencyType.FullName;
				ComponentEntry depEntry = m_container.Components[depRole];

				if (depEntry != null && entry != depEntry)
				{
					if (m_logger.IsDebugEnabled)
					{
						m_logger.Debug("  Dependency: '{0}'", depEntry.ComponentType.FullName);
					}

					// "Release" are deferred to container shutdown

					object depInstance = depEntry.Handler.GetInstance();
					
					lookupManager.Add(dep.Key, depInstance);

					OnSetupDependencies(instance, entry, depRole, dep.Key, depInstance);
				}
			}
		}

		private bool ShallSetUpComponent(object instance, ComponentEntry entry)
		{
			// Here, LifestyleManager must know something about lifestyles. 
			// This is too fragile. A better way is the handler/factory tell 
			// us someway if the component needs set up or not

			if (entry.Lifestyle == Lifestyle.Transient)
			{
				return true;
			}

			// TODO: Ensure thread safety

			if (!m_knowReferences.IsAlreadyInitialized(entry, instance))
			{
				// This should be done at the end of initialization process...

				m_knowReferences.Add(entry, instance);

				return true;
			}

			return false;
		}
	}

	/// <summary>
	/// 
	/// </summary>
	public sealed class LifecycleEventArgs : EventArgs
	{
		private object         m_component;
		private ComponentEntry m_entry;
		private String         m_dependencyRole;
		private object         m_dependencyInstance;
		private String         m_dependencyKey;

		internal LifecycleEventArgs(object component, ComponentEntry entry)
		{
			m_component = component;
			m_entry     = entry;
		}

		internal LifecycleEventArgs(object component, ComponentEntry entry, 
			String dependencyRole, object dependencyInstance, String dependencyKey) : this(component, entry)
		{
			m_dependencyRole     = dependencyRole;
			m_dependencyInstance = dependencyInstance;
			m_dependencyKey      = dependencyKey;
		}

		public object Component
		{
			get
			{
				return m_component;
			}
			set
			{
				m_component = value;
			}
		}

		public object ComponentType
		{
			get
			{
				return m_entry.ComponentType;
			}
		}

		public Lifestyle Lifestyle
		{
			get
			{
				return m_entry.Lifestyle;
			}
		}

		public String DependencyRole
		{
			get
			{
				return m_dependencyRole;
			}
		}

		public String DependencyKey
		{
			get
			{
				return m_dependencyKey;
			}
		}

		public object DependencyInstance
		{
			get
			{
				return m_dependencyInstance;
			}
		}
	}

	internal class WeakMap
	{
		private Hashtable m_components;

		public WeakMap()
		{
			m_components = new Hashtable();
		}

		public bool IsAlreadyInitialized(ComponentEntry entry, object instance)
		{
			lock(this)
			{
				WeakItemList item = m_components[entry.ComponentType] as WeakItemList;

				if (item != null)
				{
					return item.HasInstance(instance);
				}
			}

			return false;
		}

		public void Add(ComponentEntry entry, object instance)
		{
			lock(this)
			{
				WeakItemList item = m_components[entry.ComponentType] as WeakItemList;

				if (item == null)
				{
					item = new WeakItemList();
					m_components[entry.ComponentType] = item;
				}

				item.Add(instance);
			}
		}

		internal class WeakItemList
		{
			private ArrayList m_items;

			public WeakItemList()
			{
				m_items = new ArrayList();
			}

			public void Add(object instance)
			{
				m_items.Add(new WeakReference(instance));
			}

			public bool HasInstance(object instance)
			{
				foreach(WeakReference weakRef in m_items)
				{
					if (!weakRef.IsAlive)
					{
						m_items.Remove(weakRef);
					}

					if (weakRef.IsAlive && weakRef.Target == instance)
					{
						return true;
					}
				}

				return true;
			}
		}
	}
}