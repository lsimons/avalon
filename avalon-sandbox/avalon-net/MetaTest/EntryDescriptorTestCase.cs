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