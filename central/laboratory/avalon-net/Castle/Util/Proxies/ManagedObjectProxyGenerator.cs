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

namespace Apache.Avalon.Castle.Util.Proxies
{
	using System;
	using System.Reflection;

	using Apache.Avalon.DynamicProxy;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;

	/// <summary>
	/// Summary description for ManagedObjectProxyGenerator.
	/// </summary>
	public sealed class ManagedObjectProxyGenerator
	{
		private ManagedObjectProxyGenerator()
		{
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="name"></param>
		/// <param name="server"></param>
		/// <param name="implementedInterface"></param>
		/// <returns></returns>
		public static object CreateProxy( ManagedObjectName name, 
			MServer server, Type implementedInterface )
		{
			ManagedObjectInvocationHandler handler = 
				new ManagedObjectInvocationHandler( name, server );

			return ProxyGenerator.CreateProxy( new Type[] { implementedInterface }, handler );
		}
	}

	/// <summary>
	/// 
	/// </summary>
	internal class ManagedObjectInvocationHandler : IInvocationHandler
	{
		/// <summary>
		/// 
		/// </summary>
		private ManagedObjectName m_target;
		
		/// <summary>
		/// 
		/// </summary>
		private MServer m_server;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="target"></param>
		/// <param name="server"></param>
		public ManagedObjectInvocationHandler(ManagedObjectName target, MServer server)
		{
			if (target == null)
			{
				throw new ArgumentNullException("target");
			}
			if (server== null)
			{
				throw new ArgumentNullException("server");
			}

			m_target = target;
			m_server = server;
		}

		#region IInvocationHandler Members

		/// <summary>
		/// 
		/// </summary>
		/// <param name="proxy"></param>
		/// <param name="method"></param>
		/// <param name="arguments"></param>
		/// <returns></returns>
		public object Invoke(object proxy, MethodBase method, params object[] arguments)
		{
			if (method.IsSpecialName)
			{
				if (method.Name.StartsWith("set_"))
				{
					MXUtil.SetAttribute( m_server, m_target, method.Name.Substring(4), arguments[0] );
				}
				else if (method.Name.StartsWith("get_"))
				{
					return MXUtil.GetAttribute( m_server, m_target, method.Name.Substring(4) );
				}
			}
			else
			{
				return MXUtil.InvokeOn( m_server, m_target, method.Name, arguments );
			}

			return null;
		}

		#endregion

	}
}
