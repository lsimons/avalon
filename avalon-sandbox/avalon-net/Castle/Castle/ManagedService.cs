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

namespace Apache.Avalon.Castle
{
	using System;
	using System.Collections;

	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for ManagedService.
	/// </summary>
	public abstract class ManagedService : MService
	{
		/// <summary>
		/// Reference to 
		/// <see cref="Apache.Avalon.Castle.ManagementExtensions.MServer"/> 
		/// that hosts this instance.
		/// </summary>
		private MServer m_server;

		/// <summary>
		/// Reference to 
		/// <see cref="Apache.Avalon.Castle.ManagementExtensions.ManagedObjectName"/> 
		/// that represents this instance.
		/// </summary>
		private ManagedObjectName m_name;

		/// <summary>
		/// Reference to 
		/// <see cref="Apache.Avalon.Castle.ManagementExtensions.ManagedObjectName"/> 
		/// of the parent managed component.
		/// </summary>
		private ManagedObjectName m_parentName;

		/// <summary>
		/// Collection of 
		/// <see cref="Apache.Avalon.Castle.ManagementExtensions.ManagedObjectName"/> 
		/// of children managed components.
		/// </summary>
		private ArrayList m_children = new ArrayList();

		/// <summary>
		/// Reference to 
		/// <see cref="Apache.Avalon.Castle.ManagedObjectState"/> 
		/// of the parent managed component.
		/// </summary>
		private ManagedObjectState m_state = ManagedObjectState.Undefined;

		/// <summary>
		/// Pending.
		/// </summary>
		public ManagedService()
		{
		}

		#region MService Members

		protected MServer Server
		{
			get
			{
				return m_server;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName ParentName
		{
			get 
			{
				return m_parentName;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName[] Children
		{
			get
			{
				return (ManagedObjectName[])
					m_children.ToArray( typeof(ManagedObjectName) );
			}
		}

		[ManagedAttribute]
		public ManagedObjectState ManagedObjectState
		{
			get
			{
				return m_state;
			}
		}

		[ManagedOperation]
		public void AddChild(ManagedObjectName childName)
		{
			if ( childName == null )
			{
				throw new ArgumentNullException( "childName", "Child name can't be null" );
			}

			m_children.Add( childName );

			m_server.Invoke( 
				childName, 
				"SetParent", new Object[] { ManagedObjectName }, new Type[] { typeof(ManagedObjectName) } );
		}

		[ManagedOperation]
		public void RemoveChild(ManagedObjectName childName)
		{
			if ( childName == null )
			{
				throw new ArgumentNullException( "childName", "Child name can't be null" );
			}

			m_children.Remove( childName );
		}

		[ManagedOperation]
		public void SetParent(ManagedObjectName parentName)
		{
			m_parentName = parentName;
		}

		[ManagedAttribute]
		public ManagedObjectName ManagedObjectName
		{
			get
			{
				return m_name;
			}
		}

		[ManagedOperation]
		public virtual void Create()
		{
			m_state = ManagedObjectState.Created;
		}

		[ManagedOperation]
		public virtual void Start()
		{
			m_state = ManagedObjectState.Started;
		}

		[ManagedOperation]
		public virtual void Stop()
		{
			m_state = ManagedObjectState.Stopped;
		}

		[ManagedOperation]
		public virtual void Destroy()
		{
			m_state = ManagedObjectState.Destroyed;

			if (ParentName != null)
			{
				m_server.Invoke( 
					ParentName, 
					"RemoveChild", new Object[] { ManagedObjectName }, new Type[] { typeof(ManagedObjectName) } );
			}
		}

		#endregion

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			m_server = server;
			m_name = name;

			BeforeRegister();
		}

		public virtual void AfterDeregister()
		{
			m_server = null;
			m_name = null;
		}

		public virtual void AfterRegister()
		{
		}

		public virtual void BeforeDeregister()
		{
		}

		#endregion

		protected virtual void BeforeRegister()
		{
		}
	}
}
