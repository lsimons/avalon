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

namespace Apache.Avalon.DynamicProxy.Builder.CodeGenerators
{
	using System;

	/// <summary>
	/// Summary description for ClassProxyGenerator.
	/// </summary>
	public class ClassProxyGenerator : BaseCodeGenerator
	{
		public ClassProxyGenerator() : base()
		{
		}

		public ClassProxyGenerator(EnhanceTypeDelegate enhance, ScreenInterfacesDelegate screenInterfaces) : 
			base(enhance, screenInterfaces)
		{
		}

		public Type GenerateCode(Type baseClass)
		{
			Type[] interfaces = new Type[0];

			if (ScreenInterfacesDelegate != null)
			{
				interfaces = ScreenInterfacesDelegate(interfaces);
			}

			CreateTypeBuilder( baseClass, interfaces );
			GenerateTypeImplementation( baseClass, true );

			if (EnhanceTypeDelegate != null)
			{
				EnhanceTypeDelegate(MainTypeBuilder, HandlerFieldBuilder, DefaultConstructorBuilder);
			}

			return MainTypeBuilder.CreateType();
		}
	}
}
