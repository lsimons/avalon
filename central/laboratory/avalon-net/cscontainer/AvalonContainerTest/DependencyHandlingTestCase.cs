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

namespace Apache.Avalon.Container.Test
{
	using System;
	using NUnit.Framework;

	using Apache.Avalon.Container.Test.Components;

	/// <summary>
	/// Summary description for DependencyHandlingTestCase.
	/// </summary>
	[TestFixture]
	public class DependencyHandlingTestCase : ContainerTestCase
	{
		[Test]
		public void ShutDownOrderCheck()
		{
			Assertion.AssertEquals(10, m_container.ShutDownOrder.Length);

			Assertion.AssertEquals(typeof(IEntityLocator).FullName, m_container.ShutDownOrder[0].Name);
			Assertion.AssertEquals(typeof(IBus).FullName, m_container.ShutDownOrder[1].Name);
			Assertion.AssertEquals(typeof(IVehicle).FullName, m_container.ShutDownOrder[2].Name);
			Assertion.AssertEquals(typeof(IRadio).FullName, m_container.ShutDownOrder[3].Name);
			Assertion.AssertEquals(typeof(IEngine).FullName, m_container.ShutDownOrder[4].Name);
		}

		[Test]
		public void ComponentWithDependency()
		{
			IVehicle vehicle = null;
			
			try
			{
				vehicle = (IVehicle) m_container.LookupManager[ typeof(IVehicle).FullName ];
			}
			finally
			{
				if (vehicle != null)
				{
					m_container.LookupManager.Release(vehicle);
				}
			}
		}

		[Test]
		public void ComponentWithInheritedDependency()
		{
			IBus bus = null;

			try
			{
				bus = (IBus) m_container.LookupManager[ typeof(IBus).FullName ];

				bus.Travel();
			}
			finally
			{
				if (bus != null)
				{
					m_container.LookupManager.Release(bus);
				}
			}
		}
	}
}
