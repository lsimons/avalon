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

namespace Apache.Avalon.Castle.MicroKernel.Test
{
	using System;
	using System.Reflection;
	using System.Collections;

	using NUnit.Framework;

	using Apache.Avalon.Castle.MicroKernel;
	using Apache.Avalon.Castle.MicroKernel.Test.Components;

	/// <summary>
	/// Summary description for BaseKernelTestCase.
	/// </summary>
	[TestFixture]
	public class BaseKernelTestCase : Assertion
	{
		/// <summary>
		/// Just a simple Service resolution.
		/// No concerns or aspects involved.
		/// </summary>
		[Test]
		public void SimpleUsage()
		{
			BaseKernel container = new BaseKernel();
			container.AddComponent( "a", typeof(IMailService), typeof(SimpleMailService) );

			IHandler handler = container[ "a" ];

			IMailService service = handler.Resolve() as IMailService;

			AssertNotNull( service );

			service.Send("hammett at apache dot org", "johndoe at yahoo dot org", "Aloha!", "What's up?");

			handler.Release( service );
		}

		[Test]
		public void ComponentDependingOnLogger()
		{
			BaseKernel container = new BaseKernel();
			container.AddComponent( "a", typeof(IMailService), typeof(SimpleMailServiceWithLogger) );

			IHandler handler = container[ "a" ];

			IMailService service = handler.Resolve() as IMailService;

			AssertNotNull( service );

			service.Send("hammett at apache dot org", 
				"johndoe at yahoo dot org", "Aloha!", "What's up?");

			handler.Release( service );
		}

		/*
		[Test]
		public void FacilityEvents()
		{
			BaseKernel container = new BaseKernel();
			MockFacility facility = new MockFacility();

			container.RegisterFacility( facility );

			Assert( facility.OnInitCalled );

			container.AddComponent( "a", typeof(IMailService), typeof(SimpleMailServiceWithLogger) );

			Assert( facility.ComponentAddedCalled );

			IHandler handler = container[ "a" ];

			IMailService service = handler.Resolve() as IMailService;

			Assert( facility.ComponentCreatedCalled );

			AssertNotNull( service );

			service.Send("hammett at apache dot org", 
				"johndoe at yahoo dot org", "Aloha!", "What's up?");

			handler.Release( service );

			Assert( facility.ComponentReleasedCalled );
		}

		public class MockFacility : IContainerFacility
		{
			
		}
		*/
	}
}
