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
			m_reference = new ReferenceDescriptor(typeof(ServiceTestCase).Name, new Version("1.2.3"));
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
			AssertEquals(m_reference.Typename, service.Typename);
			AssertEquals(m_reference.Version, service.Version);
			
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