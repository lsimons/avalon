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

namespace Apache.Avalon.Castle.Default.Runtime
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Composition.Model;


	/// <summary>
	/// Summary description for DefaultRuntime.
	/// </summary>
	[ManagedComponent]
	public class DefaultRuntime : ManagedService, IRuntime
	{
		public DefaultRuntime()
		{
		}
	
		#region IRuntime Members

		[ManagedOperation]
		public void Decommission(IDeploymentModel model)
		{
			// TODO:  Add DefaultRuntime.Decommission implementation
		}

		[ManagedOperation]
		public void Commission(IDeploymentModel model)
		{
			// TODO:  Add DefaultRuntime.Commission implementation
		}

		[ManagedOperation]
		public void Release(IDeploymentModel model, object instance)
		{
			// TODO:  Add DefaultRuntime.Release implementation
		}

		[ManagedOperation]
		public object Resolve(IDeploymentModel model)
		{
			// TODO:  Add DefaultRuntime.Resolve implementation
			return null;
		}

		#endregion
	}
}
