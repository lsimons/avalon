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

namespace Apache.Avalon.Castle.Core.Proxies
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for RuntimeProxy.
	/// </summary>
	public class RuntimeProxy : AbstractManagedObjectProxy, IRuntime
	{
		public RuntimeProxy( MServer server, ManagedObjectName name ) : base( server, name )
		{
		}
	
		#region IRuntime Members

		public void Decommission(IDeploymentModel model)
		{
			MXUtil.InvokeOn ( server, target, "Decommission", model );
		}

		public void Commission(IDeploymentModel model)
		{
			MXUtil.InvokeOn ( server, target, "Commission", model );
		}

		public void Release(IDeploymentModel model, object instance)
		{
			MXUtil.InvokeOn ( server, target, "Release", model, instance );
		}

		public object Resolve(IDeploymentModel model)
		{
			return MXUtil.InvokeOn ( server, target, "Resolve", model );;
		}

		#endregion
	}
}
