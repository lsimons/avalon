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

namespace Apache.Avalon.Castle.MicroKernel.Factory.Default
{
	using System;
	using System.Collections;
	using System.Reflection;

	using Apache.Avalon.Castle.MicroKernel.Assemble;
	using Apache.Avalon.Castle.MicroKernel.Handler;
	using Apache.Avalon.Castle.MicroKernel.Model;

	/// <summary>
	/// Summary description for SimpleComponentFactory.
	/// </summary>
	public class SimpleComponentFactory : IComponentFactory
	{
		// protected IAspect[] m_before;
		// protected IAspect[] m_after;
		protected IComponentModel m_componentModel;
		protected Hashtable m_serv2handler;
		private Hashtable m_instances = new Hashtable();

		public SimpleComponentFactory( /*IAspect[] before, IAspect[] after, */
			IComponentModel componentModel,
			Hashtable serv2handler)
		{
			// AssertUtil.ArgumentNotNull( before, "before" );
			// AssertUtil.ArgumentNotNull( after, "after" );
			AssertUtil.ArgumentNotNull( componentModel, "componentModel" );
			AssertUtil.ArgumentNotNull( serv2handler, "serv2handler" );

			// m_before = before;
			// m_after = after;
			m_componentModel = componentModel;
			m_serv2handler = serv2handler;
		}

		#region IComponentFactory Members

		public virtual Object Incarnate()
		{
			try
			{
				ComponentInstanceBurden burden = new ComponentInstanceBurden();

				object[] arguments = BuildArguments(burden);

				Object instance = Activator.CreateInstance( m_componentModel.ConstructionModel.Implementation, arguments );

				/*
				if (m_before.Length != 0 || m_after.Length != 0 )
				{
					instance = DynamicProxy.ProxyGenerator.CreateProxy( 
						new Type[] { m_componentModel.Service }, 
						new Aspects.AspectInvocationHandler( m_before, m_after, instance ) ); 
				}*/

				SetupProperties( instance, burden );

				AssociateBurden( instance, burden );

				return instance;
			}
			catch(Exception ex)
			{
				throw new HandlerException("Exception while attempting to instantiate type", ex);
			}		
		}

		public virtual void Etherialize( object instance )
		{
			if (instance == null)
			{
				return;
			}

			ReleaseBurden( instance );
		}

		#endregion

		private void AssociateBurden( object instance, ComponentInstanceBurden burden )
		{
			if ( burden.HasBurden )
			{
				m_instances.Add( instance, burden );
			}
		}

		private void ReleaseBurden( object instance )
		{
			if (m_instances.ContainsKey( instance ))
			{
				ComponentInstanceBurden burden = 
					(ComponentInstanceBurden) m_instances[ instance ];

				burden.ReleaseBurden();

				m_instances.Remove( instance );
			}
		}

		protected virtual object[] BuildArguments(ComponentInstanceBurden burden)
		{
			return Assembler.BuildConstructorArguments( 
				m_componentModel, burden, new ResolveTypeHandler( ResolveType ));
		}

		protected virtual void SetupProperties( object instance, ComponentInstanceBurden burden )
		{
			Assembler.AssembleProperties( 
				instance, m_componentModel, burden, new ResolveTypeHandler( ResolveType ));
		}

		private void ResolveType( IComponentModel model, Type typeRequest, 
			String argumentOrPropertyName, object key, out object value )
		{
			value = null;

			if (m_serv2handler.ContainsKey( typeRequest ))
			{
				IHandler handler = (IHandler) m_serv2handler[ typeRequest ];
				value = handler.Resolve();

				ComponentInstanceBurden burden = (ComponentInstanceBurden) key;
				burden.AddBurden( value, handler );
			}
		}
	}
}
