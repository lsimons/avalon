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
	/// ReferenceDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class ReferenceDescriptorTestCase : Assertion
	{
		private static System.Type m_classname;
		
		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new ReferenceDescriptor(null);
				Fail("Did not throw the expected NullPointerException");
			}
			catch (System.ArgumentNullException)
			{
				// Success!
			}
			
			ReferenceDescriptor ref_Renamed = new ReferenceDescriptor(m_classname);
			CheckDescriptor(ref_Renamed, m_classname);
		}
		
		private void CheckDescriptor(ReferenceDescriptor ref_Renamed, System.Type classname)
		{
			AssertNotNull(ref_Renamed);
			AssertNotNull(ref_Renamed.Type);
			AssertEquals(classname, ref_Renamed.Type);
		}
		
		static ReferenceDescriptorTestCase()
		{
			m_classname = typeof(ReferenceDescriptorTestCase);
		}
	}
}