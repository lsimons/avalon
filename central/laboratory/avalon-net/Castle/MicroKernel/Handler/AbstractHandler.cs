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

namespace Apache.Avalon.Castle.MicroKernel.Handler
{
	using System;
	using System.Collections;

	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Events;

	/// <summary>
	/// Summary description for AbstractHandler.
	/// </summary>
	public abstract class AbstractHandler : IHandler
	{
		protected IKernel m_kernel;

		protected IComponentModel m_componentModel;

		protected State m_state = State.Valid;

		protected ArrayList m_dependencies = new ArrayList();

		protected Hashtable m_serv2handler = new Hashtable();

		protected ILifestyleManager m_lifestyleManager;

		private ArrayList m_instances = new ArrayList();

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <param name="implementation"></param>
		public AbstractHandler( IComponentModel model )
		{
			AssertUtil.ArgumentNotNull( model, "model" );

			m_componentModel = model;
		}

		#region IHandler Members

		public virtual void Init( IKernel kernel )
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
				object instance = m_lifestyleManager.Resolve();

				RegisterInstance( ref instance );

				return instance;
			}
			catch(Exception ex)
			{
				throw new HandlerException("Exception while attempting to instantiate type", ex);
			}
		}

		public virtual void Release( object instance )
		{
			if ( IsOwner(instance) )
			{
				UnregisterInstance( instance );
				m_lifestyleManager.Release( instance );
			}
		}

		public virtual bool IsOwner( object instance )
		{
			return HasInstance( instance, false );
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

		protected virtual void RegisterInstance( ref object instance )
		{
			RaiseComponentCreatedEvent( ref instance );

			if (!HasInstance( instance, false ))
			{
				// WeakReference reference = new WeakReference( instance );
				// m_instances.Add( reference );
				m_instances.Add( instance );
			}
		}

		protected virtual void UnregisterInstance( object instance )
		{
			RaiseComponentCreatedEvent( instance );

			if (m_instances.Count == 0)
			{
				return;
			}

			HasInstance( instance, true );
		}

		protected virtual bool HasInstance( object instance, bool removeIfFound )
		{
			// foreach( WeakReference reference in m_instances )
			foreach( object storedInstance in m_instances )
			{
				// if (reference.Target == null)
				// {
				//	m_instances.Remove( reference );
				// }

				if ( Object.ReferenceEquals( instance, storedInstance /*reference.Target*/ ) )
				{
					if (removeIfFound)
					{
						m_instances.Remove( instance );
					}

					return true;
				}
			}

			return false;
		}

		protected virtual void RaiseComponentCreatedEvent( ref object instance )
		{
			IEventManager eventManager = (IEventManager) m_kernel.GetSubsystem( KernelConstants.EVENTS );
			
			if (eventManager != null)
			{
				// We're passing the instance and allowing the listeners to
				// replace/wrap the instance as they wish.
				EventManagerData data = new EventManagerData( m_componentModel, instance );
				
				eventManager.OnComponentCreated( data );

				/// 90% of cases we're setting the same instance back
				instance = data.Instance;
			}
		}

		protected virtual void RaiseComponentCreatedEvent( object instance )
		{
			IEventManager eventManager = (IEventManager) m_kernel.GetSubsystem( KernelConstants.EVENTS );
			
			if (eventManager != null)
			{
				EventManagerData data = new EventManagerData( m_componentModel, instance );
				eventManager.OnComponentDestroyed( data );
			}
		}
	}
}
