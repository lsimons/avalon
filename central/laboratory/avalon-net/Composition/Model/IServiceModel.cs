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
	using ServiceDirective = Apache.Avalon.Composition.Data.ServiceDirective;
	
	/// <summary> Service model manages service exported by a container.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IServiceModel
	{
		/// <summary> Return the service provider.  </summary>
		/// <returns> the model identifying the provider implementation
		/// </returns>
		IDeploymentModel ServiceProvider
		{
			get;
			
		}
		/// <summary> Return the service directive for the model.
		/// 
		/// </summary>
		/// <returns> the directive declaring the service export
		/// </returns>
		ServiceDirective ServiceDirective
		{
			get;
		}
		
		/// <summary> Return the service class.  </summary>
		/// <returns> the service class
		/// </returns>
		System.Type ServiceClass
		{
			get;
		}
	}
}