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

namespace Apache.Avalon.Castle.MicroKernel.Test.Lifestyle
{
	using System;
	using System.Threading;

	using NUnit.Framework;

	using Apache.Avalon.Castle.MicroKernel;
	using Apache.Avalon.Castle.MicroKernel.Test.Lifestyle.Components;

	/// <summary>
	/// Summary description for LifestyleManagerTestCase.
	/// </summary>
	[TestFixture]
	public class LifestyleManagerTestCase : Assertion
	{
		private IAvalonKernel m_kernel;

		private IComponent m_instance3;

		[SetUp]
		public void CreateContainer()
		{
			m_kernel = new DefaultAvalonKernel();
		}

		[Test]
		public void TestTransient()
		{
			m_kernel.AddComponent( "a", typeof(IComponent), typeof(TransientComponent) );

			IHandler handler = m_kernel[ "a" ];
			
			IComponent instance1 = handler.Resolve() as IComponent;
			IComponent instance2 = handler.Resolve() as IComponent;

			AssertNotNull( instance1 );
			AssertNotNull( instance2 );

			Assert( !instance1.Equals( instance2 ) );
			Assert( instance1.ID != instance2.ID );

			handler.Release( instance1 );
			handler.Release( instance2 );
		}

		[Test]
		public void TestSingleton()
		{
			m_kernel.AddComponent( "a", typeof(IComponent), typeof(SingletonComponent) );

			IHandler handler = m_kernel[ "a" ];
			
			IComponent instance1 = handler.Resolve() as IComponent;
			IComponent instance2 = handler.Resolve() as IComponent;

			AssertNotNull( instance1 );
			AssertNotNull( instance2 );

			Assert( instance1.Equals( instance2 ) );
			Assert( instance1.ID == instance2.ID );

			handler.Release( instance1 );
			handler.Release( instance2 );
		}

		[Test]
		public void TestPerThread()
		{
			m_kernel.AddComponent( "a", typeof(IComponent), typeof(PerThreadComponent) );

			IHandler handler = m_kernel[ "a" ];
			
			IComponent instance1 = handler.Resolve() as IComponent;
			IComponent instance2 = handler.Resolve() as IComponent;

			AssertNotNull( instance1 );
			AssertNotNull( instance2 );

			Assert( instance1.Equals( instance2 ) );
			Assert( instance1.ID == instance2.ID );

			Thread thread = new Thread( new ThreadStart(OtherThread) );
			thread.Start();
			thread.Join();

			AssertNotNull( m_instance3 );
			Assert( !instance1.Equals( m_instance3 ) );
			Assert( instance1.ID != m_instance3.ID );

			handler.Release( instance1 );
			handler.Release( instance2 );
		}

		private void OtherThread()
		{
			IHandler handler = m_kernel[ "a" ];
			m_instance3 = handler.Resolve() as IComponent;
		}
	}
}
