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

namespace Apache.Avalon.Castle.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Castle.Test.ManagedComponents;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util.Proxies;

	/// <summary>
	/// Summary description for ManagedObjectProxyGeneratorTestCase.
	/// </summary>
	[TestFixture]
	public class ManagedObjectProxyGeneratorTestCase : CastleDomainTestCaseBase
	{
		private static readonly String SOME_SERVICE_TYPE_NAME = typeof(SomeServiceImpl).FullName + ", Apache.Avalon.Castle.Test";

		private static readonly String SOME_SERVICE_COMPONENT_NAME = "apache.avalon.castle.test:name=SomeService";

		private static readonly ManagedObjectName SOME_SERVICE_MANAGED_NAME = new ManagedObjectName( SOME_SERVICE_COMPONENT_NAME );

		[Test]
		public void TestGeneration()
		{
			ManagedInstance instance = 
				server.CreateManagedObject( SOME_SERVICE_TYPE_NAME, SOME_SERVICE_MANAGED_NAME );

			AssertNotNull( instance );

			object proxy = ManagedObjectProxyGenerator.CreateProxy( 
				SOME_SERVICE_MANAGED_NAME, server, typeof(ISomeService) );

			AssertNotNull( proxy );

			ISomeService serviceProxy = proxy as ISomeService;

			AssertNotNull( serviceProxy );

			AssertEquals( 10, serviceProxy.DoSomething( 2, 5 ) );
			AssertEquals( SomeServiceImpl.VERSION, serviceProxy.Version );
		}
	}
}
