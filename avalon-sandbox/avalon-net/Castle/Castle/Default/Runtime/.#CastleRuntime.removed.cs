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

namespace Apache.Avalon.Castle.Default.Runtime
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Activation.Default;

	/// <summary>
	/// Summary description for CastleRuntime.
	/// </summary>
	[ManagedComponent]
	public class CastleRuntime : ManagedService, IRuntime
	{
		private ISystemContext m_context;

		public CastleRuntime()
		{
		}

		#region IRuntime Members

		[ManagedOperation]
		public void Decommission(IDeploymentModel model)
		{
			// m_runtime.Decommission( model );
		}

		[ManagedOperation]
		public void Commission(IDeploymentModel model)
		{
			// m_runtime.Commission( model );
		}

		[ManagedOperation]
		public void Release(IDeploymentModel model, object instance)
		{
			// m_runtime.Release( model, instance );
		}

		[ManagedOperation]
		public object Resolve(IDeploymentModel model)
		{
			// return m_runtime.Resolve( model );;
			return null;
		}

		#endregion
	
		public override void Start()
		{
			base.Start();

			m_context = (ISystemContext) MXUtil.GetAttribute( 
				Server, CastleConstants.ORCHESTRATOR_NAME, "SystemContext" );
		}
	}
}
