using Apache.Avalon.DynamicProxy.Builder.CodeGenerators;
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

namespace Apache.Avalon.DynamicProxy.Builder
{
	using System;

	/// <summary>
	/// Summary description for DefaultProxyBuilder.
	/// </summary>
	public class DefaultProxyBuilder : IProxyBuilder
	{
		#region IProxyBuilder Members

		public Type CreateInterfaceProxy(Type[] interfaces)
		{
			InterfaceProxyGenerator generator = new InterfaceProxyGenerator();
			return generator.GenerateCode( interfaces );
		}

		public Type CreateClassProxy(Type theClass)
		{
			ClassProxyGenerator generator = new ClassProxyGenerator();
			return generator.GenerateCode( theClass );
		}

		public Type CreateCustomInterfaceProxy(Type[] interfaces, EnhanceTypeDelegate enhance, 
			ScreenInterfacesDelegate screenInterfaces)
		{
			InterfaceProxyGenerator generator = new InterfaceProxyGenerator(enhance, screenInterfaces);
			return generator.GenerateCode( interfaces );
		}

		public Type CreateCustomClassProxy(Type theClass, EnhanceTypeDelegate enhance, 
			ScreenInterfacesDelegate screenInterfaces)
		{
			ClassProxyGenerator generator = new ClassProxyGenerator(enhance, screenInterfaces);
			return generator.GenerateCode( theClass );
		}

		#endregion
	}
}
