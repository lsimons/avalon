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

namespace Apache.Avalon.Meta.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Meta;

	/// <summary> 
	/// ExtensionDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class ExtensionDescriptorTestCase : AbstractDescriptorTestCase
	{
		private System.String m_key;
		
		[SetUp]
		public void SetUp()
		{
			m_key = typeof(ExtensionDescriptorTestCase).Name;
		}
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			ExtensionDescriptor ext = (ExtensionDescriptor) desc;
			
			AssertEquals(m_key, ext.Key);
		}

		protected override internal Descriptor Descriptor
		{
			get
			{
				return new ExtensionDescriptor(m_key, Properties);
			}
		}

		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new ExtensionDescriptor(null, Properties);
				Fail("Did not throw the expected NullPointerException");
			}
			catch (System.NullReferenceException)
			{
				// Success!!
			}
		}
	}
}