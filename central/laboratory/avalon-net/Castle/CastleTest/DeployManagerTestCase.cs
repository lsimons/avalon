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

namespace Apache.Avalon.Castle.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Castle;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Controller;
	using Apache.Avalon.Castle.Controller.Config;
	using Apache.Avalon.Castle.Core.Default.Deployment;

	/// <summary>
	/// Summary description for DeployManagerTestCase.
	/// </summary>
	[TestFixture]
	public class DeployManagerTestCase : CastleDomainTestCaseBase
	{
		private static readonly String DEPLOY_MANAGER_TYPE_NAME = typeof(DeployManager).FullName + ", Apache.Avalon.Castle";

		private static readonly String DEPLOY_MANAGER_NAME = "apache.avalon.castle.test:name=DeployManager";

		private static readonly ManagedObjectName DEPLOY_MANAGER_MANAGED_NAME = new ManagedObjectName( DEPLOY_MANAGER_NAME );

		private static readonly String BLOCK_DEPLOYER_TYPE_NAME = typeof(BlockDeployment).FullName + ", Apache.Avalon.Castle";

		private static readonly String BLOCK_DEPLOYER_NAME = "apache.avalon.castle.test:name=BlockDeployment";

		private static readonly ManagedObjectName BLOCK_DEPLOYER_MANAGED_NAME = new ManagedObjectName( BLOCK_DEPLOYER_NAME );

		[Test]
		public void TestAcceptance()
		{
			CastleController controller = new CastleController( new CastleOptions() );

			ManagedInstance instance = server.RegisterManagedObject( 
				controller, CastleConstants.CONTROLLER_NAME );

			AssertNotNull( instance );

			CastleConfig config = new CastleConfig();
			
			ComponentDescriptor deployManager = 
				new ComponentDescriptor( 
				DEPLOY_MANAGER_TYPE_NAME, DEPLOY_MANAGER_NAME );

			ComponentDescriptor childDeployer = 
				new ComponentDescriptor( 
				BLOCK_DEPLOYER_TYPE_NAME, BLOCK_DEPLOYER_NAME );

			config.Components.Add( deployManager );
			deployManager.Dependencies.Components.Add( childDeployer );

			controller.Config = config;

			AssertNotNull( controller.Config );

			controller.Create();

			ManagedInstance deployManagerInstance = server.GetManagedInstance( DEPLOY_MANAGER_MANAGED_NAME );
			AssertNotNull( deployManagerInstance );
			
			ManagedInstance blockDeploymentInstance = server.GetManagedInstance( BLOCK_DEPLOYER_MANAGED_NAME );
			AssertNotNull( blockDeploymentInstance );

			controller.Start();

			DeployerStats stats = (DeployerStats) server.GetAttribute( BLOCK_DEPLOYER_MANAGED_NAME, "Stats" );
			AssertNotNull( stats );

			AssertEquals( 1, stats.Accepts );
		}
	}
}
