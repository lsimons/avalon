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

namespace Apache.Avalon.Castle.Default.Deployment
{
	using System;
	using System.IO;

	using Apache.Avalon.Castle.Logger;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Castle.Util;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for DeployManager.
	/// </summary>
	[ManagedComponent]
	public class DeployManager : ManagedService
	{
		protected ILogger logger = LoggerFactory.GetLogger("DeployManager");

		protected FileInfo baseDir;
		protected FileInfo homeDir;

		public DeployManager()
		{
		}

		public override void Start()
		{
			logger.Info("Start");

			base.Start();

			ObtainHomeDir();
		}

		[ManagedOperation]
		public void Inspect()
		{
			InspectDomainDirectory();
		}

		private void ObtainHomeDir()
		{
			ISystemContext context = (ISystemContext) 
				MXUtil.GetAttribute( server, CastleConstants.ORCHESTRATOR_NAME, "SystemContext" );
			
			if (context == null)
			{
				throw new DeploymentException("Could not obtain SystemContext from Orchestrator.");
			}

			baseDir = context.BaseDirectory;
			homeDir = context.HomeDirectory;
		}

		private void InspectDomainDirectory()
		{
			Inspect(baseDir);
			Inspect(homeDir);
		}

		private void Inspect( FileInfo dir )
		{
			DirectoryInfo dirInfo = new DirectoryInfo( dir.FullName );
			FileInfo[] files = dirInfo.GetFiles();

			foreach(FileInfo file in files)
			{
				ManagedObjectName deployer = FindDeployerForFile( file );

				if (deployer != null)
				{
					SendDeployMessage( deployer, file );
				}
			}
		}

		private ManagedObjectName FindDeployerForFile( FileInfo file )
		{
			foreach(ManagedObjectName child in Children)
			{
				bool accepts = (bool) MXUtil.InvokeOn( server, child, "Accepts",  file );

				if (accepts)
				{
					return child;
				}
			}

			return null;
		}

		private void SendDeployMessage( ManagedObjectName deployer, FileInfo file )
		{
			MXUtil.InvokeOn( server, deployer, "Deploy",  file );
		}
	}
}
