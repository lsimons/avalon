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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.MicroKernel;
	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Model.Default;
	using Apache.Avalon.Castle.MicroKernel.Configuration;
	using Apache.Avalon.Castle.MicroKernel.Configuration.Default;
	using Apache.Avalon.Castle.MicroKernel.Test.Components;

	/// <summary>
	/// Summary description for DefaultConfigurationManagerTestCase.
	/// </summary>
	[TestFixture]
	public class DefaultConfigurationManagerTestCase : Assertion
	{
		[Test]
		public void TestUsage()
		{
			DefaultConfigurationManager config = new DefaultConfigurationManager();
			IConfiguration componentConfig = config.GetConfiguration( "component1" );

			AssertNotNull( componentConfig );
			AssertEquals( "johndoe", componentConfig.GetChild("name", true).Value );
			AssertEquals( "1099", componentConfig.GetChild("port", true).Value );
		}

		[Test]
		public void TestNoContentConfig()
		{
			DefaultConfigurationManager config = new DefaultConfigurationManager();
			IConfiguration componentConfig = config.GetConfiguration( "component2" );

			AssertNotNull( componentConfig );
			AssertEquals( 0, componentConfig.Attributes.Count );
			AssertEquals( 0, componentConfig.Children.Count );
		}
	}
}
