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

namespace Apache.Avalon.Castle.Core.Proxies
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;


	/// <summary>
	/// Summary description for AbstractManagedObjectProxy.
	/// </summary>
	public abstract class AbstractManagedObjectProxy
	{
		/// <summary>
		/// 
		/// </summary>
		protected MServer m_server;

		/// <summary>
		/// 
		/// </summary>
		protected ManagedObjectName m_target;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="server"></param>
		/// <param name="name"></param>
		public AbstractManagedObjectProxy( MServer server, ManagedObjectName name )
		{
			if (server == null)
			{
				throw new ArgumentNullException ( "server" );
			}

			if (name == null)
			{
				throw new ArgumentNullException ( "name" );
			}

			this.m_server = server;
			this.m_target = name;

			// TODO: Inspect ManagedObject to have clue
			// if it implements or not the interface
		}
	}
}
