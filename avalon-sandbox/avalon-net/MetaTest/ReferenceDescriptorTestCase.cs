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
	/// ReferenceDescriptorTestCase does XYZ
	/// </summary>
	[TestFixture]
	public class ReferenceDescriptorTestCase : Assertion
	{
		private static System.String m_classname;
		private static Version m_version;
		
		[Test]
		public virtual void TestConstructor()
		{
			try
			{
				new ReferenceDescriptor(null, m_version);
				Fail("Did not throw the expected NullPointerException");
			}
			catch (System.NullReferenceException )
			{
				// Success!
			}
			
			try
			{
				new ReferenceDescriptor(null);
				Fail("Did not throw the expected NullPointerException");
			}
			catch (System.NullReferenceException )
			{
				// Success!
			}
			
			ReferenceDescriptor ref_Renamed = new ReferenceDescriptor(m_classname, m_version);
			CheckDescriptor(ref_Renamed, m_classname, m_version);
			
			ref_Renamed = new ReferenceDescriptor(m_classname + ":3.2.1.1");
			CheckDescriptor(ref_Renamed, m_classname, new Version("3.2.1.1"));
		}
		
		private void CheckDescriptor(ReferenceDescriptor ref_Renamed, System.String classname, Version version)
		{
			AssertNotNull(ref_Renamed);
			AssertNotNull(ref_Renamed.Typename);
			AssertEquals(classname, ref_Renamed.Typename);
			AssertNotNull(ref_Renamed.Version);
			AssertEquals(version, ref_Renamed.Version);
		}
		
		[Test]
		public virtual void TestCompliance()
		{
			ReferenceDescriptor ref_Renamed = new ReferenceDescriptor(m_classname, m_version);
			ReferenceDescriptor any = new ReferenceDescriptor(m_classname, new Version(1, 0, 3, 1));
			
			//Assert("anything matches explicit", any.matches(ref_Renamed));
			Assert("explicit does not match anything", !ref_Renamed.Matches(any));
		}
		
		static ReferenceDescriptorTestCase()
		{
			m_classname = typeof(ReferenceDescriptorTestCase).Name;
			m_version = new Version("1.2.3.1");
		}
	}
}