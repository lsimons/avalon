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
	using Apache.Avalon.Framework;

	
	/// <summary> 
	/// InfoDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class InfoDescriptorTestCase : AbstractDescriptorTestCase
	{
		private void  InitBlock()
		{
			m_classname = typeof(InfoDescriptorTestCase);
			m_lifestyle = Lifestyle.Transient;
			m_collection = CollectionPolicy.Conservative;
		}

		private System.String m_name = "name";
		private System.Type m_classname;
		private Lifestyle m_lifestyle;
		private CollectionPolicy m_collection;
		private System.String m_schema = "schema";
		
		public InfoDescriptorTestCase()
		{
			InitBlock();
		}

		protected override internal Descriptor Descriptor
		{
			get
			{
				return new InfoDescriptor(m_name, m_classname, 
					m_lifestyle, m_collection, m_schema, Properties);
			}
		}
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			InfoDescriptor info = (InfoDescriptor) desc;
			AssertEquals(m_name, info.Name);
			AssertEquals(m_classname, info.Type);
			AssertEquals(m_lifestyle, info.Lifestyle);
			// AssertEquals(InfoDescriptor.CollectionPolicy(m_collection), info.CollectionPolicy);
			AssertEquals(m_schema, info.ConfigurationSchema);
		}
		
		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new InfoDescriptor(m_name, null, m_lifestyle, m_collection, m_schema, Properties);
				Fail("Did not throw the proper ArgumentNullException");
			}
			catch (System.ArgumentNullException)
			{
				// Success!
			}
			
			/*
			try
			{
				new InfoDescriptor(m_name, "foo/fake/ClassName", m_version, m_lifestyle, m_collection, m_schema, Properties);
				Fail("Did not throw the proper ArgumentNullException");
			}
			catch (System.ArgumentNullException)
			{
				// Success!
			}*/
			
			new InfoDescriptor(m_name, m_classname, Lifestyle.Singleton, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, Lifestyle.Pooled, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, Lifestyle.Thread, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, Lifestyle.Transient, m_collection, m_schema, Properties);
		}
	}
}