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

namespace Apache.Avalon.Container.Handler
{
	using System;
	using System.Threading;
	using System.Runtime.Remoting.Proxies;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for DelegateHandler.
	/// </summary>
	public sealed class DelegateHandler : MarshalByRefObject, IComponentHandler
	{
		private  IComponentFactory m_factory;
		private  ComponentEntry    m_componentEntry;

		public DelegateHandler(IComponentFactory factory, ComponentEntry entry)
		{
			m_factory        = factory;
			m_componentEntry = entry;
		}

		#region IComponentHandler Members

		public object GetInstance()
		{
			try
			{
				return Factory.Create(m_componentEntry.ComponentType);
			}
			catch(Exception inner)
			{
				throw new LookupException("Error instantiating component.", inner);
			}
		}

		public void PutInstance(object instance)
		{
			Factory.Release(instance);
		}

		#endregion

		private IComponentFactory Factory
		{
			get
			{
				return m_factory;
			}
		}

		internal ComponentEntry ComponentEntry
		{
			get
			{
				return m_componentEntry;
			}
		}

		public IComponentHandler GetProxy(LifecycleManager manager)
		{
			HandlerProxy proxy = new HandlerProxy(this, manager);
			return (IComponentHandler) proxy.GetTransparentProxy();
		}
	}
}
