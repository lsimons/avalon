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

	using Apache.Avalon.Framework;
	using ILogger = Apache.Avalon.Framework.ILogger;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Logger;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Data.Builder;

	/// <summary>
	/// Summary description for BlockDeployment.
	/// </summary>
	[ManagedComponent]
	public class BlockDeployment : DeployerBase
	{
		protected static readonly String BLOCK_NAME = "block.xml";

		protected String config;
		
		private ILogger logger = LoggerFactory.GetLogger("BlockDeployment");

		public BlockDeployment()
		{
			logger.Info("BlockDeployment");
		}
		
		[ManagedAttribute]
		public String Config
		{
			get
			{
				return config;
			}
			set
			{
				config = value;
			}
		}
		
		[ManagedOperation]
		public override bool Accepts( System.IO.FileInfo file )
		{
			bool accepts = String.Compare( file.Name, BLOCK_NAME, true ) == 0;

			if (accepts)
			{
				Stats.IncrementAccepts();
			}

			return accepts;
		}
	
		[ManagedOperation]
		public override void Deploy( System.IO.FileInfo file )
		{
			try
			{
				IConfiguration configuration = ObtainConfiguration( file );

				ContainmentProfileCreator creator = new ContainmentProfileCreator();
				ContainmentProfile profile = creator.CreateContainmentProfile( configuration );

				base.DeployContainmentProfile( profile );
			}
			catch(DeploymentException de)
			{
				Stats.IncrementFailed();

				throw de;
			}
			catch(Exception ex)
			{
				Stats.IncrementFailed();

				throw new DeploymentException( "Could not deploy block.", ex );
			}

			Stats.IncrementSuccessfull();
		}

		private IConfiguration ObtainConfiguration( System.IO.FileInfo file )
		{
			IConfiguration config = null;
			
			try
			{
				config = DefaultConfigurationSerializer.Deserialize( file.FullName );
			}
			catch(Exception e)
			{
				logger.Error("Could not deserialize configuration from file {0}", file.FullName);
				throw new DeploymentException( "Could not deserialize configuration from file", e );
			}

			return config;
		}
	}
}
