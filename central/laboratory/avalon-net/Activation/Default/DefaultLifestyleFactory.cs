// Copyright 2004 Apache Software Foundation
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

namespace Apache.Avalon.Activation.Default
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Activation.Default.Lifestyles;

	/// <summary>
	/// A factory enabling the establishment of runtime handlers.
	/// </summary>
	public class DefaultLifestyleFactory : ILifestyleFactory
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		protected ISystemContext m_system;

		/// <summary>
		/// Creation of a new default lifestyle factory.
		/// </summary>
		/// <param name="system">the system context</param>
		public DefaultLifestyleFactory( ISystemContext system )
		{
			m_system = system;
		}

		#region ILifestyleFactory Members

		public virtual ILifestyleManager CreateLifestyleManager(IComponentModel model)
		{
			IComponentFactory factory = new DefaultComponentFactory( m_system, model );
			return CreateLifestyleManager( model, factory );
		}

		#endregion

		/// <summary>
		/// Create a new lifestyle manager.
		/// </summary>
		/// <param name="model">the component model</param>
		/// <param name="factory">the component factory</param>
		/// <returns>the lifestyle manager</returns>
		protected ILifestyleManager CreateLifestyleManager( IComponentModel model, IComponentFactory factory )
		{
			Lifestyle lifestyle = model.TypeDescriptor.Info.Lifestyle;

			if( lifestyle == Lifestyle.Singleton )
			{
				return new SingletonLifestyleManager( model, factory );
			}
			else if( lifestyle == Lifestyle.Thread )
			{
				// return new ThreadLifestyleManager( model, factory );
				String error = "Unsupported lifestyle [" + lifestyle + "].";
				throw new ArgumentException( error );
			}
			else if( lifestyle == Lifestyle.Transient )
			{
				return new TransientLifestyleManager( model, factory );
			}
			else
			{
				//
				// TODO
				// check if the key is an artifact reference and if 
				// so, try to load up a lifestyle factory and delegate the 
				// request
				//

				String error = "Unsupported lifestyle [" + lifestyle + "].";
				throw new ArgumentException( error );
			}
		}

		/*
		private IComponentFactory CreateComponentFactory( IComponentModel model )
		{
			//
			// TODO
			// check for a custom component factory artifact reference
			// and load via avalon-repository if non null
			//

			return new DefaultComponentFactory( m_system, model );
		}*/
	}
}
