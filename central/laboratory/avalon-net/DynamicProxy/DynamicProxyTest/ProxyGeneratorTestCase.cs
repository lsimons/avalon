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
	public class ProxyGeneratorTestCase : Assertion, IInvocationHandler, IMyInterface, IMySecondInterface, IServiceStatus
	{
		protected String m_nameProperty;
		protected String m_addressProperty;
		protected State m_state = State.Invalid;
		protected bool m_started;

		[Test]
		public void TestSimpleCase()
		{
			object proxy = ProxyGenerator.CreateProxy( new Type[] { typeof(IMyInterface) }, this );
			AssertNotNull( proxy );
			Assert( typeof(IMyInterface).IsAssignableFrom( proxy.GetType() ) );

			IMyInterface inter = (IMyInterface) proxy;
			inter.Calc(1, "ola");
			inter.Name = "opa";
			AssertEquals( "opa", inter.Name );
			inter.Started = true;
			AssertEquals( true, inter.Started );
			AssertEquals( 45, inter.Calc( 20, 25 ) );
		}

		[Test]
		public void TestMoreComplexCase()
		{
			object proxy = ProxyGenerator.CreateProxy( new Type[] { typeof(IMySecondInterface) }, this );
			AssertNotNull( proxy );
			Assert( typeof(IMyInterface).IsAssignableFrom( proxy.GetType() ) );
			Assert( typeof(IMySecondInterface).IsAssignableFrom( proxy.GetType() ) );

			IMySecondInterface inter = (IMySecondInterface) proxy;
			inter.Calc(1, "ola");
			inter.Name = "opa";
			AssertEquals( "opa", inter.Name );
			inter.Address = "pereira leite, 44";
			AssertEquals( "pereira leite, 44", inter.Address );
			AssertEquals( 45, inter.Calc( 20, 25 ) );
		}

		[Test]
		public void TestEnumCase()
		{
			m_state = State.Invalid;

			object proxy = ProxyGenerator.CreateProxy( new Type[] { typeof(IServiceStatus) }, this );
			AssertNotNull( proxy );
			Assert( typeof(IServiceStatus).IsAssignableFrom( proxy.GetType() ) );

			IServiceStatus inter = (IServiceStatus) proxy;
			AssertEquals( State.Invalid, inter.ActualState );
			inter.ChangeState( State.Valid );
			AssertEquals( State.Valid, inter.ActualState );
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

		public String Name
		{
			get
			{
				return m_nameProperty;
			}
			set
			{
				m_nameProperty = value;
			}
		}

		public bool Started
		{
			get
			{
				return m_started;
			}
			set
			{
				m_started = value;
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

		#region IMySecondInterface Members

		public String Address
		{
			get
			{
				return m_addressProperty;
			}
			set
			{
				m_addressProperty = value;
			}
		}

		#endregion

		#region IServiceStatus Members

		public int Requests
		{
			get
			{
				return 32;
			}
		}

		public Apache.Avalon.DynamicProxy.Test.State ActualState
		{
			get
			{
				return m_state ;
			}
		}

		public void ChangeState(Apache.Avalon.DynamicProxy.Test.State state)
		{
			m_state = state;
		}

		#endregion
	}
	
	/// <summary>
	/// 
	/// </summary>
	public enum State
	{
		Valid, 
		Invalid
	}

	/// <summary>
	/// 
	/// </summary>
	public interface IMyInterface
	{
		String Name
		{
			get;
			set;
		}

		bool Started
		{
			get;
			set;
		}

		void Calc(int x, String y);

		void Calc(int x, String y, Single ip);

		int Calc(int x, int y);

		int Calc(int x, int y, int z, Single h);
	}

	/// <summary>
	/// 
	/// </summary>
	public interface IMySecondInterface : IMyInterface
	{
		String Address
		{
			get;
			set;
		}
	}

	public interface IServiceStatus
	{
		int Requests
		{
			get;
		}

		State ActualState
		{
			get;
		}

		void ChangeState(State state);
	}

	public class MyTest : IServiceStatus
	{
		IInvocationHandler handler = null;

		#region IServiceStatus Members

		public int Requests
		{
			get
			{
				MethodBase method = MethodBase.GetCurrentMethod();
				return (int) handler.Invoke( this, method );
			}
		}

		public Apache.Avalon.DynamicProxy.Test.State ActualState
		{
			get
			{
				MethodBase method = MethodBase.GetCurrentMethod();
				return (State) handler.Invoke( this, method );
			}
		}

		public void ChangeState(Apache.Avalon.DynamicProxy.Test.State state)
		{
			MethodBase method = MethodBase.GetCurrentMethod();
			handler.Invoke( this, method, state );
		}

		#endregion

	}
}

