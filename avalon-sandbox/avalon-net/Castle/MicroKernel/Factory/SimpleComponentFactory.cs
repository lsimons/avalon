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

namespace Apache.Avalon.Castle.MicroKernel.Factory
{
	using System;
	using System.Reflection;

	/// <summary>
	/// Summary description for SimpleComponentFactory.
	/// </summary>
	public class SimpleComponentFactory : IComponentFactory
	{
		protected Type m_service;
		protected Type m_implementation;
		protected IAspect[] m_before;
		protected IAspect[] m_after;
		protected ConstructionInfo m_info;

		public SimpleComponentFactory( Type service, Type implementation, 
			IAspect[] before, IAspect[] after, 
			ConstructionInfo info )
		{
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( implementation, "implementation" );
			AssertUtil.ArgumentNotNull( before, "before" );
			AssertUtil.ArgumentNotNull( after, "after" );
			AssertUtil.ArgumentNotNull( info, "info" );

			m_service = service;
			m_implementation = implementation;
			m_before = before;
			m_after = after;
			m_info = info;
		}

		#region IComponentFactory Members

		public Object Incarnate()
		{
			try
			{
				object[] arguments = BuildArguments();

				Object instance = Activator.CreateInstance( m_implementation, arguments );

				if (m_before.Length != 0 || m_after.Length != 0 )
				{
					instance = DynamicProxy.ProxyGenerator.CreateProxy( 
						new Type[] { m_service }, 
						new AspectInvocationHandler( m_before, m_after, instance ) ); 
				}

				foreach( PropertyInfo property in m_info.Properties )
				{
					IHandler handler = m_info[ property.PropertyType ];
					property.SetValue( instance, handler.Resolve(), null);
				}

				return instance;
			}
			catch(Exception ex)
			{
				throw new HandlerException("Exception while attempting to instantiate type", ex);
			}		
		}

		public void Etherialize()
		{
		}

		#endregion

		protected object[] BuildArguments()
		{
			// TODO: Enqueue handlers for dispose in Etherialize()

			ParameterInfo[] parameters = m_info.Constructor.GetParameters();
			object[] args = new object[ parameters.Length ];

			for(int i=0; i < args.Length; i++)
			{
				ParameterInfo parameter = parameters[i];
				Type service = parameter.ParameterType;
				IHandler handler = m_info[ service ];
				args[ parameter.Position ] = handler.Resolve();
			}

			return args;
		}
	}
}
