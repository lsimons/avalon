// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

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
			m_reference = new ReferenceDescriptor(typeof(TypeTestCase).FullName);
			m_key = typeof(TypeTestCase).FullName;
			m_descriptor = CreateSimpleInfo(typeof(TypeTestCase).FullName);
			m_loggers = new CategoryDescriptor[]{new CategoryDescriptor("name", new System.Collections.Specialized.NameValueCollection())};
			m_context = new ContextDescriptor(typeof(TypeTestCase).FullName, new EntryDescriptor[0]);
			m_services = new ServiceDescriptor[]{new ServiceDescriptor(m_reference)};
			m_dependencies = new DependencyDescriptor[]{new DependencyDescriptor("role", m_reference)};
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
			AssertEquals(m_dependencies[0], type.getDependency(m_dependencies[0].Key));
			AssertEquals(m_extensions[0], type.getExtension(m_stages[0].Key));
			CheckArray(m_extensions, type.Extensions);
			AssertEquals(m_descriptor, type.Info);
			AssertEquals(m_services[0], type.getService(m_reference));
			AssertEquals(m_services[0], type.getService(m_services[0].Reference.Typename));
			CheckArray(m_services, type.Services);
			CheckArray(m_stages, type.Stages);
			Assert(type.isaCategory(m_loggers[0].Name));
			Assert(!type.isaCategory("fake name"));
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
		
		private static InfoDescriptor CreateSimpleInfo(System.String classname)
		{
			return new InfoDescriptor(null, classname,
				null, Lifestyle.Transient, CollectionPolicy.Undefined, 
				null, null);
		}
	}
}