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

	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Containment model is an extended deployment model that aggregates 
	/// a set of models.  A containment model describes a logical containment 
	/// context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public struct IContainmentModel_Fields
	{
		public readonly static System.String KEY = "urn:composition:containment.model";
		public readonly static System.String SECURE_EXECUTION_KEY = "urn:composition:security.enabled";
	}

	public interface IContainmentModel : IDeploymentModel
	{
		/// <summary> Get the startup sequence for the model.</summary>
		IDeploymentModel[] StartupGraph
		{
			get;
		}
		
		/// <summary> Get the shutdown sequence for the model.</summary>
		IDeploymentModel[] ShutdownGraph
		{
			get;
		}
		
		/// <summary> Return the logging categories. </summary>
		/// <returns> the logging categories
		/// </returns>
		CategoriesDirective Categories
		{
			get;
			set;
		}
		
		/// <summary> Return the partition established by the containment model.
		/// 
		/// </summary>
		/// <returns> the partition name
		/// </returns>
		System.String Partition
		{
			get;
		}
		
		/// <summary> Return the classloader model.
		/// 
		/// </summary>
		/// <returns> the classloader model
		/// </returns>
		ITypeLoaderModel TypeLoaderModel
		{
			get;
		}
		
		/// <summary> Returns true if Secure Execution mode has been enabled in the kernel.
		/// 
		/// Secure Execution mode enables the deployer to restrict the exection
		/// environment, and this flag allows for developers to quickly switch
		/// between the secure and non-secure execution modes.
		/// 
		/// </summary>
		/// <returns> true if Secure Execution mode has been enabled in the kernel.
		/// 
		/// </returns>
		bool IsSecureExecutionEnabled();
		
		/// <summary> Assemble the containment model.</summary>
		/// <exception cref=""> Exception if an error occurs during model assembly
		/// </exception>
		void Assemble();
		
		/// <summary> Return the set of models nested within this model.</summary>
		/// <returns> the classloader model
		/// </returns>
		IDeploymentModel[] Models
		{
			get;
		}
		
		/// <summary> Return a model relative to a supplied path.
		/// 
		/// </summary>
		/// <param name="path">a relative or absolute path
		/// </param>
		/// <returns> the model or null if the path is unresolvable
		/// </returns>
		/// <exception cref=""> IllegalArgumentException if the path if badly formed
		/// </exception>
		IDeploymentModel GetModel(System.String path);
		
		/// <summary> Addition of a new subsidiary containment model
		/// using a supplied profile url.
		/// 
		/// </summary>
		/// <param name="url">a containment profile url
		/// </param>
		/// <returns> the model based on the derived profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		IContainmentModel AddContainmentModel(System.Uri url);
		
		/// <summary> Addition of a new subsidiary containment model within
		/// the containment context using a supplied url.
		/// 
		/// </summary>
		/// <param name="block">a url referencing a containment profile
		/// </param>
		/// <param name="config">containment configuration targets
		/// </param>
		/// <returns> the model created using the derived profile and configuration
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		IContainmentModel AddContainmentModel(System.Uri block, System.Uri config);
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context using a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">a containment or deployment profile 
		/// </param>
		/// <returns> the model based on the supplied profile
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		IDeploymentModel AddModel(DeploymentProfile profile);
		
		/// <summary> Addition of a new subsidiary model within
		/// the containment context.
		/// 
		/// </summary>
		/// <param name="model">the model to add 
		/// </param>
		/// <returns> the model 
		/// </returns>
		IDeploymentModel AddModel(IDeploymentModel model);
		
		/// <summary> Remove a named model from this model.</summary>
		/// <param name="name">the name of an immediate child model
		/// </param>
		void RemoveModel(System.String name);
		
		/// <summary> Return the set of service export models.</summary>
		/// <returns> t he export directives
		/// </returns>
		IServiceModel[] ServiceModels
		{
			get;
		}
		
		/// <summary> Return a service exoport model matching a supplied class.</summary>
		/// <returns> the service model
		/// </returns>
		IServiceModel GetServiceModel(System.Type clazz);
		
		/// <summary> Apply a set of override targets resolvable from a supplied url.</summary>
		/// <param name="url">a url resolvable to a TargetDirective[]
		/// </param>
		/// <exception cref=""> ModelException if an error occurs
		/// </exception>
		void  ApplyTargets(System.Uri url);
		
		/// <summary> Apply a set of override targets.</summary>
		/// <param name="targets">a set of target directives
		/// </param>
		void  ApplyTargets(TargetDirective[] targets);
		
		/// <summary> Add a composition listener to the model.</summary>
		/// <param name="listener">the composition listener
		/// </param>
		// void  addCompositionListener(CompositionListener listener);
		
		/// <summary> Remove a composition listener from the model.</summary>
		/// <param name="listener">the composition listener
		/// </param>
		// void  removeCompositionListener(CompositionListener listener);
	}
}