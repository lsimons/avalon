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
	/// EntryDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class EntryDescriptorTestCase : Assertion
	{
		private const System.String m_key = "key";
		private const System.String m_alias = "otherVal";
		private static readonly System.Type m_type;
		private const bool m_optional = true;
		private const bool m_volatile = true;
		
		[Test]
		public void TestEntryDescriptor()
		{
			EntryDescriptor entry = new EntryDescriptor(m_key, m_type, m_optional, m_volatile, m_alias, null);
			CheckEntry(entry, m_key, m_type, m_optional, m_volatile, m_alias);
			
			entry = new EntryDescriptor(m_key, m_type, null);
			CheckEntry(entry, m_key, m_type, false, false, null);
			
			entry = new EntryDescriptor(m_key, m_type, m_optional, null);
			CheckEntry(entry, m_key, m_type, m_optional, false, null);
			
			entry = new EntryDescriptor(m_key, m_type, m_optional, m_volatile, null);
			CheckEntry(entry, m_key, m_type, m_optional, m_volatile, null);
			
			try
			{
				new EntryDescriptor(null, m_type, null);
				Fail("Did not throw expected NullPointerException");
			}
			catch (System.ArgumentNullException)
			{
				// Success!!
			}
			
			try
			{
				new EntryDescriptor(m_key, null, null);
				Fail("Did not throw expected NullPointerException");
			}
			catch (System.ArgumentNullException)
			{
				// Success!!
			}
		}
		
		private void CheckEntry(EntryDescriptor desc, System.String key, System.Type type, bool isOptional, bool isVolatile, System.String alias)
		{
			AssertNotNull(desc);
			AssertEquals(key, desc.Key);
			if ((System.Object) alias == null)
			{
				AssertEquals(key, desc.Alias);
			}
			else
			{
				AssertEquals(alias, desc.Alias);
			}
			AssertEquals(type, desc.Type);
			AssertEquals(isOptional, desc.Optional);
			AssertEquals(!isOptional, desc.Required);
			AssertEquals(isVolatile, desc.Volatile);
		}
		
		static EntryDescriptorTestCase()
		{
			m_type = typeof(EntryDescriptor);
		}
	}
}