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
	using DependencyDescriptor = Apache.Avalon.Meta.DependencyDescriptor;
	using StageDescriptor = Apache.Avalon.Meta.StageDescriptor;
	
	/// <summary> The model repository interface declares operations through which 
	/// clients may resolve new or existing model instances relative to
	/// a stage or service dependency.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IModelRepository
	{
		/// <summary> Locate an model meeting the supplied criteria.
		/// 
		/// </summary>
		/// <returns> the model
		/// </returns>
		IDeploymentModel[] Models
		{
			get;
			
		}
		/// <summary> Locate an model matching the supplied name.
		/// 
		/// </summary>
		/// <param name="name">the model name
		/// </param>
		/// <returns> the model or null if the model name is unknown
		/// </returns>
		IDeploymentModel GetModel(System.String name);
		
		/// <summary> Locate a model meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="dependency">a component service dependency
		/// </param>
		/// <returns> the model
		/// </returns>
		IDeploymentModel GetModel(DependencyDescriptor dependency);
		
		/// <summary> Locate all models meeting the supplied dependency criteria.
		/// 
		/// </summary>
		/// <param name="dependency">a component service dependency
		/// </param>
		/// <returns> the candidate models
		/// </returns>
		IDeploymentModel[] GetCandidateProviders(DependencyDescriptor dependency);
		
		/// <summary> Locate all models meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="stage">a component stage dependency
		/// </param>
		/// <returns> the candidate models
		/// </returns>
		IDeploymentModel[] GetCandidateProviders(StageDescriptor stage);
		
		/// <summary> Locate a model meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="stage">a component stage dependency
		/// </param>
		/// <returns> the model
		/// </returns>
		IDeploymentModel GetModel(StageDescriptor stage);
		
		/// <summary> Add an model to the repository.
		/// 
		/// </summary>
		/// <param name="model">the model to add
		/// </param>
		void AddModel(IDeploymentModel model);
		
		/// <summary> Add an model to the repository.
		/// 
		/// </summary>
		/// <param name="name">the name to register the model under
		/// </param>
		/// <param name="model">the model to add
		/// </param>
		void AddModel(System.String name, IDeploymentModel model);
		
		/// <summary> Remove an model from the repository.
		/// 
		/// </summary>
		/// <param name="model">the model to remove
		/// </param>
		void RemoveModel(IDeploymentModel model);
	}
}