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

namespace Apache.Avalon.Container.Handler
{
	using System;
	using System.Runtime.Remoting;
	using System.Runtime.Remoting.Messaging;
	using System.Runtime.Remoting.Proxies;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Services;

	/// <summary>
	/// Summary description for HandlerProxy.
	/// </summary>
	internal class HandlerProxy : RealProxy
	{
		private DelegateHandler  m_handler;
		private LifecycleManager m_manager;

		public HandlerProxy(DelegateHandler handler, LifecycleManager manager) : base( typeof(DelegateHandler) )
		{
			m_handler = handler;
			m_manager = manager;
		}

		public override IMessage Invoke(IMessage message)
		{
			IMethodCallMessage call = message as IMethodCallMessage;
			IMethodReturnMessage returnMessage = null;

			if (IsGetInstanceCall(call))
			{
				m_manager.BeforeGetInstance(m_handler);

				returnMessage = RemotingServices.ExecuteMessage(m_handler, call);

				object instance = returnMessage.ReturnValue;

				if (instance != null)
				{
					m_manager.AfterGetInstance(m_handler, instance);
				}
			}
			else if (IsPutInstanceCall(call))
			{
				object instance = call.GetArg(0);

				m_manager.BeforePutInstance(m_handler, instance);
				returnMessage = RemotingServices.ExecuteMessage(m_handler, call);
				m_manager.AfterPutInstance(m_handler, instance);
			}
			else
			{
				returnMessage = RemotingServices.ExecuteMessage(m_handler, call);
			}

			return returnMessage;
		}

		private bool IsGetInstanceCall(IMethodCallMessage call)
		{
			if (!IsCommonInstanceCall(call))
			{
				if (call.MethodBase.Name.Equals("GetInstance"))
				{
					return true;
				}
			}

			return false;
		}

		private bool IsPutInstanceCall(IMethodCallMessage call)
		{
			if (!IsCommonInstanceCall(call))
			{
				if (call.MethodBase.Name.Equals("PutInstance"))
				{
					return true;
				}
			}

			return false;
		}

		private bool IsCommonInstanceCall(IMethodCallMessage call)
		{
			if ( call.MethodBase.IsSpecialName )
			{
				return true;
			}

			if ( call.MethodBase.DeclaringType != typeof( IComponentHandler ) )
			{
				return true;
			}

			return false;
		}
	}
}
