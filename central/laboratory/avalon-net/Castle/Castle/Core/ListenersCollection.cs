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

	using Apache.Avalon.Castle.Util;

	/// <summary>
	/// Summary description for ListenersCollection.
	/// </summary>
	public class ListenersCollection
	{
		private ArrayList m_listeners = new ArrayList();

		public ListenersCollection()
		{
		}

		public void Add( MContributeLifecycle listener )
		{
			Assert.ArgumentNotNull( listener, "listener" );

			m_listeners.Add( listener );
		}

		public bool IsEmpty
		{
			get
			{
				return m_listeners.Count == 0;
			}
		}
	}
}
