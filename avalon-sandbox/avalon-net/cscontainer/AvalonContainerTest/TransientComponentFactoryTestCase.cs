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

namespace Apache.Avalon.Container.Test
{
	using System;
	using System.Security.Permissions;
	using System.IO;
	using System.Reflection;
	using NUnit.Framework;

	using Apache.Avalon.Container.Configuration;
	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Test.Components;

	/// <summary>
	/// Summary description for TransientComponentFactoryTestCase.
	/// </summary>
	[TestFixture]
	public class TransientComponentFactoryTestCase : ContainerTestCase
	{
		[Test]
		public void TransientBehavior()
		{
			ICalculator transient = (ICalculator)
				m_container.LookupManager[ typeof(ICalculator).FullName ];

			Assertion.AssertNotNull(transient);
			Assertion.AssertEquals(30, transient.Add(10,20));

			ICalculator transient2 = (ICalculator)
				m_container.LookupManager[ typeof(ICalculator).FullName ];

			Assertion.AssertNotNull(transient2);
			Assertion.AssertEquals(40, transient2.Add(20,20));

			Assertion.Assert( transient2 != transient );

			m_container.LookupManager.Release(transient);
			m_container.LookupManager.Release(transient2);
		}
	}
}
