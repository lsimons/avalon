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
	using Apache.Avalon.Meta.Test.Components;

	
	/// <summary> ContextDescriptorTestCase does XYZ
	/// 
	/// </summary>
	/// <author>  <a href="bloritsch.at.apache.org">Berin Loritsch</a>
	/// </author>
	/// <version>  CVS $ Revision: 1.1 $
	/// </version>
	[TestFixture]
	public class ContextDescriptorTestCase : AbstractDescriptorTestCase
	{
		private System.Type m_classname;
		private EntryDescriptor[] m_entries;
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			ContextDescriptor ctxd = (ContextDescriptor) desc;
			
			AssertEquals(m_classname, ctxd.ContextInterface);
			AssertEquals(m_entries.Length, ctxd.Entries.Length);
			
			EntryDescriptor[] entries = ctxd.Entries;
			
			for (int i = 0; i < m_entries.Length; i++)
			{
				//AssertEquals(m_entries[i], entries[i]);
				AssertEquals(m_entries[i], ctxd.GetEntry(m_entries[i].Key));
			}
		}
		
		[Test]
		public void TestJoin()
		{
			ContextDescriptor desc = (ContextDescriptor) Descriptor;
			EntryDescriptor[] good = new EntryDescriptor[]{new EntryDescriptor("key", typeof(System.String), null), new EntryDescriptor("no conflict", typeof(System.String), null)};
			EntryDescriptor[] bad = new EntryDescriptor[]{new EntryDescriptor("key", typeof(System.Int32), null)};
			
			CheckDescriptor(desc);
			EntryDescriptor[] merged = desc.Merge(good);
			CheckDescriptor(desc);
			
			// The items to merge in are first.  Shouldn't this be a set?
			AssertEquals(good[0], merged[0]);
			AssertEquals(good[1], merged[1]);
			AssertEquals(m_entries[0], merged[2]);
			
			try
			{
				desc.Merge(bad);
				Fail("Did not throw expected IllegalArgumentException");
			}
			catch (System.ArgumentException)
			{
				// Success!!
			}
		}
		
		[SetUp]
		public void SetUp()
		{
			m_classname = typeof(IMyContext);
			m_entries = new EntryDescriptor[]{new EntryDescriptor("key", typeof(System.String), null)};
		}

		protected override internal Descriptor Descriptor
		{
			get
			{
				return new ContextDescriptor(m_classname, m_entries, Properties);
			}
		}
	}
}