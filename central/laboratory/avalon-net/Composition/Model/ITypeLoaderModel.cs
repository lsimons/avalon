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

	using ContainmentProfile = Apache.Avalon.Composition.Data.ContainmentProfile;
	using Apache.Avalon.Framework;
	
	/// <summary> <p>Specification of a classloader model from which a 
	/// a fully qualifed classpath can be established.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface ITypeLoaderModel
	{
		
		/// <summary> Return the classloader model type repository.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		ITypeRepository TypeRepository
		{
			get;
		}
		
		/// <summary> Return the classloader model service repository.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		IServiceRepository ServiceRepository
		{
			get;
		}
		
		/// <summary> Return the optional extensions manager.</summary>
		/// <returns> the extension manager
		/// </returns>
		// ExtensionManager getExtensionManager();
		
		/// <summary> Return the set of local established optional packages.
		/// 
		/// </summary>
		/// <returns> the local set of optional packages
		/// </returns>
		// OptionalPackage[] getOptionalPackages();
		
		/// <summary> Return the set of optional packages already established including
		/// the optional packages established by any parent classloader model.
		/// 
		/// </summary>
		/// <param name="policy">if TRUE, return the local and all ancestor optional 
		/// package - if FALSE only return the local packages
		/// </param>
		/// <returns> the OptionalPackage instances
		/// </returns>
		// OptionalPackage[] getOptionalPackages(bool policy);
		
		/// <summary> Return the fully qualified classpath including extension jar files
		/// resolved relative to a classpath directives.
		/// 
		/// </summary>
		/// <returns> an array of URL representing the qualified classpath 
		/// </returns>
		System.Uri[] QualifiedClassPath
		{
			get;
		}
		
		/// <summary> Creation of a classloader model using this model as the 
		/// relative parent.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel 
		/// </param>
		/// <param name="profile">the containment profile
		/// </param>
		/// <param name="implied">any implied urls
		/// </param>
		/// <returns> a new classloader context
		/// </returns>
		ITypeLoaderModel CreateTypeLoaderModel(ILogger logger, ContainmentProfile profile, System.Uri[] implied);
	}
}