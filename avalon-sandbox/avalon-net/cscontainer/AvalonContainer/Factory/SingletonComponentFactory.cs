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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Attributes;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for SingletonComponentFactory.
	/// </summary>
	[CustomFactoryBuilder(typeof(SingletonComponentFactoryBuilder))]
	[LifestyleTarget(Lifestyle.Singleton)]
	internal class SingletonComponentFactory : AbstractComponentFactory, IDisposable
	{
		public SingletonComponentFactory()
		{
		}

		public override object Create( Type componentType )
		{
			if (m_instance == null)
			{
				m_instance = base.Create(componentType);
			}
			return m_instance;
		}
	
		public override void Release(object componentInstance)
		{
			// Can't call Dispose in a singleton component
		}

		#region IDisposable Members
		public override void Dispose()
		{
			base.Dispose();
		}
		#endregion
	}

	/// <summary>
	/// <see cref="SingletonComponentFactoryBuilder"/> is a <see cref="FactoryBuilder"/>
	/// implementation for <see cref="SingletonComponentFactory"/> that
	/// keep instances of factories
	/// </summary>
	internal sealed class SingletonComponentFactoryBuilder : FactoryBuilder
	{
		private static Hashtable factories = new Hashtable();

		public SingletonComponentFactoryBuilder()
		{
		}

		public override IComponentFactory GetFactory(Type componentType)
		{
			IComponentFactory factory = null;

			lock(factories)
			{
				factory = (IComponentFactory) factories[componentType];

				if (factory == null)
				{
					factory = new SingletonComponentFactory();
					factories[componentType] = factory;
				}
			}

			return factory;
		}
	}
}
