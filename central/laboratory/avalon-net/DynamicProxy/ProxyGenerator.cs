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

namespace Apache.Avalon.DynamicProxy
{
	using System;
	using System.Reflection;
	using System.Reflection.Emit;

	using Apache.Avalon.DynamicProxy.Builder;

	/// <summary>
	/// Generates a Java style proxy. This overrides the .Net proxy requirements 
	/// that forces one to extend MarshalByRefObject or (for a different purpose)
	/// ContextBoundObject to have a Proxiable class.
	/// </summary>
	/// <remarks>
	/// The <see cref="ProxyGenerator"/> should be used to generate a class 
	/// implementing the specified interfaces. The class implementation will 
	/// only call the internal <see cref="IInvocationHandler"/> instance.
	/// </remarks>
	/// <remarks>
	/// This proxy implementation currently doesn't not supports ref and out arguments 
	/// in methods.
	/// </remarks>
	/// <example>
	/// <code>
	/// MyInvocationHandler handler = ...
	/// IInterfaceExposed proxy = 
	///		ProxyGenerator.CreateProxy( new Type[] { typeof(IInterfaceExposed) }, handler );
	/// </code>
	/// </example>
	public abstract class ProxyGenerator
	{
		private static IProxyBuilder m_builder = new ProxyBuilderImpl();

		public static IProxyBuilder ProxyBuilder
		{
			get { return m_builder; }
			set { m_builder = value; }
		}

		public static object CreateClassProxy(Type baseClass, IInvocationHandler handler)
		{
			if (baseClass == null)
			{
				throw new ArgumentNullException("theClass");
			}
			if (baseClass.IsInterface)
			{
				throw new ArgumentException("'baseClass' must be a class, not an interface");
			}
			if (handler == null)
			{
				throw new ArgumentNullException("handler");
			}

			Type newType = ProxyBuilder.CreateClassProxy(baseClass);
			return CreateProxyInstance( newType, handler );
		}

		/// <summary>
		/// Generates a proxy implementing all the specified interfaces and
		/// redirecting method invocations to the specifed handler.
		/// </summary>
		/// <param name="theInterface">Interface to be implemented</param>
		/// <param name="handler">instance of <see cref="IInvocationHandler"/></param>
		/// <returns>Proxy instance</returns>
		public static object CreateProxy(Type theInterface, IInvocationHandler handler)
		{
			return CreateProxy(new Type[] {theInterface}, handler);
		}

		/// <summary>
		/// Generates a proxy implementing all the specified interfaces and
		/// redirecting method invocations to the specifed handler.
		/// </summary>
		/// <param name="interfaces">Array of interfaces to be implemented</param>
		/// <param name="handler">instance of <see cref="IInvocationHandler"/></param>
		/// <returns>Proxy instance</returns>
		public static object CreateProxy(Type[] interfaces, IInvocationHandler handler)
		{
			if (interfaces == null)
			{
				throw new ArgumentNullException("interfaces");
			}
			if (handler == null)
			{
				throw new ArgumentNullException("handler");
			}
			if (interfaces.Length == 0)
			{
				throw new ArgumentException("Can't handle an empty interface array");
			}

			Type newType = ProxyBuilder.CreateInterfaceProxy(interfaces);
			return CreateProxyInstance( newType, handler );
		}

		private static object CreateProxyInstance(Type type, IInvocationHandler handler)
		{
			return Activator.CreateInstance(type, new object[] {handler});
		}
	}
}