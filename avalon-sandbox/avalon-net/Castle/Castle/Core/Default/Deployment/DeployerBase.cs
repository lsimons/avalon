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
	using System.IO;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Logger;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Model.Default;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for DeployerBase.
	/// </summary>
	public abstract class DeployerBase : ManagedService
	{
		private ILogger logger = LoggerFactory.GetLogger("DeployerBase");

		/// <summary>
		/// Pending
		/// </summary>
		protected DeployerStats m_stats = new DeployerStats();

		/// <summary>
		/// Pending
		/// </summary>
		[ManagedAttribute]
		public DeployerStats Stats
		{
			get
			{
				return m_stats;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="file"></param>
		/// <returns></returns>
		public abstract bool Accepts(FileInfo file);

		/// <summary>
		/// 
		/// </summary>
		public abstract void Deploy(FileInfo file);

		/// <summary>
		/// 
		/// </summary>
		/// <param name="profile"></param>
		protected void DeployContainmentProfile( ContainmentProfile profile )
		{
			MXUtil.InvokeOn( Server, CastleConstants.ORCHESTRATOR_NAME, "DeployContainmentProfile", profile );
		}
	}
}
