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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	
	/// <summary> 
	/// TypeTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class TypeTestCase : Assertion
	{
		private InfoDescriptor m_descriptor;
		private CategoryDescriptor[] m_loggers;
		private ContextDescriptor m_context;
		private ServiceDescriptor[] m_services;
		private DependencyDescriptor[] m_dependencies;
		private StageDescriptor[] m_stages;
		private ExtensionDescriptor[] m_extensions;
		private IConfiguration m_defaults;
		private ReferenceDescriptor m_reference;
		private System.String m_key;

		[SetUp]
		public virtual void SetUp()
		{
			m_reference = new ReferenceDescriptor(typeof(TypeTestCase));
			m_key = typeof(TypeTestCase).FullName;
			m_descriptor = CreateSimpleInfo(typeof(TypeTestCase));
			m_loggers = new CategoryDescriptor[]{new CategoryDescriptor("name", new System.Collections.Specialized.NameValueCollection())};
			m_context = new ContextDescriptor(typeof(TypeTestCase), new EntryDescriptor[0]);
			m_services = new ServiceDescriptor[]{new ServiceDescriptor(m_reference)};
			m_dependencies = new DependencyDescriptor[]{new DependencyDescriptor("role", m_reference, null)};
			m_stages = new StageDescriptor[]{new StageDescriptor(m_key)};
			m_extensions = new ExtensionDescriptor[]{new ExtensionDescriptor(m_key)};
			m_defaults = new DefaultConfiguration("default", String.Empty);
		}
		
		private void CheckType(TypeDescriptor type)
		{
			AssertNotNull(type);
			CheckArray(m_loggers, type.Categories);
			AssertEquals(m_defaults, type.Configuration);
			AssertEquals(m_context, type.Context);
			CheckArray(m_dependencies, type.Dependencies);
			AssertEquals(m_dependencies[0], type.GetDependency(m_dependencies[0].Key));
			AssertEquals(m_extensions[0], type.GetExtension(m_stages[0].Key));
			CheckArray(m_extensions, type.Extensions);
			AssertEquals(m_descriptor, type.Info);
			AssertEquals(m_services[0], type.GetService(m_reference));
			AssertEquals(m_services[0], type.GetService(m_services[0].Reference.Type.FullName));
			CheckArray(m_services, type.Services);
			CheckArray(m_stages, type.Stages);
			Assert(type.IsACategory(m_loggers[0].Name));
			Assert(!type.IsACategory("fake name"));
		}
		
		private void CheckArray(System.Object[] orig, System.Object[] other)
		{
			AssertEquals(orig.Length, other.Length);
			for (int i = 0; i < orig.Length; i++)
			{
				AssertEquals(orig[i], other[i]);
			}
		}
		
		[Test]
		public virtual void TestTypeDescriptor()
		{
			TypeDescriptor type = new TypeDescriptor(m_descriptor, m_loggers, m_context, m_services, m_dependencies, m_stages, m_extensions, m_defaults);
			CheckType(type);
		}
		
		private static InfoDescriptor CreateSimpleInfo(System.Type classname)
		{
			return new InfoDescriptor(classname.Name, classname,
				Lifestyle.Transient, CollectionPolicy.Undefined, 
				null, null);
		}
	}
}