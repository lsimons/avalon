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

namespace Apache.Avalon.Castle.MicroKernel.Factory.Default
{
	using System;

	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Concerns;

	/// <summary>
	/// Summary description for ConcernChainComponentFactory.
	/// </summary>
	public class ConcernChainComponentFactory : IComponentFactory
	{
		private IConcern m_commissionChain;
		private IConcern m_decomissionChain;
		private IComponentModel m_model;
		private IComponentFactory m_innerFactory;

		public ConcernChainComponentFactory(
			IConcern commissionChain, IConcern decomissionChain, 
			IComponentModel model, IComponentFactory innerFactory)
		{
			AssertUtil.ArgumentNotNull( commissionChain, "commissionChain" );
			AssertUtil.ArgumentNotNull( decomissionChain, "decomissionChain" );
			AssertUtil.ArgumentNotNull( model, "model" );
			AssertUtil.ArgumentNotNull( innerFactory, "innerFactory" );

			m_commissionChain = commissionChain;
			m_decomissionChain = decomissionChain;
			m_model = model;
			m_innerFactory = innerFactory;
		}

		#region IComponentFactory Members

		public Object Incarnate()
		{
			ICreationConcern creationConcern = (ICreationConcern) m_commissionChain;

			object instance = creationConcern.Apply( m_model, m_innerFactory );

			creationConcern.Apply( m_model, instance );

			return instance;
		}

		public void Etherialize( object instance )
		{
			m_decomissionChain.Apply( m_model, instance );
		}

		#endregion
	}
}
