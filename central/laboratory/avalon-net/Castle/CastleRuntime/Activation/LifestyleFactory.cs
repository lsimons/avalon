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

namespace Apache.Avalon.Castle.Runtime.Extended.Activation
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Activation;
	using Apache.Avalon.Activation.Default;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for LifestyleFactory.
	/// </summary>
	public class LifestyleFactory : DefaultLifestyleFactory, ILifestyleFactory
	{
		protected MServer m_server;

		public LifestyleFactory( MServer server, ISystemContext system ) : base( system )
		{
			Assert.ArgumentNotNull( server, "server" );
			m_server = server;
		}

		#region ILifestyleFactory Members

		public override ILifestyleManager CreateLifestyleManager( IComponentModel model )
		{
			Assert.ArgumentNotNull( model, "model" );

			IComponentFactory factory = new ComponentFactory( m_server, model );
			
			return base.CreateLifestyleManager( model, factory );
		}

		#endregion
	}
}
