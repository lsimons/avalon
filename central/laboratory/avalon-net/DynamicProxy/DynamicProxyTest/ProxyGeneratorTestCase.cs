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

namespace Apache.Avalon.DynamicProxy.Test
{
	using System;
	using System.Reflection;

	using NUnit.Framework;

	using Apache.Avalon.DynamicProxy;

	/// <summary>
	/// Summary description for ProxyGeneratorTestCase.
	/// </summary>
	[TestFixture]
	public class ProxyGeneratorTestCase : Assertion
	{
		[Test]
		public void TestGenerationSimpleInterface()
		{
			object proxy = ProxyGenerator.CreateProxy( 
				typeof(IMyInterface), new StandardInvocationHandler( new MyInterfaceImpl() ) );
			
			AssertNotNull( proxy );
			Assert( typeof(IMyInterface).IsAssignableFrom( proxy.GetType() ) );

			IMyInterface inter = (IMyInterface) proxy;

			AssertEquals( 45, inter.Calc( 20, 25 ) );

			inter.Name = "opa";
			AssertEquals( "opa", inter.Name );

			inter.Started = true;
			AssertEquals( true, inter.Started );
		}

		[Test]
		public void TestGenerationWithInterfaceHeritage()
		{
			object proxy = ProxyGenerator.CreateProxy( 
				typeof(IMySecondInterface), new StandardInvocationHandler( new MySecondInterfaceImpl() ) );

			AssertNotNull( proxy );
			Assert( typeof(IMyInterface).IsAssignableFrom( proxy.GetType() ) );
			Assert( typeof(IMySecondInterface).IsAssignableFrom( proxy.GetType() ) );

			IMySecondInterface inter = (IMySecondInterface) proxy;
			inter.Calc(1, 1);

			inter.Name = "hammett";
			AssertEquals( "hammett", inter.Name );

			inter.Address = "pereira leite, 44";
			AssertEquals( "pereira leite, 44", inter.Address );
			
			AssertEquals( 45, inter.Calc( 20, 25 ) );
		}

		[Test]
		public void TestEnumProperties()
		{
			ServiceStatusImpl service = new ServiceStatusImpl();

			object proxy = ProxyGenerator.CreateProxy( 
				typeof(IServiceStatus), new StandardInvocationHandler( service ) );
			
			AssertNotNull( proxy );
			Assert( typeof(IServiceStatus).IsAssignableFrom( proxy.GetType() ) );

			IServiceStatus inter = (IServiceStatus) proxy;
			AssertEquals( State.Invalid, inter.ActualState );
			
			inter.ChangeState( State.Valid );
			AssertEquals( State.Valid, inter.ActualState );
		}

		public class MyInterfaceProxy : IInvocationHandler
		{
			#region IInvocationHandler Members

			public object Invoke(object proxy, MethodInfo method, params object[] arguments)
			{
				return null;
			}

			#endregion
		}
	}
}

