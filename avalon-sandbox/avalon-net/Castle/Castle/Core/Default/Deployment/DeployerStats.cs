// Copyright 2003-2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.Core.Default.Deployment
{
	using System;

	/// <summary>
	/// Summary description for DeployerStats.
	/// </summary>
	[Serializable]
	public class DeployerStats
	{
		private int m_accepts;

		private int m_deploysSuccessfull;

		private int m_deploysFailed;

		public DeployerStats()
		{
		}

		public void IncrementAccepts()
		{
			m_accepts++;
		}

		public void IncrementSuccessfull()
		{
			m_deploysSuccessfull++;
		}

		public void IncrementFailed()
		{
			m_deploysFailed++;
		}

		public int Accepts
		{
			get
			{
				return m_accepts;
			}
		}

		public int SuccessfullDeployes
		{
			get
			{
				return m_deploysSuccessfull;
			}
		}

		public int FailedDeployes
		{
			get
			{
				return m_deploysFailed;
			}
		}
	}
}
