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
	
	/// <summary> Defintion of runtime services.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IRuntime
	{
		//------------------------------------------------------------------
		// runtime operations
		//------------------------------------------------------------------
		
		/// <summary> Request the commissioning of a runtime for a supplied deployment 
		/// model.d
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		void  Commission(IDeploymentModel model);
		
		/// <summary> Request the decommissioning of a runtime for a supplied deployment 
		/// model.
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		void  Decommission(IDeploymentModel model);
		
		/// <summary> Request resolution of an object from the runtime.</summary>
		/// <param name="model">the deployment model
		/// </param>
		/// <exception cref=""> Exception if a deployment error occurs
		/// </exception>
		System.Object Resolve(IDeploymentModel model);
		
		/// <summary> Request the release of an object from the runtime.</summary>
		/// <param name="model">the deployment model
		/// </param>
		/// <param name="instance">the object to release
		/// </param>
		/// <exception cref=""> Exception if a deployment error occurs
		/// </exception>
		void Release(IDeploymentModel model, System.Object instance);
	}
}