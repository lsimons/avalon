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
	using Apache.Avalon.Castle.Controller;
	using Apache.Avalon.Castle.Controller.Config;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Test.ManagedComponents;

	/// <summary>
	/// Summary description for CastleControllerTestCase.
	/// </summary>
	[TestFixture]
	public class CastleControllerTestCase : CastleDomainTestCaseBase
	{
		private static readonly String ROOT_COMPONENT_TYPE_NAME = typeof(RootComponent).FullName + ", Apache.Avalon.Castle.Test";

		private static readonly String ROOT_COMPONENT_NAME = "apache.avalon.castle.test:name=RootComponent";

		private static readonly ManagedObjectName ROOT_MANAGED_NAME = new ManagedObjectName( ROOT_COMPONENT_NAME );

		private static readonly String CHILD_COMPONENT_TYPE_NAME = typeof(ChildComponent).FullName + ", Apache.Avalon.Castle.Test";

		private static readonly String CHILD_COMPONENT_NAME = "apache.avalon.castle.test:name=ChildComponent";

		private static readonly ManagedObjectName CHILD_MANAGED_NAME = new ManagedObjectName( CHILD_COMPONENT_NAME );


		[Test]
		public void TestComponentLifecycle()
		{
			CastleController controller = new CastleController( new CastleOptions() );

			ManagedInstance instance = server.RegisterManagedObject( 
				controller, CastleConstants.CONTROLLER_NAME );

			AssertNotNull( instance );

			CastleConfig config = new CastleConfig();
			
			ComponentDescriptor comp = 
				new ComponentDescriptor( 
					ROOT_COMPONENT_TYPE_NAME, ROOT_COMPONENT_NAME );

			config.Components.Add( comp );

			controller.Config = config;

			AssertNotNull( controller.Config );

			controller.Create();

			ManagedInstance rootComponent = server.GetManagedInstance( ROOT_MANAGED_NAME );
			AssertNotNull( rootComponent );

			AssertStateEquals( ManagedObjectState.Created, ROOT_MANAGED_NAME );
			AssertNull( GetParent(ROOT_MANAGED_NAME) );

			controller.Start();

			AssertNull( GetParent(ROOT_MANAGED_NAME) );
			AssertStateEquals( ManagedObjectState.Started, ROOT_MANAGED_NAME );

			controller.Stop();

			// The CastleController.Stop will stop and destroy 
			// the components
			AssertStateEquals( ManagedObjectState.Destroyed, ROOT_MANAGED_NAME );
		}
		
		[Test]
		public void TestComponentLifecycleWithDependencies()
		{
			CastleController controller = new CastleController( new CastleOptions() );

			ManagedInstance instance = server.RegisterManagedObject( 
				controller, CastleConstants.CONTROLLER_NAME );

			AssertNotNull( instance );

			CastleConfig config = new CastleConfig();
			
			ComponentDescriptor comp = 
				new ComponentDescriptor( 
				ROOT_COMPONENT_TYPE_NAME, ROOT_COMPONENT_NAME );

			ComponentDescriptor childDescriptor = 
				new ComponentDescriptor( 
				CHILD_COMPONENT_TYPE_NAME, CHILD_COMPONENT_NAME );

			config.Components.Add( comp );
			comp.Dependencies.Components.Add( childDescriptor );

			controller.Config = config;

			AssertNotNull( controller.Config );

			controller.Create();

			ManagedInstance rootComponent = server.GetManagedInstance( ROOT_MANAGED_NAME );
			AssertNotNull( rootComponent );

			controller.Start();

			AssertNull( GetParent(ROOT_MANAGED_NAME) );

			ManagedObjectName parent = GetParent( CHILD_MANAGED_NAME );
			AssertNotNull( parent );
			AssertEquals( ROOT_MANAGED_NAME, parent );

			ManagedObjectName[] children = GetChildren( ROOT_MANAGED_NAME );
			AssertNotNull( children );
			AssertEquals( 1, children.Length );
			AssertEquals( CHILD_MANAGED_NAME, children[0] );

			children = GetChildren( CHILD_MANAGED_NAME );
			AssertNotNull( children );
			AssertEquals( 0, children.Length );
			
			controller.Stop();

			children = GetChildren( ROOT_MANAGED_NAME );
			AssertNotNull( children );
			AssertEquals( 0, children.Length );

			children = GetChildren( CHILD_MANAGED_NAME );
			AssertNotNull( children );
			AssertEquals( 0, children.Length );
		}

		private void AssertStateEquals( ManagedObjectState value, ManagedObjectName name )
		{
			ManagedObjectState state = (ManagedObjectState) 
				server.GetAttribute( name, "ManagedObjectState" );
			AssertNotNull( state );
			AssertEquals( value, state );
		}

		private ManagedObjectName GetParent( ManagedObjectName name )
		{
			return (ManagedObjectName) server.GetAttribute( name, "ParentName" );
		}

		private ManagedObjectName[] GetChildren( ManagedObjectName name )
		{
			return (ManagedObjectName[]) server.GetAttribute( name, "Children" );
		}
	}
}

