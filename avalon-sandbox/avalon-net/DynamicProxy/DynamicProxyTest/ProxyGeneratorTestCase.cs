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
	public class ProxyGeneratorTestCase : Assertion, IInvocationHandler, IMyInterface
	{
		protected String nameProperty;

		[Test]
		public void TestSimpleCase()
		{
			object proxy = ProxyGenerator.CreateProxy( new Type[] { typeof(IMyInterface) }, this );
			AssertNotNull( proxy );
			Assert( typeof(IMyInterface).IsAssignableFrom( proxy.GetType() ) );

			IMyInterface inter = (IMyInterface) proxy;
			inter.Calc(1, "ola");
			inter.Nome = "opa";
			AssertEquals( "opa", inter.Nome );
			AssertEquals( 45, inter.Calc( 20, 25 ) );

		}

		#region IInvocationHandler Members

		public object Invoke(object obj, MethodBase method, params object[] arguments)
		{
			Type[] parameters = new Type[arguments.Length];
			
			for(int i=0; i < arguments.Length; i++ )
			{
				parameters[i] = arguments[i].GetType();
			}

			MethodInfo ourMethod = this.GetType().GetMethod( method.Name, parameters );

			AssertNotNull( ourMethod );
			
			return ourMethod.Invoke( this, arguments );
		}

		#endregion

		#region IMyInterface Members

		public String Nome
		{
			get
			{
				return nameProperty;
			}
			set
			{
				nameProperty = value;
			}
		}

		public void Calc(int x, String y)
		{
		}

		public void Calc(int x, String y, Single ip)
		{
		}

		public int Calc(int x, int y)
		{
			return x + y;
		}

		public int Calc(int x, int y, int z, Single h)
		{
			return x + y + z + (int)h;
		}

		#endregion
	}

	/// <summary>
	/// 
	/// </summary>
	public interface IMyInterface
	{
		String Nome
		{
			get;
			set;
		}

		void Calc(int x, String y);

		void Calc(int x, String y, Single ip);

		int Calc(int x, int y);

		int Calc(int x, int y, int z, Single h);
	}

	public class MyTest : IMyInterface
	{
		IInvocationHandler handler = null;

		#region IMyInterface Members

		public String Nome
		{
			get
			{
				// TODO:  Add MyTest.Nome getter implementation
				return null;
			}
			set
			{
				// TODO:  Add MyTest.Nome setter implementation
			}
		}

		public void Calc(int x, String y)
		{
			MethodBase method = MethodBase.GetCurrentMethod();
			handler.Invoke( this, method, x, y );
		}

		public void Calc(int x, String y, Single ip)
		{
			MethodBase method = MethodBase.GetCurrentMethod();
			handler.Invoke( this, method, x, y, ip );
		}

		public int Calc(int x, int y)
		{
			MethodBase method = MethodBase.GetCurrentMethod();
			return (int) handler.Invoke( this, method, x, y );
		}

		public int Calc(int x, int y, int z, Single h)
		{
			MethodBase method = MethodBase.GetCurrentMethod();
			return (int) handler.Invoke( this, method, x, y, h );
		}

		#endregion

	}

}

