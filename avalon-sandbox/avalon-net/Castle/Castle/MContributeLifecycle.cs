// Copyright 2003-2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle
{
	using System;

	using Apache.Avalon.Castle.Core;

	/// <summary>
	/// Summary description for MContributeLifecycle.
	/// </summary>
	public interface MContributeLifecycle : MService
	{
		/// <summary>
		/// Gives the implementation a chance to register for lifecycle
		/// phases.
		/// </summary>
		/// <param name="notification"></param>
		void RegisterForPhases(OrchestratorNotificationSystem notification);

		/// <summary>
		/// I know a simple object won't be enough when things become 
		/// more complex, but lets keep this simple for a minute
		/// </summary>
		/// <param name="target"></param>
		/// <returns></returns>
		void Perform(object target);
	}
}
