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
			m_classname = typeof(InfoDescriptorTestCase).Name;
			m_version = new Version("1.2.3");
			m_lifestyle = Lifestyle.Transient;
			m_collection = CollectionPolicy.Conservative;
		}

		private System.String m_name = "name";
		private System.String m_classname;
		private Version m_version;
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
					m_version, m_lifestyle, m_collection, m_schema, Properties);
			}
		}
		
		protected override internal void CheckDescriptor(Descriptor desc)
		{
			base.CheckDescriptor(desc);
			InfoDescriptor info = (InfoDescriptor) desc;
			AssertEquals(m_name, info.Name);
			AssertEquals(m_classname, info.Typename);
			AssertEquals(m_version, info.Version);
			AssertEquals(m_lifestyle, info.Lifestyle);
			// AssertEquals(InfoDescriptor.CollectionPolicy(m_collection), info.CollectionPolicy);
			AssertEquals(m_schema, info.ConfigurationSchema);
		}
		
		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new InfoDescriptor(m_name, null, m_version, m_lifestyle, m_collection, m_schema, Properties);
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
			
			new InfoDescriptor(m_name, m_classname, m_version, Lifestyle.Singleton, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, m_version, Lifestyle.Pooled, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, m_version, Lifestyle.Thread, m_collection, m_schema, Properties);
			new InfoDescriptor(m_name, m_classname, m_version, Lifestyle.Transient, m_collection, m_schema, Properties);
		}
	}
}