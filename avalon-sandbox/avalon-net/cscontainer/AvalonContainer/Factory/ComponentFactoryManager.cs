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

namespace Apache.Avalon.Container.Factory
{
	using System;
	using System.Collections;
	using System.Reflection;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Attributes;
	using Apache.Avalon.Container.Services;
	using Apache.Avalon.Container.Util;

	/// <summary>
	/// Summary description for ComponentFactoryManager.
	/// </summary>
	internal interface IComponentFactoryManager
	{
		IComponentFactory GetFactory(ComponentEntry entry);
	}

	internal class ComponentFactoryManager : IComponentFactoryManager, IInitializable, IDisposable
	{
		private Hashtable m_factoryPrototype;
		private ArrayList m_builders;

		#region IComponentFactoryManager Members

		public IComponentFactory GetFactory(ComponentEntry entry)
		{
			Type factoryType = (Type) m_factoryPrototype[entry.Lifestyle];

			if (factoryType == null)
			{
				throw new UnsupportedLifestyleException(entry.Lifestyle);
			}

			FactoryBuilder builder = ResolveFactoryBuilder(factoryType);

			return builder.GetFactory(entry.ComponentType);
		}

		#endregion

		#region IInitializable Members

		public void Initialize()
		{
			m_builders = new ArrayList();
			InitializeFactories();
		}

		#endregion

		private FactoryBuilder ResolveFactoryBuilder(Type factoryType)
		{
			object[] attributes = factoryType.GetCustomAttributes(
				typeof(CustomFactoryBuilderAttribute), true);

			if (attributes.Length == 0)
			{
				return new FactoryBuilder(factoryType);
			}

			CustomFactoryBuilderAttribute attribute = 
				(CustomFactoryBuilderAttribute) attributes[0];
			
			FactoryBuilder builder = (FactoryBuilder) 
				Activator.CreateInstance(attribute.FactoryBuilder);

			m_builders.Add(new WeakReference(builder));
			
			return builder;
		}

		private void InitializeFactories()
		{
			m_factoryPrototype = new Hashtable();
			Pair[] pairs = FindFactories();
			InitializePrototype(pairs);
		}

		private Pair[] FindFactories()
		{
			Assembly currentAssembly = Assembly.GetExecutingAssembly();

			return AssemblyUtil.FindTypesUsingAttribute(
				currentAssembly, typeof(LifestyleTargetAttribute), false);
		}

		private void InitializePrototype(Pair[] pairs)
		{
			foreach(Pair pair in pairs)
			{
				Type type = pair.First as Type;
				LifestyleTargetAttribute att = pair.Second as LifestyleTargetAttribute;

				Lifestyle style = att.Lyfestyle;
				
				System.Diagnostics.Debug.Assert(!m_factoryPrototype.Contains(style), "Lifestyle already handle by a factory.");

				// Simplest situation (one handler registered 
				// to each supported lifestyle)

				m_factoryPrototype[style] = type;
			}
		}

		#region IDisposable Members

		public void Dispose()
		{
			m_factoryPrototype.Clear();

			foreach(WeakReference weakRef in m_builders)
			{
				if (weakRef.IsAlive)
				{
					ContainerUtil.Shutdown(weakRef);
				}
			}
		}

		#endregion
	}
}
