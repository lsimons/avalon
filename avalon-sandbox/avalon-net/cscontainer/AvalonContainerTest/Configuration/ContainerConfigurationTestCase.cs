// Copyright 2004 Apache Software Foundation
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

namespace Apache.Avalon.Container.Test.Configuration
{
	using System;
	using System.IO;

	using NUnit.Framework;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Configuration;

	/// <summary>
	/// Summary description for ContainerConfigurationTestCase.
	/// </summary>
	[TestFixture]
	public class ContainerConfigurationTestCase : Assertion
	{
		String m_basePath = AppDomain.CurrentDomain.SetupInformation.ApplicationBase;

		[Test]
		public void TestConfiguration()
		{
			String file = Path.Combine( m_basePath, "ConfigurationTest1.xml.config" );

			ContainerConfiguration config = new ContainerConfiguration(file);

			AssertNotNull( config );
			AssertNotNull( config.Configuration );
			AssertEquals( 2, config.Configuration.Children.Count );
		}
	}
}
