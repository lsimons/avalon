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
	using System.IO;

	using NUnit.Framework;

	using Apache.Avalon.Meta;
		
	/// <summary> 
	/// AbstractDescriptorTestCase does XYZ
	/// </summary>
	public abstract class AbstractDescriptorTestCase : Assertion
	{
		protected internal abstract Descriptor Descriptor{get;}
		protected internal const System.String VALID_KEY = "key";
		protected internal const System.String VALID_VALUE = "value";
		protected internal const System.String INVALID_KEY = "bad-key";
		protected internal const System.String DEFAULT_VALUE = "default";

		virtual protected internal System.Collections.Specialized.NameValueCollection Properties
		{
			get
			{
				System.Collections.Specialized.NameValueCollection props = new System.Collections.Specialized.NameValueCollection();
				props[VALID_KEY] = VALID_VALUE;
				return props;
			}
		}
		
		protected internal virtual void CheckDescriptor(Descriptor desc)
		{
			AssertEquals(VALID_VALUE, desc.GetAttribute(VALID_KEY));
			AssertEquals(DEFAULT_VALUE, desc.GetAttribute(INVALID_KEY, DEFAULT_VALUE));
			
			bool hasValid = false;
			bool hasInvalid = false;
			System.String[] names = desc.AttributeNames;
			
			AssertNotNull(names);
			Assert(names.Length > 0);
			
			for (int i = 0; i < names.Length; i++)
			{
				if (VALID_KEY.Equals(names[i]))
					hasValid = true;
				if (INVALID_KEY.Equals(names[i]))
					hasInvalid = true;
			}
			
			Assert(hasValid);
			Assert(!hasInvalid);
		}
		
		[Test]
		public virtual void TestSerialization()
		{
			Descriptor desc = Descriptor;
			CheckDescriptor(desc);
			
			System.IO.FileInfo file = new System.IO.FileInfo("test.file");
			System.Runtime.Serialization.Formatters.Binary.BinaryFormatter formatter = new System.Runtime.Serialization.Formatters.Binary.BinaryFormatter();
			
			using(FileStream fs = new FileStream( file.FullName, FileMode.OpenOrCreate ))
			{
				formatter.Serialize( fs, desc );
			}
			
			System.IO.BinaryReader ois = new System.IO.BinaryReader(new FileStream(file.FullName, FileMode.Open, FileAccess.Read));

			Descriptor serialized = (Descriptor) formatter.Deserialize(ois.BaseStream);

			ois.Close();
			bool tmpBool;
			if (System.IO.File.Exists(file.FullName))
			{
				System.IO.File.Delete(file.FullName);
				tmpBool = true;
			}
			else if (System.IO.Directory.Exists(file.FullName))
			{
				System.IO.Directory.Delete(file.FullName);
				tmpBool = true;
			}
			else
				tmpBool = false;
			bool generatedAux = tmpBool;
			
			Assert(desc != serialized); // Ensure this is not the same instance
			CheckDescriptor(serialized);
			
			AssertEquals(desc, serialized);
			// AssertEquals(desc.GetHashCode(), serialized.GetHashCode());
		}
	}
}