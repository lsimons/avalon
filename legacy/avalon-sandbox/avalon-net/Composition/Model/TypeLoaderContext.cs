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
	using Apache.Avalon.Repository;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Defintion of a working context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface ITypeLoaderContext : IContext
	{
		/// <summary> Return the logging channel to be applied to the 
		/// classloader model.
		/// 
		/// </summary>
		/// <returns> the system logging channel
		/// </returns>
		ILogger Logger
		{
			get;
		}
		
		/// <summary> Return the local repository.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		IRepository Repository
		{
			get;
		}
		
		/// <summary> Return the base directory from which relative library directives
		/// and fileset directory paths may be resolved.
		/// 
		/// </summary>
		/// <returns> the base directory
		/// </returns>
		System.IO.FileInfo BaseDirectory
		{
			get;
		}
		
		/// <summary> Return the optional packages already establised relative to 
		/// the parent classloader.
		/// 
		/// </summary>
		/// <returns> the array of established optional packages
		/// </returns>
		// OptionalPackage[] getOptionalPackages();
		
		/// <summary> Return the extension manager established by the parent 
		/// classloader model.
		/// 
		/// </summary>
		/// <returns> the extension manager
		/// </returns>
		// ExtensionManager getExtensionManager();
		
		/// <summary> Return the classloader directive to be applied to the 
		/// classloader model.
		/// 
		/// </summary>
		/// <returns> the classloader directive
		/// </returns>
		TypeLoaderDirective TypeLoaderDirective
		{
			get;
		}
		
		/// <summary> Return the type repository established by the parent classloader.
		/// 
		/// </summary>
		/// <returns> the type repository
		/// </returns>
		ITypeRepository TypeRepository
		{
			get;
		}
		
		/// <summary> Return the service repository established by the parent classloader.
		/// 
		/// </summary>
		/// <returns> the service repository
		/// </returns>
		IServiceRepository ServiceRepository
		{
			get;
		}
		
		/// <summary> Return any implied urls to include in the classloader.
		/// 
		/// </summary>
		/// <returns> the implied urls
		/// </returns>
		System.Uri[] ImplicitURLs
		{
			get;
		}
	}
}