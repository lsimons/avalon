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
	using Apache.Avalon.Repository;
	using Apache.Avalon.Composition.Logging;
	
	/// <summary> Defintion of a system context that exposes a system wide set of parameters.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface ISystemContext : IContext
	{
		/// <summary> Return the model factory.
		/// 
		/// </summary>
		/// <returns> the factory
		/// </returns>
		IModelFactory ModelFactory
		{
			get;
		}
		
		/// <summary> Return the base directory from which relative references 
		/// should be resolved.
		/// 
		/// </summary>
		/// <returns> the base directory
		/// </returns>
		System.IO.FileInfo BaseDirectory
		{
			get;
		}
		
		/// <summary> Return the home directory from which containers may establish
		/// persistent content.
		/// 
		/// </summary>
		/// <returns> the working directory
		/// </returns>
		System.IO.FileInfo HomeDirectory
		{
			get;
		}
		
		/// <summary> Return the temp directory from which containers may establish
		/// non-persistent content.
		/// 
		/// </summary>
		/// <returns> the temp directory
		/// </returns>
		System.IO.FileInfo TempDirectory
		{
			get;
		}
		
		/// <summary> Return the application repository from which resource 
		/// directives can be resolved.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		IRepository Repository
		{
			get;
		}
		
		/// <summary> Return the system trace flag.
		/// 
		/// </summary>
		/// <returns> the trace flag
		/// </returns>
		bool IsTraceEnabled
		{
			get;
		}
		
		/// <summary> Return the logging manager.
		/// 
		/// </summary>
		/// <returns> the logging manager.
		/// </returns>
		ILoggingManager LoggingManager
		{
			get;
		}
		
		/// <summary> Return the system logging channel.
		/// 
		/// </summary>
		/// <returns> the system logging channel
		/// </returns>
		ILogger Logger
		{
			get;
		}
		
		/// <summary> Return the default deployment phase timeout value.</summary>
		/// <returns> the timeout value
		/// </returns>
		long DefaultDeploymentTimeout
		{
			get;
		}
		
		//------------------------------------------------------------------
		// runtime operations
		//------------------------------------------------------------------
		
		/// <summary> Request the commissioning of a runtime for a supplied deployment 
		/// model.
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		void Commission(IDeploymentModel model);
		
		/// <summary> Request the decommissioning of a runtime for a supplied deployment 
		/// model.
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		void Decommission(IDeploymentModel model);
		
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