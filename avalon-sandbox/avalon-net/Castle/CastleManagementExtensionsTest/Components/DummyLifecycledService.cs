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

namespace Apache.Avalon.Castle.ManagementExtensions.Test.Components
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for DummyLifecycledService.
	/// </summary>
	[ManagedComponent]
	public class DummyLifecycledService : MRegistrationListener
	{
		private int state = 0;
		
		public int beforeRegisterCalled;
		public int afterRegisterCalled;
		public int beforeDeregisterCalled;
		public int afterDeregisterCalled;

		public DummyLifecycledService()
		{
		}

		[ManagedOperation]
		public void Start()
		{
		}

		[ManagedOperation]
		public void Stop()
		{
		}

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			Assertion.AssertNotNull(server);
			Assertion.AssertNotNull(name);

			beforeRegisterCalled = state++;
		}

		public void AfterDeregister()
		{
			afterDeregisterCalled = state++;
		}

		public void AfterRegister()
		{
			afterRegisterCalled = state++;
		}

		public void BeforeDeregister()
		{
			beforeDeregisterCalled = state++;
		}

		#endregion
	}
}
