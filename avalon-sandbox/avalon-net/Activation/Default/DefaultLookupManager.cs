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

	/// <summary>
	/// Summary description for DefaultLookupManager.
	/// </summary>
	public class DefaultLookupManager : ILookupManager
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private IComponentModel m_model;

		private ILogger m_logger;

		/// <summary>
		/// A table of identity hashcode integers of established objects 
		/// that map to the lookup key that was uased to establish the object.
		/// Used to track which model is providing an object when we 
		/// handle release of objects.
		/// </summary>
		private Hashtable m_table = new Hashtable();

		/// <summary>
		/// A table of dependency models keyed by lookup key.
		/// </summary>
		private Hashtable m_map;

		/// <summary>
		/// Construct a new ServiceManager.
		/// </summary>
		/// <param name="model">component model of the component that is to be services</param>
		public DefaultLookupManager( IComponentModel model )
		{
			if( model == null )
			{
				throw new ArgumentNullException( "model" );
			}

			m_model = model;
			m_logger = model.Logger;

			m_map = new Hashtable();
			IDependencyModel[] dependencies = model.DependencyModels;
			
			foreach(IDependencyModel dependency in dependencies)
			{
				String key = dependency.Dependency.Key;
				m_map[ key ] = dependency;
			}
		}

		#region ILookupManager Members

		public object this[string role]
		{
			get
			{
				if( role == null )
				{
					throw new ArgumentNullException( "role" );
				}

				if( !Contains( role ) )
				{
					String error = "Unknown key: " + role;
					throw new LookupException( role, error );
				}

				//
				// locate the provider model that is prividing components
				// for this dependency
				//

				IDependencyModel dependency = (IDependencyModel) m_map[ role ];
				IDeploymentModel provider = dependency.Provider;
				if( null == provider )
				{
					String error = "service.error.null-provider " + role;
					throw new ApplicationException( error );
				}

				//
				// get a proxy to the service from the provider
				// (note that it is up to a provider to determine if
				// a proxy if generated based on its service export 
				// parameters)
				//

				try
				{
					Object instance = provider.Resolve();

					//
					// otherwise we need to hold a reference linking the 
					// object with the source provider
					//

					String id = "" + instance.GetHashCode();
					m_table[ id ] = role;
					if( Logger.IsDebugEnabled )
					{
						String message = "resolved service [" 
						+ id 
						+ "] for the role [" 
						+ role 
						+ "].";
						Logger.Debug( message );
					}

					return instance;
				}
				catch( Exception e )
				{
					//
					// TODO: framework states that ServiceException is thrown
					// if the service is not found - and in this case that isn't 
					// the issue - in effect we have a good key, but we simply
					// have not been able to go from key to instance -
					// should look into some more concrete subtypes of 
					// ServiceException

					String error = "Unexpected runtime error while attempting to resolve service for key: " + role;
					throw new FatalServiceException( role, error, e );
				}
			}
		}

		public void Release(object instance)
		{
			if( instance == null ) return;

			//
			// otherwise we need to locate the source ourselves
			//

			String id = "" + instance.GetHashCode();
			String key = (String) m_table[ id ];
			
			if( key == null )
			{
				if( Logger.IsWarnEnabled )
				{
					String warning = 
						"Unrecognized object identity [" 
						+ id 
						+ "]. "
						+ "Either this object was not provided by this service manager "
						+ "or it has already been released.";
					Logger.Warn( warning );
				}
				return;
			}

			IDependencyModel dependency = (IDependencyModel) m_map[ key ];
			IDeploymentModel provider = dependency.Provider;
			if( provider == null )
			{
				if( Logger.IsErrorEnabled )
				{
					String error = 
						"Unable to release component as no provider could be found for the key ["
						+ key
						+ "].";
					Logger.Warn( error );
				}
				return;
			}

			provider.Release( instance );
			if( Logger.IsDebugEnabled )
			{
				String message = 
								 "released service [" 
								 + id 
								 + "] from the key [" 
								 + key 
								 + "].";
				Logger.Debug( message );
			}

			m_table.Remove( id );
		}

		public bool Contains(string role)
		{
			if( role == null )
			{
				return false;
			}
			return m_map.Contains( role );
		}

		#endregion

		private ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}
	}
}
