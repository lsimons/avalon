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

namespace Apache.Avalon.Activation.Default
{
	using System;

	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for DefaultRuntime.
	/// </summary>
	public class DefaultRuntime : IRuntime
	{
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------

		private IRuntimeFactory m_runtimeFactory;

		/// <summary>
		/// Creation of a new system context.
		/// </summary>
		/// <param name="system">the system context</param>
		public DefaultRuntime( ISystemContext system )
		{
			if( system == null )
			{
				throw new ArgumentNullException( "system" );
			}
			m_runtimeFactory = new DefaultRuntimeFactory( system );
		}

		#region IRuntime Members

		public void Decommission(IDeploymentModel model)
		{
			RuntimeFactory.GetRuntime( model ).Decommission();
		}

		public void Commission(IDeploymentModel model)
		{
			RuntimeFactory.GetRuntime( model ).Commission();
		}

		public void Release(IDeploymentModel model, object instance)
		{
			RuntimeFactory.GetRuntime( model ).Release( instance );
		}

		public object Resolve(IDeploymentModel model)
		{
			return RuntimeFactory.GetRuntime( model ).Resolve();;
		}

		#endregion

		protected IRuntimeFactory RuntimeFactory
		{
			get
			{
				return m_runtimeFactory;
			}
		}
	}
}
