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

namespace Apache.Avalon.Container.Test
{
	using System;
	using System.Security.Permissions;
	using System.IO;
	using System.Reflection;
	using NUnit.Framework;

	using Apache.Avalon.Container.Configuration;
	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Test.Components;

	/// <summary>
	/// Summary description for PublicConstructorTestCase.
	/// </summary>
	[TestFixture]
	public class PublicConstructorTestCase : ContainerTestCase
	{
		[Test]
		public void LookupForErroneousComponent()
		{
			try
			{
				Object x = m_container.LookupManager[ typeof(IAirBus).FullName ];
				Assertion.Fail( "A non-constructable component should not be lookup'ed." );
			}
			catch( LookupException )
			{
				// Ok, expected.
			}
			catch(Exception)
			{
				Assertion.Fail( "We should receive a LookupException exception." );
			}
		}
	}
}
