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
	using System.Collections;

	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for TransientLifestyleManager.
	/// </summary>
	public class TransientLifestyleManager : AbstractLifestyleManager
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private ArrayList m_list = new ArrayList();

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public TransientLifestyleManager( IComponentModel model, IComponentFactory factory ) : base( model, factory )
		{
		}

		//-------------------------------------------------------------------
		// Commissionable
		//-------------------------------------------------------------------

		/// <summary>
		/// Commission the appliance. 
		/// </summary>
		public override void Commission() 
		{
			// TODO: setup a background thread to check queues for 
			// released references and remove them from our list, otherwise we
			// have a memory leak due to accumulation of weak references
		}

		/// <summary>
		/// Decommission the appliance.  Once an appliance is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public override void Decommission()
		{
			foreach(WeakReference reference in m_list)
			{
				Finalize( reference.Target );
			}
			m_list.Clear();
		}

		//-------------------------------------------------------------------
		// Resolver
		//-------------------------------------------------------------------

		/// <summary>
		/// Resolve a object to a value relative to a supplied set of 
		/// interface classes.
		/// </summary>
		/// <returns>the resolved object</returns>
		protected override Object HandleResolve()
		{
			object instance = ComponentFactory.Incarnate();
			WeakReference reference = new WeakReference( instance );
			m_list.Add( reference );
			return instance;
		}

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance"> the object to be released</param>
		protected override void HandleRelease( Object instance )
		{
			Finalize( instance );
		}
	}
}
