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

		/// <summary>
		/// Adds a InterceptionAspect to calling chain and check if is has been called
		/// during method execution
		/// </summary>
		[Test]
		public void AddingAspect()
		{
			BaseKernel container = new BaseKernel();

			InterceptionAspect aspect = new InterceptionAspect();

			container.AddAspect( 
				AspectPointCutFlags.Before|AspectPointCutFlags.After, 
				aspect );

			container.AddComponent( "a", typeof(IMailService), typeof(SimpleMailService) );

			IHandler handler = container[ "a" ];

			IMailService service = handler.Resolve() as IMailService;

			AssertNotNull( service );

			service.Send("hammett at apache dot org", "johndoe at yahoo dot org", "Aloha!", "What's up?");

			handler.Release( service );

			AssertEquals( 2, aspect.m_invocations.Count );
			AssertEquals( "Before Send", aspect.m_invocations[0] );
			AssertEquals( "After Send", aspect.m_invocations[1] );
		}

		public class InterceptionAspect : IAspect
		{
			public ArrayList m_invocations = new ArrayList();

			public void Perform( AspectPointCutFlags pointcut, 
				object componentInstance, 
				MethodBase method, 
				object returnValue, 
				Exception exception, 
				params object[] arguments)
			{
				if (pointcut == AspectPointCutFlags.Before)
				{
					m_invocations.Add("Before " + method.Name);
				}
				else if (pointcut == AspectPointCutFlags.After)
				{
					m_invocations.Add("After " + method.Name);
				}
			}
		}
	}
}
