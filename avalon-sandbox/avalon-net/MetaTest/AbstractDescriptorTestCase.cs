// Copyright 2004 Apache Software Foundation
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