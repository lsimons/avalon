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
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for DeployManager.
	/// </summary>
	[ManagedComponent]
	public class DeployManager : ManagedService
	{
		protected ILogger logger = LoggerFactory.GetLogger("DeployManager");

		public DeployManager()
		{
		}

		public override void Start()
		{
			base.Start();
			logger.Info("Start");

			InspectDomainDirectory();
		}

		private void InspectDomainDirectory()
		{
			// TEMPORARY - we should obtain the home directory
			// by others means
			String homeDir = AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
			DirectoryInfo homeDirInfo = new DirectoryInfo( homeDir );
			FileInfo[] files = homeDirInfo.GetFiles();

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
				bool accepts = (bool) 
					server.Invoke( 
						child, 
						"Accepts", 
						new Object[] { file }, 
						new Type[] { typeof(FileInfo) } );

				if (accepts)
				{
					return child;
				}
			}

			return null;
		}

		private void SendDeployMessage( ManagedObjectName deployer, FileInfo file )
		{
			server.Invoke( 
				deployer, 
				"Deploy", 
				new Object[] { file }, 
				new Type[] { typeof(FileInfo) } );
		}
	}
}
