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

namespace Apache.Avalon.Container.Lookup
{
	using System;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for DefaultLookupManager.
	/// </summary>
	public class DefaultLookupManager : ILookupManager
	{
		private Hashtable m_handlers;
		private DefaultContainer m_container;

		public DefaultLookupManager( DefaultContainer container )
		{
			m_container = container;
			m_handlers  = new Hashtable();
		}

		#region ILookupManager Members

		public virtual object this[ String role ]
		{
			get
			{
				if ( role == null || role.Length == 0 )
				{
					throw new ArgumentNullException("role", "You can't look up using an empty role.");
				}

				IComponentHandler handler = m_container.GetComponentHandler(role);

				if ( handler == null )
				{
					throw new LookupException( role, "Unknown component." );
				}

				object instance = handler.GetInstance();
				m_handlers[instance] = handler;
				return instance;
			}
		}

		public virtual void Release(object resource)
		{
			if ( resource == null )
			{
				throw new ArgumentNullException("resource", "You can't release a null resource.");
			}

			IComponentHandler handler = m_handlers[resource] as IComponentHandler;

			if ( handler != null )
			{
				handler.PutInstance( resource );
				m_handlers.Remove( resource );
			}
		}

		public virtual bool Contains( String role )
		{
			if ( role == null )
			{
				throw new ArgumentNullException("role");
			}

			return m_container.Components.Contains(role);
		}

		#endregion
	}
}
