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

namespace Apache.Avalon.DynamicProxy.Test
{
	using System;

	/// <summary>
	/// Summary description for ServiceStatusImpl.
	/// </summary>
	public class ServiceStatusImpl : IServiceStatus
	{
		private Apache.Avalon.DynamicProxy.Test.State m_state = Apache.Avalon.DynamicProxy.Test.State.Invalid;

		public ServiceStatusImpl()
		{
		}

		#region IServiceStatus Members

		public int Requests
		{
			get
			{
				return 10;
			}
		}

		public Apache.Avalon.DynamicProxy.Test.State ActualState
		{
			get
			{
				return m_state;
			}
		}

		public void ChangeState(Apache.Avalon.DynamicProxy.Test.State state)
		{
			m_state = state;
		}

		#endregion
	}
}
