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

namespace Apache.Avalon.Castle.Core
{
	using System;
	using System.Collections;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Castle.Util.Proxies;

	/// <summary>
	/// Summary description for OrchestratorNotificationSystem.
	/// </summary>
	public sealed class OrchestratorNotificationSystem
	{
		/// <summary></summary>
		private MServer m_server;

		/// <summary></summary>
		private ListenersHolder m_phases = new ListenersHolder();

		/// <summary>
		/// 
		/// </summary>
		/// <param name="server"></param>
		public OrchestratorNotificationSystem(MServer server)
		{
			Assert.ArgumentNotNull( server, "server" );

			m_server = server;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="name"></param>
		/// <param name="phase"></param>
		public void AddListener(ManagedObjectName name, LifecyclePhase phase)
		{
			Assert.ArgumentNotNull( name, "name" );

			MContributeLifecycle proxy = (MContributeLifecycle) 
				ManagedObjectProxyGenerator.CreateProxy( name, m_server, typeof(MContributeLifecycle) );

			m_phases[phase].Add( proxy );
		}

		public void RaiseEvents(LifecyclePhase phase)
		{
		}

		public bool HasListeners(LifecyclePhase phase)
		{
			return !m_phases[phase].IsEmpty;
		}

		public void Clear()
		{
			m_phases.Clear();
		}
	}
}
