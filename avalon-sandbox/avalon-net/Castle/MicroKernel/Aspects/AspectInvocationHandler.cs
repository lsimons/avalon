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

namespace Apache.Avalon.Castle.MicroKernel.Aspects
{
	using System;
	using System.Reflection;

	/// <summary>
	/// Summary description for AspectInvocationHandler.
	/// </summary>
	public class AspectInvocationHandler : DynamicProxy.IInvocationHandler
	{
		private Object m_target;
		
		protected IAspect[] m_before;
		protected IAspect[] m_after;

		public AspectInvocationHandler( IAspect[] before, IAspect[] after, Object target )
		{
			m_before = before;
			m_after  = after;
			m_target = target;
		}

		#region IInvocationHandler Members

		public object Invoke(object proxy, MethodBase method, params object[] arguments)
		{
			Type[] parameters = new Type[arguments.Length];

			for(int i=0; i < arguments.Length; i++ )
			{
				parameters[i] = arguments[i].GetType();
			}

			MethodInfo targetMethod = m_target.GetType().GetMethod( method.Name, parameters );
			
			Object returnValue = null;
			Exception exceptionThrowed = null;

			Perform( m_before, AspectPointCutFlags.Before, method, returnValue, exceptionThrowed, arguments );

			try
			{
				targetMethod.Invoke( m_target, arguments );
			}
			catch(Exception ex)
			{
				exceptionThrowed = ex;
			}

			Perform( m_after, AspectPointCutFlags.After, method, returnValue, exceptionThrowed, arguments );

			return null;
		}

		#endregion

		protected void Perform( IAspect[] aspects, AspectPointCutFlags pointcut, MethodBase method, 
			Object returnValue, Exception exceptionThrowed, params object[] arguments )
		{
			foreach( IAspect aspect in aspects )
			{
				try
				{
					aspect.Perform( pointcut, m_target, method, returnValue, exceptionThrowed, arguments );
				}
				catch(Exception)
				{
					// Exceptions throwed while executing aspects will be
					// ignored
				}
			}
		}
	}
}
