// Copyright 2003-2004 The Apache Software Foundation
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

namespace Apache.Avalon.Meta.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Meta.Builder;

	/// <summary>
	/// Summary description for TypeDescriptorBuilderTestCase.
	/// </summary>
	[TestFixture]
	public class TypeDescriptorBuilderTestCase : Assertion
	{
		[Test]
		public void TestBuilder()
		{
			TypeDescriptorBuilder builder = new TypeDescriptorBuilder();
			
			TypeDescriptor descriptor = 
				builder.CreateTypeDescriptor( typeof(Components.SampleComponent).AssemblyQualifiedName );
			AssertNotNull( descriptor );

			AssertEquals( 2, descriptor.Services.Length );
			AssertEquals( 1, descriptor.Dependencies.Length );
			AssertEquals( Lifestyle.Singleton, descriptor.Info.Lifestyle );
			AssertEquals( "samplecomponent", descriptor.Info.Name );
			AssertEquals( "role", descriptor.Dependencies[0].Key );
			AssertEquals( true, descriptor.Dependencies[0].Optional );
			AssertEquals( "loggerName", descriptor.Categories[0].Name );
		}
	}
}
