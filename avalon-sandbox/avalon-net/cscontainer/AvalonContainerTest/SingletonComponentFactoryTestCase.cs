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
	/// Summary description for SingletonComponentFactoryTestCase.
	/// </summary>
	[TestFixture]
	public class SingletonComponentFactoryTestCase : ContainerTestCase
	{
		[Test]
		public void SingletonBehavior()
		{
			ICalculatorSingleton singleton = (ICalculatorSingleton)
				m_container.LookupManager[ typeof(ICalculatorSingleton).FullName ];

			Assertion.AssertNotNull(singleton);
			Assertion.AssertEquals(30, singleton.Add(10,20));

			m_container.LookupManager.Release(singleton);

			singleton = (ICalculatorSingleton) 
				m_container.LookupManager[ typeof(ICalculatorSingleton).FullName ];

			Assertion.AssertNotNull(singleton);
			Assertion.AssertEquals(30, singleton.LastResult);
		}
	}
}
