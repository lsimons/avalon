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

	
	/// <summary> ServiceTestCase does XYZ
	/// 
	/// </summary>
	/// <author>  <a href="bloritsch.at.apache.org">Berin Loritsch</a>
	/// </author>
	/// <version>  CVS $ Revision: 1.1 $
	/// </version>
	public class ServiceTestCase : AbstractDescriptorTestCase
	{
		private ReferenceDescriptor m_reference;
		private EntryDescriptor[] m_entries;
		
		public virtual void  setUp()
		{
			m_reference = new ReferenceDescriptor(typeof(ServiceTestCase));
			m_entries = new EntryDescriptor[]{new EntryDescriptor("key", typeof(System.String), null)};
		}

		protected override internal Descriptor Descriptor
		{
			get
			{
				return new Service(m_reference, m_entries, Properties);
			}
		}
		
		public virtual void  testConstructor()
		{
			try
			{
				new Service(null);
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
			Service service = (Service) desc;
			
			AssertEquals(m_reference, service.Reference);
			AssertEquals(m_reference.Type, service.Type);
			
			AssertEquals(m_entries.Length, service.Entries.Length);
			Assert(service.Matches(m_reference));
			
			EntryDescriptor[] serviceEntries = service.Entries;
			for (int i = 0; i < m_entries.Length; i++)
			{
				AssertEquals(m_entries[i], serviceEntries[i]);
			}
		}
	}
}