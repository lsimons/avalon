// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.Core
{
	using System;
	using System.Collections;

	/// <summary>
	/// Summary description for ListenersHolder.
	/// </summary>
	public class ListenersHolder
	{
		private Hashtable m_map = new Hashtable();

		public ListenersHolder()
		{
		}

		public ListenersCollection this [ LifecyclePhase phase ]
		{
			get
			{
				ListenersCollection collection = (ListenersCollection) m_map[ phase ];

				if (collection == null)
				{
					collection = new ListenersCollection();
					m_map[ phase ] = collection;
				}

				return collection;
			}
		}

		public void Clear()
		{
			m_map.Clear();
		}
	}
}
