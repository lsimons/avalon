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

namespace Apache.Avalon.Activation.Default.Lifestyles
{
	using System;

	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Meta;

	/// <summary>
	/// Summary description for SingletonLifestyleManager.
	/// </summary>
	public class SingletonLifestyleManager : AbstractLifestyleManager
	{
		private WeakReference m_reference;

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public SingletonLifestyleManager( IComponentModel model, IComponentFactory factory ) : base(model,factory)
		{
		}

		//-------------------------------------------------------------------
		// Commissionable
		//-------------------------------------------------------------------

		/// <summary>
		/// Commission the runtime handler. 
		/// </summary>
		public override void Commission() 
		{
			if( ComponentModel.ActivationPolicy == ActivationPolicy.Startup )
			{
				RefreshReference();
			}
		}

		/// <summary>
		/// Invokes the decommissioning phase.  Once a handler is
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public override void Decommission()
		{
			if( m_reference != null )
			{
				Finalize( m_reference.Target );
				m_reference = null;
			}
		}

		//-------------------------------------------------------------------
		// Resolver
		//-------------------------------------------------------------------

		/// <summary>
		/// Resolve a object to a value relative to a supplied set of interface classes.
		/// </summary>
		/// <returns>the resolved object</returns>
		protected override object HandleResolve() 
		{
			object instance = null;

			if( m_reference == null )
			{
				return RefreshReference();
			}
			else
			{
				instance = m_reference.Target;
				if( instance == null )
				{
					return RefreshReference();
				}
				else
				{
					return instance;
				}
			}
		}

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance">the object to be released</param>
		protected override void HandleRelease( object instance )
		{
			// continue with the current singleton reference
		}

		//-------------------------------------------------------------------
		// LifecycleManager
		//-------------------------------------------------------------------

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance">the object to be released</param>
		public override void Finalize( object instance )
		{
			ComponentFactory.Etherialize( instance );
			m_reference = null;
		}

		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------

		private Object RefreshReference()
		{
			IComponentFactory factory = ComponentFactory;
			lock( factory )
			{
				Object instance = factory.Incarnate();
				m_reference = new WeakReference( instance );
				return instance;
			}
		}
	}
}
