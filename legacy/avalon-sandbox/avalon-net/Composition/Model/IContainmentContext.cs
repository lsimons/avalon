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

	
	/// <summary> Defintion of a working context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IContainmentContext : IDeploymentContext
	{
		/// <summary> Return the classloader model.
		/// 
		/// </summary>
		/// <returns> the type manager assigned to the containment model.
		/// </returns>
		ITypeLoaderModel TypeLoaderModel
		{
			get;
		}
		
		/// <summary> Return the working directory for a container.
		/// 
		/// </summary>
		/// <returns> the working directory
		/// </returns>
		System.IO.FileInfo HomeDirectory
		{
			get;
		}
		
		/// <summary> Return the temporary directory for a container. 
		/// 
		/// </summary>
		/// <returns> the temporary directory
		/// </returns>
		System.IO.FileInfo TempDirectory
		{
			get;
		}
		
		/// <summary> Return the containment profile.
		/// 
		/// </summary>
		/// <returns> the containment profile
		/// </returns>
		ContainmentProfile ContainmentProfile
		{
			get;
		}
		
		/// <summary> Return the model repository.
		/// 
		/// </summary>
		/// <returns> the model repository
		/// </returns>
		IModelRepository ModelRepository
		{
			get;
		}
		
		/// <summary> Return the parent container model. If the container is a root
		/// container, the operation shall return a null value.
		/// 
		/// </summary>
		/// <returns> the parent containment model
		/// </returns>
		IContainmentModel ParentContainmentModel
		{
			get;
		}
	}
}