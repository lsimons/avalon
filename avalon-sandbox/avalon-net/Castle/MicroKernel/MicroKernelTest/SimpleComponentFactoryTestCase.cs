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
	using Apache.Avalon.Castle.MicroKernel.Factory;
	using Apache.Avalon.Castle.MicroKernel.Test.Components;

	/// <summary>
	/// Summary description for SimpleComponentFactoryTestCase.
	/// </summary>
	[TestFixture]
	public class SimpleComponentFactoryTestCase : Assertion
	{
		BaseKernel kernel;

		[SetUp]
		public void CreateKernel()
		{
			kernel = new BaseKernel();
		}

		[Test]
		public void NoArgumentsConstructor()
		{
			Type service = typeof( IMailService );
			Type implementation = typeof( SimpleMailService );

			kernel.AddComponent( "a", service, implementation );

			ConstructorInfo constructor = implementation.GetConstructor( Type.EmptyTypes );
			ConstructionInfo info = new ConstructionInfo( constructor, null );

			SimpleComponentFactory factory = new SimpleComponentFactory( 
				service, implementation, new IAspect[0], new IAspect[0], info );

			Object instance = factory.Incarnate();

			AssertNotNull( instance );
			AssertNotNull( instance as IMailService );

			factory.Etherialize();
		}

		[Test]
		public void DependencyInConstructor()
		{
			Type service = typeof( ISpamService );
			Type implementation = typeof( SimpleSpamService );
			Type serviceDep = typeof( IMailService );
			Type implementationDep = typeof( SimpleMailService );

			kernel.AddComponent( "a", service, implementation );
			kernel.AddComponent( "b", serviceDep, implementationDep );

			ConstructorInfo constructor = 
				implementation.GetConstructor( new Type[] { typeof(IMailService) } );

			Hashtable serv2Handler = new Hashtable();
			serv2Handler[ serviceDep ] = kernel.GetHandlerForService( serviceDep );

			ConstructionInfo info = new ConstructionInfo( constructor, serv2Handler );

			SimpleComponentFactory factory = new SimpleComponentFactory( 
				service, implementation, new IAspect[0], new IAspect[0], info );

			Object instance = factory.Incarnate();

			AssertNotNull( instance );
			AssertNotNull( instance as ISpamService );
			
			SimpleSpamService spamService = (SimpleSpamService) instance;
			AssertNotNull( spamService.m_mailService );

			factory.Etherialize();
		}
	}
}
