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
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Model.Default;

	/// <summary>
	/// Summary description for DefaultBlock.
	/// </summary>
	public class DefaultBlock : AbstractAppliance
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private IContainmentModel m_model;

		private ISystemContext m_system;

		private DefaultState m_commissioned = new DefaultState();

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public DefaultBlock( ISystemContext system, IContainmentModel model ) : base( model )
		{
			m_model = model;
			m_system = system;
		}

		//-------------------------------------------------------------------
		// Commissionable
		//-------------------------------------------------------------------

		/// <summary>
		/// Commission the appliance. 
		/// </summary>
		public override void Commission()
		{
			lock( m_commissioned )
			{
				if( m_commissioned.Enabled ) return;

				try
				{
					/*
					ILogger log = m_model.Logger.CreateChildLogger( "proxy" );
					final BlockInvocationHandler handler = 
					new BlockInvocationHandler( log, this );
					final Class[] classes = getInterfaceClasses();
	            
					m_proxy = Proxy.newProxyInstance( 
					m_model.getClassLoaderModel().getClassLoader(),
					classes,
					handler );*/

					m_commissioned.Enabled = true;
				}
				catch( Exception e )
				{
					String error = "Composite service establishment failure in block: " + this;
					throw new ApplianceRuntimeException( error, e );
				}
			}
		}

		/// <summary>
		/// Decommission the appliance.  Once an appliance is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public override void Decommission()
		{
			lock( m_commissioned )
			{
				if( !m_commissioned.Enabled ) return;
				/*if( null != m_proxy )
				{
					m_proxy = null;
				}*/
				m_commissioned.Enabled = false;
			}
		}

		//-------------------------------------------------------------------
		// Resolver
		//-------------------------------------------------------------------

		/// <summary>
		/// Resolve a object to a value.
		/// </summary>
		/// <returns></returns>
		public override object Resolve() 
		{
			if( !m_commissioned.Enabled )
			{
				String error = "block.error.resolve.non-commission-state " + this.ToString();
				throw new ApplicationException( error );
			}
			// return m_proxy;
			return this;
		}

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance">the object to be released</param>
		public override void Release( object instance )
		{
			// ignore
		}

		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------

		/// <summary>
		/// Return the model backing the handler.
		/// </summary>
		protected IContainmentModel ContainmentModel
		{
			get
			{
				return m_model;
			}
		}

		private Type[] InterfaceTypes
		{
			get
			{
				IContainmentModel model = ContainmentModel;
				ArrayList list = new ArrayList();
				IServiceModel[] services = model.ServiceModels;
				for( int i=0; i < services.Length; i++ )
				{
					IServiceModel service = services[i];
					list.Add( service.ServiceClass );
				}
				return (Type[]) list.ToArray( typeof(Type) );
			}
		}

		//-------------------------------------------------------------------
		// Object
		//-------------------------------------------------------------------

		public override String ToString()
		{
			return "block:" + ContainmentModel.QualifiedName;
		}
	}
}
