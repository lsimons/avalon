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

namespace Apache.Avalon.Castle.MicroKernel
{
	using System;
	using System.Collections;
	using System.Reflection;

	/// <summary>
	/// Summary description for ConstructionInfo.
	/// </summary>
	public class ConstructionInfo
	{
		private ConstructorInfo m_constructor;

		private Hashtable m_service2handler;

		public ConstructionInfo(ConstructorInfo constructor, Hashtable service2handler)
		{
			m_constructor = constructor;
			m_service2handler = service2handler;
		}

		public ConstructorInfo Constructor
		{
			get
			{
				return m_constructor;
			}
		}

		public IHandler this [ Type service ]
		{
			get
			{
				return (IHandler) m_service2handler[ service ];
			}
		}
	}
}
