/* 
* Copyright 2003-2004 The Apache Software Foundation
* Licensed  under the  Apache License,  Version 2.0  (the "License");
* you may not use  this file  except in  compliance with the License.
* You may obtain a copy of the License at 
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed  under the  License is distributed on an "AS IS" BASIS,
* WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
* implied.
* 
* See the License for the specific language governing permissions and
* limitations under the License.
*/

namespace Apache.Avalon.Composition.Model
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;
	
	/// <summary> Defintion of a component deployment context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public struct IComponentContext_Fields
	{
		/// <summary> The standard context entry key for the partition name.</summary>
		public readonly static System.String PARTITION_KEY;
		/// <summary> The standard context entry key for the partition name.</summary>
		public readonly static System.String NAME_KEY;
		/// <summary> The standard context entry key for the partition name.</summary>
		//public readonly static System.String CLASSLOADER_KEY;
		/// <summary> The standard context entry key for the partition name.</summary>
		public readonly static System.String HOME_KEY;
		/// <summary> The standard context entry key for the partition name.</summary>
		public readonly static System.String TEMP_KEY;

		static IComponentContext_Fields()
		{
			PARTITION_KEY = ContextDescriptor.PARTITION_KEY;
			NAME_KEY = ContextDescriptor.NAME_KEY;
			//CLASSLOADER_KEY = ContextDescriptor.CLASSLOADER_KEY;
			HOME_KEY = ContextDescriptor.HOME_KEY;
			TEMP_KEY = ContextDescriptor.TEMP_KEY;
		}
	}

	public interface IComponentContext : IDeploymentContext
	{
		/// <summary> Return the working directory for the component.
		/// 
		/// </summary>
		/// <returns> the working directory
		/// </returns>
		System.IO.FileInfo HomeDirectory		
		{
			get;
		}
		
		/// <summary> Return the temporary directory for the component.
		/// 
		/// </summary>
		/// <returns> the temporary directory
		/// </returns>
		System.IO.FileInfo TempDirectory		
		{
			get;
		}

		/// <summary> Return the deployment profile.
		/// 
		/// </summary>
		/// <returns> the profile
		/// </returns>
		ComponentProfile Profile
		{
			get;
		}
		
		/// <summary> Return the component type.
		/// 
		/// </summary>
		/// <returns> the type defintion
		/// </returns>
		TypeDescriptor Type		
		{
			get;
		}
		
		/// <summary> Return the component class.
		/// 
		/// </summary>
		/// <returns> the class
		/// </returns>
		System.Type DeploymentType
		{
			get;
		}
		
		/// <summary> Return the enclosing containment model.</summary>
		/// <returns> the containment model that component is within
		/// </returns>
		IContainmentModel ContainmentModel
		{
			get;
		}
		
		/// <summary> Add a context entry model to the deployment context.</summary>
		/// <param name="model">the entry model
		/// </param>
		void Register(IEntryModel model);
		
		/// <summary> Get a context entry from the deployment context.</summary>
		/// <param name="alias">the entry lookup key
		/// </param>
		/// <returns> value the corresponding value
		/// </returns>
		/// <exception cref=""> ContextException if a key corresponding to the supplied alias is unknown
		/// </exception>
		System.Object Resolve(System.String alias);
	}
}