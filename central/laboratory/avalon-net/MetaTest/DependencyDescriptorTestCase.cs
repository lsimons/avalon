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
	/// DependencyDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class DependencyDescriptorTestCase : AbstractDescriptorTestCase
	{
		private System.String m_role;
		private ReferenceDescriptor m_reference;
		private bool m_optional = true;
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			
			DependencyDescriptor dep = (DependencyDescriptor) desc;
			AssertEquals(m_role, dep.Key);
			AssertEquals(m_reference, dep.Service);
			AssertEquals(m_optional, dep.Optional);
			AssertEquals(!m_optional, dep.Required);
		}
		
		[SetUp]
		public void SetUp()
		{
			m_role = "Test";
			m_reference = new ReferenceDescriptor( typeof(DependencyDescriptorTestCase) );
		}

		protected override internal Descriptor Descriptor
		{
			get
			{
				return new DependencyDescriptor(m_role, m_reference, m_optional, Properties, null);
			}
			
		}
	}
}