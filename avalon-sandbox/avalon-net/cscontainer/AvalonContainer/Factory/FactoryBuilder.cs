// Copyright 2003-2004 The Apache Software Foundation
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
	using System.Reflection;
	using System.Threading;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Attributes;
	using Apache.Avalon.Container.Services;
	using Apache.Avalon.Container.Util;

	/// <summary>
	/// Summary description for FactoryBuilder.
	/// </summary>
	public class FactoryBuilder
	{
		protected Type m_factoryType;

		protected FactoryBuilder()
		{
		}

		public FactoryBuilder(Type factoryType)
		{
			if (factoryType == null)
			{
				throw new ArgumentNullException(
					"factoryType", "IComponentFactory implementation type can't be null.");
			}

			m_factoryType = factoryType;
		}

		public virtual IComponentFactory GetFactory(Type componentType)
		{
			IComponentFactory factory = (IComponentFactory)
				Activator.CreateInstance(
					m_factoryType, 
					new object[] {componentType} );

			return factory;
		}
	}
}
