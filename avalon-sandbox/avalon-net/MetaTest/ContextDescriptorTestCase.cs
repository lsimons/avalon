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