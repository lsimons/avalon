// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
//
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
//
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
//
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
//    must not be used to endorse or promote products derived from this  software
//    without  prior written permission. For written permission, please contact
//    apache@apache.org.
//
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
//
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

namespace Apache.Avalon.Container.Handler
{
	using System;
	using System.Runtime.Remoting;
	using System.Runtime.Remoting.Messaging;
	using System.Runtime.Remoting.Proxies;

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

				m_manager.AfterGetInstance(m_handler, instance);
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
