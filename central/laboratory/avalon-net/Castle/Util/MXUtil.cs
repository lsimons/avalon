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

namespace Apache.Avalon.Castle.Util
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for MXUtil.
	/// </summary>
	public sealed class MXUtil
	{
		private MXUtil()
		{
		}

		public static void Create( MServer server, ManagedObjectName name )
		{
			InvokeOn( server, name, "Create" );
		}

		public static void Start( MServer server, ManagedObjectName name )
		{
			InvokeOn( server, name, "Start" );
		}

		public static void Stop( MServer server, ManagedObjectName name )
		{
			InvokeOn( server, name, "Stop" );
		}

		public static void Destroy( MServer server, ManagedObjectName name )
		{
			InvokeOn( server, name, "Destroy" );
		}

		public static object InvokeOn( MServer server, ManagedObjectName name, String operation )
		{
			return server.Invoke(name, operation, null, null);
		}

		public static object InvokeOn( MServer server, ManagedObjectName name, String operation, params Object[] args )
		{
			Type[] argTypes = new Type[ args.Length ];
			
			for(int i=0; i < args.Length; i++)
			{
				argTypes[ i ] = args[ i ].GetType();
			}

			return server.Invoke(name, operation, args, argTypes);
		}

		public static void SetAttribute( MServer server, ManagedObjectName name, String attributeName, object value )
		{
			server.SetAttribute( name, attributeName, value );
		}

		public static object GetAttribute( MServer server, ManagedObjectName name, String attributeName )
		{
			return server.GetAttribute( name, attributeName );
		}
	}
}
