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
    using Apache.Avalon.Castle.MicroKernel.Model;
    using Apache.Avalon.Castle.MicroKernel.Test.Components;

	/// <summary>
    /// Summary description for KernelEventsTestCase.
    /// </summary>
	[TestFixture]
	public class KernelEventsTestCase : Assertion
	{
		private bool m_componentAdded = false;
		private bool m_componentCreated = false;
		private bool m_componentDestroyed = false;

		[Test]
		public void TestComponentAddedEvent()
		{
			IKernel kernel = new DefaultAvalonKernel();

            kernel.ComponentAdded += new ComponentDataDelegate(OnComponentAdded);

            kernel.AddComponent( "key", typeof(IMailService), typeof(SimpleMailService) );

			Assert( m_componentAdded );
		}

        public void OnComponentAdded(IComponentModel model, String key, Type service, Type implementation, IHandler handler)
        {
			m_componentAdded = true;
		}

        /*
		[Test]
		public void TestComponentCreatedEvent()
		{
			IKernel kernel = new DefaultAvalonKernel();

            kernel.ComponentCreated += new KernelDelegate(OnComponentCreated);

            kernel.AddComponent( "key", typeof(IMailService), typeof(SimpleMailService) );

			Assert( !m_componentCreated );

			kernel[ "key" ].Resolve();

			Assert( m_componentCreated );
		}

    	public void OnComponentCreated( EventManagerData data )
		{
			m_componentCreated = true;
		}*/

        /*
		[Test]
		public void TestComponentDestroyedEvent()
		{
			IKernel kernel = new DefaultAvalonKernel();

			IEventManager eventManager = (IEventManager) kernel.GetSubsystem( KernelConstants.EVENTS );
			eventManager.ComponentDestroyed += new KernelDelegate(OnComponentDestroyed);

			kernel.AddComponent( "key", typeof(IMailService), typeof(SimpleMailService) );

			Assert( !m_componentDestroyed );

			object instance = kernel[ "key" ].Resolve();

			Assert( !m_componentDestroyed );

			kernel[ "key" ].Release( instance );

			Assert( m_componentDestroyed );
		}

		public void OnComponentDestroyed( EventManagerData data )
		{
			m_componentDestroyed = true;
		}
        */
    }
}
