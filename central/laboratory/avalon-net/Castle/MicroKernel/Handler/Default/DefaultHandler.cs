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

namespace Apache.Avalon.Castle.MicroKernel.Handler.Default
{
	using System;

	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Concerns;
	using Apache.Avalon.Castle.MicroKernel.Factory.Default;

	/// <summary>
	/// Summary description for DefaultHandler.
	/// </summary>
	public class DefaultHandler : SimpleHandler
	{
		public DefaultHandler( IComponentModel model ) : base( model )
		{
		}

		protected override void CreateComponentFactoryAndLifestyleManager()
		{
			IComponentFactory innerFactory = new Factory.Default.SimpleComponentFactory( 
				// m_kernel.GetAspects(AspectPointCutFlags.Before), 
				// m_kernel.GetAspects(AspectPointCutFlags.After), 
				m_componentModel, m_serv2handler);

			if (m_kernel is IAvalonKernel)
			{
				IAvalonKernel kernel = (IAvalonKernel) m_kernel;

				IConcern commissionChain = kernel.Concerns.GetCommissionChain( kernel );
				IConcern decommissionChain = kernel.Concerns.GetDecommissionChain( kernel );

				ConcernChainComponentFactory factory = 
					new ConcernChainComponentFactory( 
						commissionChain, decommissionChain, 
						m_componentModel, innerFactory );

				innerFactory = factory;
			}

			m_lifestyleManager = 
				m_kernel.LifestyleManagerFactory.Create( 
					innerFactory, m_componentModel );
		}
	}
}
