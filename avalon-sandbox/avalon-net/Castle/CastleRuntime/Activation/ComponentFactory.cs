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

namespace Apache.Avalon.Castle.Runtime.Extended.Activation
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Activation;
	using Apache.Avalon.Composition.Model;


	/// <summary>
	/// Summary description for ComponentFactory.
	/// </summary>
	public class ComponentFactory : IComponentFactory
	{
		protected MServer m_server;
		
		protected IComponentModel m_model;

		public ComponentFactory( MServer server, IComponentModel model )
		{
			Assert.ArgumentNotNull( server, "server" );
			Assert.ArgumentNotNull( model, "model" );

			m_server = server;
			m_model  = model;
		}

		#region IComponentFactory Members

		public object Incarnate()
		{
			try
			{
				return MXUtil.InvokeOn( m_server, 
					CastleConstants.ORCHESTRATOR_NAME, "InvokeCreatePhases", m_model );
			}
			catch( Exception e )
			{
				String error = "lifestyle.error.new " +  m_model.QualifiedName;
				throw new LifecycleException( error, e );
			}		
		}

		public void Etherialize(object instance)
		{
			try
			{
				MXUtil.InvokeOn( m_server, 
					CastleConstants.ORCHESTRATOR_NAME, "InvokeDestructionPhases", m_model );
			}
			catch( Exception e )
			{
				String error = "lifestyle.error.new " +  m_model.QualifiedName;
				throw new LifecycleException( error, e );
			}	
		}

		#endregion
	}
}
