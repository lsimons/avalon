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
	using Apache.Avalon.Activation;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Abstract appliance.
	/// </summary>
	public abstract class AbstractAppliance : IAppliance
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private IDeploymentModel m_model;

		private ILogger m_logger;

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public AbstractAppliance( IDeploymentModel model )
		{
			if( null == model ) 
			{
				throw new ArgumentNullException( "model" );
			}

			m_model = model;
			m_logger = model.Logger;
		}

		#region ICommissionable Members

		/// <summary>
		/// Decommission the appliance.  Once an appliance is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public abstract void Decommission();

		/// <summary>
		/// Commission the appliance. 
		/// </summary>
		public abstract void Commission();

		#endregion

		#region IResolver Members

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance">the object to be released</param>
		public abstract void Release(object instance);

		/// <summary>
		/// Resolve a object to a value.
		/// </summary>
		/// <returns>the resolved object</returns>
		public abstract object Resolve();

		#endregion

		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------

		protected ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}

		/// <summary>
		/// Return the model backing the handler.
		/// </summary>
		protected IDeploymentModel DeploymentModel
		{
			get
			{
				return m_model;
			}
		}

		//-------------------------------------------------------------------
		// Object
		//-------------------------------------------------------------------

		public override String ToString()
		{
			return "appliance:" + DeploymentModel.QualifiedName;
		}
	}
}
