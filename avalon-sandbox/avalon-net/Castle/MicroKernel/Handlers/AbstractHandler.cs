// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.MicroKernel.Handlers
{
	using System;
	using System.Collections;

	/// <summary>
	/// Summary description for AbstractHandler.
	/// </summary>
	public abstract class AbstractHandler : IHandler
	{
		protected Type m_service;

		protected Type m_implementation;

		protected Kernel m_kernel;

		protected State m_state = State.Valid;

		protected ArrayList m_dependencies = new ArrayList();

		protected Hashtable m_serv2handler = new Hashtable();

		protected ILifestyleManager m_lifestyleManager;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <param name="implementation"></param>
		public AbstractHandler(Type service, Type implementation)
		{
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( implementation, "implementation" );

			m_service = service;
			m_implementation = implementation;
		}

		#region IHandler Members

		public virtual void Init( Kernel kernel )
		{
			m_kernel = kernel;
		}

		public virtual object Resolve()
		{
			if (m_state == State.WaitingDependency)
			{
				throw new HandlerException("Can't Resolve component. It has dependencies to be satisfied.");
			}

			try
			{
				return m_lifestyleManager.Resolve();
			}
			catch(Exception ex)
			{
				throw new HandlerException("Exception while attempting to instantiate type", ex);
			}
		}

		public virtual void Release()
		{
			m_lifestyleManager.Release();
		}

		/// <summary>
		/// 
		/// </summary>
		public virtual State ActualState
		{
			get
			{
				return m_state;
			}
		}

		#endregion
	}
}
