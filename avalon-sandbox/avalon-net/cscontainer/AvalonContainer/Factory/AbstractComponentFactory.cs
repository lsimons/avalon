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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for AbstractComponentFactory.
	/// </summary>
	internal abstract class AbstractComponentFactory : IComponentFactory
	{
		protected object m_instance;		protected AbstractComponentFactory()		{		}
		#region IComponentFactory Members
		public virtual object Create(Type componentType)
		{
			return Activator.CreateInstance(componentType);
		}

		public virtual bool IsOwner(object componentInstance)
		{
			return m_instance == componentInstance;
		}

		public abstract void Release(object componentInstance);
		#endregion

		#region IDisposable Members

		public virtual void Dispose()
		{
			m_instance = null;
		}

		#endregion
	}
}
