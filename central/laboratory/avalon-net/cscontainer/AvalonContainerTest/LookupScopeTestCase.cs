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
	using NUnit.Framework;

	using Apache.Avalon.Container.Test.Components;

	/// <summary>
	/// Assures the component can only lookups his dependencies
	/// and the returned instances matches theirs lifestyles.
	/// </summary>
	[TestFixture]
	public class LookupScopeTestCase : ContainerTestCase
	{
		[Test]
		public void ErroneousComponentTest()
		{
			IAirplane airplane = (IAirplane) 
				m_container.LookupManager[ typeof(IAirplane).FullName ];

			Assertion.AssertNotNull( airplane );
			Assertion.Assert( airplane.hasRadio == false );
		}

		[Test]
		public void LifestyleCorrectness()
		{
			IEntityLocator locator = (IEntityLocator) 
				m_container.LookupManager[ typeof(IEntityLocator).FullName ];

			Assertion.AssertNotNull( locator );

			object[] entities = new object[3];

			entities[0] = locator.Find(0);
			entities[1] = locator.Find(1);
			entities[2] = locator.Find(2);

			Assertion.AssertNotNull( entities[0] );
			Assertion.AssertNotNull( entities[1] );
			Assertion.AssertNotNull( entities[2] );

			Assertion.Assert( !entities[0].Equals(entities[1]) );
			Assertion.Assert( !entities[0].Equals(entities[2]) );
			Assertion.Assert( !entities[1].Equals(entities[2]) );
		}
	}
}
