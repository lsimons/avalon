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

	using Apache.Avalon.Meta;

	
	/// <summary> 
	/// ServiceDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class ServiceDescriptorTestCase : AbstractDescriptorTestCase
	{
		private ReferenceDescriptor m_designator;
		
		protected override internal Descriptor Descriptor
		{
			get
			{
				return new ServiceDescriptor(m_designator, Properties);
			}
		}

		[SetUp]
		public void SetUp()
		{
			m_designator = new ReferenceDescriptor(typeof(ServiceDescriptorTestCase));
		}
		
		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new ServiceDescriptor(null, Properties);
				Fail("Did not throw the expected NullPointerException");
			}
			catch (System.NullReferenceException)
			{
				// Sucess!
			}
		}
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			ServiceDescriptor service = (ServiceDescriptor) desc;
			
			AssertEquals(m_designator, service.Reference);
		}
	}
}