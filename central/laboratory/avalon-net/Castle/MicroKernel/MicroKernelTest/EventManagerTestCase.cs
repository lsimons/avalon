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

	using NUnit.Framework;

	using Apache.Avalon.Castle.MicroKernel;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Events;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Events.Default;

	/// <summary>
	/// Summary description for EventManagerTestCase.
	/// </summary>
	[TestFixture]
	public class EventManagerTestCase : Assertion
	{
		private IKernel m_kernel;
		private bool m_invoked = false;

		[SetUp]
		public void CreateKernel()
		{
			m_kernel = new DefaultAvalonKernel();
		}

		[Test]
		public void TestUsage()
		{
			IEventManager manager = new EventManager();
			manager.Init( m_kernel );

			manager.ComponentAdded += new KernelDelegate(TheEvent);
			manager.ComponentDestroyed += new KernelDelegate(TheEvent);
			
			Assert( !m_invoked );
			
			manager.OnComponentAdded( new EventManagerData( "key", typeof(Assertion), typeof(Assertion) ) );
			
			Assert( m_invoked );
			m_invoked = false;

			manager.OnComponentCreated( new EventManagerData( "key", typeof(Assertion), typeof(Assertion) ) );

			Assert( !m_invoked );

			manager.OnComponentDestroyed( new EventManagerData( "key", typeof(Assertion), typeof(Assertion) ) );
			
			Assert( m_invoked );
		}

		public void TheEvent( EventManagerData data )
		{
			m_invoked = true;
		}
	}
}
