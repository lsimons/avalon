using Apache.Avalon.DynamicProxy.Test.Classes;
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
	using System.Reflection.Emit;

	using NUnit.Framework;

	using Apache.Avalon.DynamicProxy.Test.ClassInterfaces;

	/// <summary>
	/// Summary description for CustomProxyGeneratorTestCase.
	/// </summary>
	[TestFixture]
	public class CustomProxyGeneratorTestCase : Assertion
	{
		private ProxyGenerator m_generator;
		private bool m_enhanceInvoked;
		private bool m_screenInvoked;

		[SetUp]
		public void Init()
		{
			m_generator = new ProxyGenerator();
			m_enhanceInvoked = false;
			m_screenInvoked = false;
		}

		[Test]
		public void CreateCustomProxy()
		{
			GeneratorContext context = new GeneratorContext( 
				new EnhanceTypeDelegate(EnhanceType),
				new ScreenInterfacesDelegate(ScreenInterfaces));

			object proxy = m_generator.CreateCustomProxy(
				typeof (IMyInterface), 
				new StandardInvocationHandler(new MyInterfaceImpl()), context);

			Assert( m_enhanceInvoked );
			Assert( m_screenInvoked );
		}

		[Test]
		public void CreateCustomClassProxy()
		{
			GeneratorContext context = new GeneratorContext( 
				new EnhanceTypeDelegate(EnhanceType),
				new ScreenInterfacesDelegate(ScreenInterfaces));

			object proxy = m_generator.CreateCustomClassProxy(
				typeof (ServiceClass), 
				new StandardInvocationHandler(new ServiceClass()),
				context);

			Assert( m_enhanceInvoked );
			Assert( m_screenInvoked );
		}

		private void EnhanceType(TypeBuilder mainType, FieldBuilder handlerFieldBuilder, ConstructorBuilder constructorBuilder)
		{
			Assert( !m_enhanceInvoked );

			AssertNotNull(mainType);
			AssertNotNull(handlerFieldBuilder);
			AssertNotNull(constructorBuilder);

			m_enhanceInvoked = true;
		}

		private Type[] ScreenInterfaces(Type[] interfaces)
		{
			Assert( !m_screenInvoked );

			AssertNotNull(interfaces);

			m_screenInvoked = true;

			return interfaces;
		}
	}
}