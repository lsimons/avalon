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
	using Apache.Avalon.Composition.Model.Default;

	/// <summary>
	/// Summary description for DefaultAppliance.
	/// </summary>
	public class DefaultAppliance : AbstractAppliance
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private IComponentModel m_model;

		private ILifestyleManager m_lifestyle;

		private DefaultState m_commissioned = new DefaultState();

		private long m_delay = 0;

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public DefaultAppliance( IComponentModel model, ILifestyleManager lifestyle ) : base(model)
		{
			m_model = model;
			m_lifestyle = lifestyle;
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
					m_delay = m_model.DeploymentTimeout;
					m_lifestyle.Commission();
					m_delay = 0;
					m_commissioned.Enabled = true;
				}
				finally
				{
					m_delay = 0;
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
				m_lifestyle.Decommission();
				m_commissioned.Enabled = false;
			}
		}

		//-------------------------------------------------------------------
		// Resolver
		//-------------------------------------------------------------------

		/// <summary>
		/// Resolve a object to a value.
		/// </summary>
		/// <returns> the resolved object</returns>
		public override object Resolve()
		{
			if( ComponentModel.TypeDescriptor.Info.GetAttribute( "urn:activation:proxy", "true" ).Equals( "false" ) )
			{
				return Resolve( false );
			}
			else        
			{
				return Resolve( true );
			}
		}

		protected virtual object Resolve( bool proxy )
		{
			if( !proxy )
			{
				if( m_delay > 0 )
				{
					String error = "appliance.error.resolve.transient " + this.ToString() + " " + m_delay;
					throw new TransientRuntimeException( error, m_delay );
				}
				else if( !m_commissioned.Enabled )
				{
					String error = "appliance.error.resolve.non-commission-state " + this.ToString();
					throw new ApplicationException( error );
				}
				else
				{
					return m_lifestyle.Resolve();
				}
			}

			throw new ArgumentException( "proxy" );

			/*
			else
			{
				ComponentModel model = getComponentModel();
				Logger logger = model.getLogger().getChildLogger( "proxy" );
				ApplianceInvocationHandler handler = 
				new ApplianceInvocationHandler( this, logger );

				try
				{
					return Proxy.newProxyInstance( 
					model.getDeploymentClass().getClassLoader(),
					model.getInterfaces(),
					handler );
				}
				catch( Throwable e )
				{
					final String error = 
					"Proxy establishment failure in block: " + this;
					throw new ApplianceException( error, e );
				}
			}*/
		}

		/// <summary>
		/// Release an object
		/// </summary>
		/// <param name="instance">the object to be released</param>
		public override void Release( object instance )
		{
			if( null == instance ) return;
			if( !m_commissioned.Enabled ) return;

			/*if( Proxy.isProxyClass( instance.getClass() ) )
			{
				ApplianceInvocationHandler handler = 
				(ApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
				handler.release();
			}
			else*/
			{
				m_lifestyle.Release( instance );
			}
		}

		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------

		/// <summary>
		/// Return the model backing the handler.
		/// </summary>
		protected IComponentModel ComponentModel
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
			return "appliance:" + ComponentModel.QualifiedName;
		}
	}
}
