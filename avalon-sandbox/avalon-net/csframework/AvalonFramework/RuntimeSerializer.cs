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

namespace Apache.Avalon.Framework
{
	using System;
	using System.Collections;
	using System.Runtime.Serialization;

	/// <summary>
	///	Utility class that makes it easier to serialize objects.
	/// </summary>
	internal class RuntimeSerializer
	{
		internal static void SerializeIDictionary(SerializationInfo info, IDictionary dictionary, 
			string nameKeys, string nameValues)
		{
			SerializeICollection(info, dictionary.Keys, nameKeys);
			SerializeICollection(info, dictionary.Values, nameValues);
		}

		internal static IDictionary DeserializeIDictionary(SerializationInfo info, string nameKeys, string nameValues)
		{
			Hashtable hashtable = new Hashtable();

			object[] keys = DeserializeArray(info, nameKeys); 
			object[] values = DeserializeArray(info, nameValues);

			for (int i = 0; i < keys.Length; i++)
			{
				hashtable[keys[i]] = values[i];
			}

			return (IDictionary) hashtable;
		}

		internal static void SerializeICollection(SerializationInfo info, ICollection collection, string name)
		{
			object[] elements = new object[collection.Count];
			collection.CopyTo(elements, 0);

			SerializeArray(info, elements, name);
		}

		internal static ICollection DeserializeICollection(SerializationInfo info, string name)
		{
			return (ICollection) DeserializeArray(info, name);
		}

		internal static void SerializeArray(SerializationInfo info, object[] array, string name)
		{
			info.AddValue(name, array, typeof(object[]));
		}

		internal static object[] DeserializeArray(SerializationInfo info, string name)
		{
			return (object[]) info.GetValue(name, typeof(object[]));
		}
	}
}
