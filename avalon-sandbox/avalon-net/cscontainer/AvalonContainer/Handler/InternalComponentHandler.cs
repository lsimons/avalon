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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Manages internal components, not true components.
	/// <br/>
	/// These components can't participate in extension managers,
	/// the logger it receives is a ConsoleLogger and they don't 
	/// have any special Lifestyle support (singleton, transient etc)
	/// </summary>
	internal sealed class InternalComponentHandler : IComponentHandler
	{
		private ILogger        m_logger;
		private IConfiguration m_configuration;
		private ILookupManager m_lookupManager;
		private IContext       m_context;
		private Type           m_componentType;

		public InternalComponentHandler(ILogger logger, IContext context, IConfiguration configuration, Type componentType)
		{
			m_logger        = logger;
			m_context       = context;
			m_configuration = configuration;
			m_componentType = componentType;
		}

		public ILookupManager LookupManager
		{
			get
			{
				return m_lookupManager;
			}
			set
			{
				m_lookupManager = value;
			}
		}

		#region IComponentHandler Members

		public object GetInstance()
		{
			object instance = Activator.CreateInstance(m_componentType);

			ContainerUtil.EnableLogging(instance, m_logger);
			ContainerUtil.Contextualize(instance, m_context);
			ContainerUtil.Configure(instance, m_configuration);

			if (LookupManager != null)
			{
				ContainerUtil.Service(instance, LookupManager);
			}

			ContainerUtil.Initialize(instance);
			ContainerUtil.Start(instance);

			return instance;
		}

		public void PutInstance(object instance)
		{

		}

		#endregion
	}
}

