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

namespace Apache.Avalon.Castle.Test.Logger
{
	using System;

	using Apache.Avalon.Castle;
	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for DummyLoggerManager.
	/// </summary>
	[ManagedComponent]
	public class DummyLoggerManager : ManagedService, MContributeLifecycle
	{
		String status = String.Empty;

		public DummyLoggerManager()
		{
		}

		[ManagedOperation]
		public String GetStatus()
		{
			return status;
		}

		#region MContributeLifecycle Members

		[ManagedOperation]
		public void RegisterForPhases(Apache.Avalon.Castle.Core.OrchestratorNotificationSystem notification)
		{
			notification.AddListener(ManagedObjectName, LifecyclePhase.EnableLogging);

			status = "OK";
		}

		[ManagedOperation]
		public void Perform(object target)
		{
		}

		#endregion
	}
}
