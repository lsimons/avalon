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
	/// <summary>
		public override object Create( Type componentType )
		{
			object instance = base.Create( componentType );

			// This should be done to reduce the probably of 
			// leaks caused by not releasing components.
			// If a component doesn't supports Dipose, then 
			// we won't keep the instances.
			if (ContainerUtil.ExpectsDispose(instance))
			{
				m_instances.Add(instance);
			}

			return instance;
		}
		{
			return m_instances.Contains( componentInstance );
		}
		{
			m_instances.Remove( componentInstance );
		}
		#endregion
		#region IDisposable Members

		public override void Dispose()
		{
			base.Dispose();

			foreach(object instance in m_instances)
			{
				ContainerUtil.Shutdown(instance);
			}

			m_instances.Clear();
		}

		#endregion
	}
	/// This implementation determines the behavior of one TransientComponentFactory 
	/// for the entire application lifetime.
	/// </summary>
		public override IComponentFactory GetFactory(Type componentType)
		{
			return m_componentFactory;
		}

		#region IDisposable Members

		public void Dispose()
		{
			ContainerUtil.Shutdown(m_componentFactory);
		}

		#endregion
	}