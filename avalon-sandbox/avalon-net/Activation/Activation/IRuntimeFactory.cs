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

namespace Apache.Avalon.Activation
{
	using System;

	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// A factory enabling the establishment of runtime handlers.
	/// </summary>
	public interface IRuntimeFactory
	{
		/// <summary>
		/// Resolve a runtime handler for a model.
		/// </summary>
		/// <param name="model">the deployment model</param>
		/// <returns>the runtime appliance</returns>
		IAppliance GetRuntime( IDeploymentModel model );
	}
}
