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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Events.Default
{
	using System;
	using System.ComponentModel;

	/// <summary>
	/// Summary description for EventManager.
	/// </summary>
	public class EventManager : AbstractSubsystem, IEventManager
	{
		public static readonly object ComponentAddedEvent = new object();
		public static readonly object ComponentCreatedEvent = new object();
		public static readonly object ComponentDestroyedEvent = new object();

		private EventHandlerList m_events = new EventHandlerList();

		public EventManager()
		{
		}

		#region IEventManager Members

		public event KernelDelegate ComponentAdded
		{
			add
			{
				m_events.AddHandler( ComponentAddedEvent, value );
			}
			remove
			{
				m_events.RemoveHandler( ComponentAddedEvent, value );
			}
		}

		public event KernelDelegate ComponentCreated
		{
			add
			{
				m_events.AddHandler( ComponentCreatedEvent, value );
			}
			remove
			{
				m_events.RemoveHandler( ComponentCreatedEvent, value );
			}
		}

		public event KernelDelegate ComponentDestroyed
		{
			add
			{
				m_events.AddHandler( ComponentDestroyedEvent, value );
			}
			remove
			{
				m_events.RemoveHandler( ComponentDestroyedEvent, value );
			}
		}

		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		public void OnComponentAdded( EventManagerData data )
		{
			RaiseEvent( ComponentAddedEvent, data );
		}

		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		public void OnComponentCreated( EventManagerData data )
		{
			RaiseEvent( ComponentCreatedEvent, data );
		}
		
		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		public void OnComponentDestroyed( EventManagerData data )
		{
			RaiseEvent( ComponentDestroyedEvent, data );
		}

		#endregion

		private void RaiseEvent( object eventKey, EventManagerData data )
		{
			KernelDelegate eventDelegate = (KernelDelegate) m_events[eventKey];
			if (eventDelegate != null) 
			{
				eventDelegate( data );
			}
		}
	}
}
