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

namespace Apache.Avalon.Castle.MicroKernel.Test
{
	using System;

	using NUnit.Framework;

	using Apache.Avalon.Castle.MicroKernel.Concerns;
	using Apache.Avalon.Castle.MicroKernel.Concerns.Default;

	/// <summary>
	/// Summary description for ConcernManagerTestCase.
	/// </summary>
	[TestFixture]
	public class ConcernManagerTestCase : Assertion
	{
		private Kernel m_kernel;

		[SetUp]
		public void CreateKernel()
		{
			m_kernel = new DefaultKernel();
		}

		[Test]
		public void Creation()
		{
			ConcernManager manager = new ConcernManager();
			AssertNotNull( manager );
		}

		[Test]
		public void GetDefaultCommissionChain()
		{
			ConcernManager manager = new ConcernManager();
			IConcern concern = manager.GetCommissionChain( m_kernel );
			AssertNotNull( concern );
			AssertNotNull( concern is ICreationConcern );
			AssertNotNull( concern is CreationConcern );
			AssertNotNull( concern.Next );

			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is EnableLoggerConcern );

			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is ContextConcern );
		
			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is EnableLookupConcern );

			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is ConfigureConcern );

			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is InitializeConcern );

			concern = concern.Next;
			AssertNotNull( concern );
			AssertNotNull( concern is ICommissionConcern );
			AssertNotNull( concern is StartConcern );

			AssertNull( concern.Next );
		}

		[Test]
		public void GetDefaultDecommissionChain()
		{
			ConcernManager manager = new ConcernManager();
			IConcern concern = manager.GetDecommissionChain( m_kernel );
			AssertNotNull( concern );
			AssertNotNull( concern is IDecommissionConcern );
			AssertNotNull( concern is ShutdownConcern );
			AssertNotNull( concern.Next );

			concern = concern.Next;
			AssertNotNull( concern is IDestructionConcern );
			AssertNotNull( concern is DestructionConcern );
			AssertNull( concern.Next );
		}
	}
}
