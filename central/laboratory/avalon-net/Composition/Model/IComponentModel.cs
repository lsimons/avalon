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

	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	
	/// <summary> Deployment model defintion.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IComponentModel : IDeploymentModel
	{
		/// <summary> Test if the component type backing the model is 
		/// parameterizable.
		/// 
		/// </summary>
		/// <returns> TRUE if the component type is parameterizable
		/// otherwise FALSE
		/// </returns>
		/*
		bool Parameterizable
		{
			get;
			
		}*/
		
		/// <summary> Return the deployment type.
		/// 
		/// </summary>
		/// <returns> the type
		/// </returns>
		TypeDescriptor TypeDescriptor
		{
			get;
		}
		
		/// <summary> Return the collection policy for the model. If a profile
		/// does not declare a collection policy, the collection policy 
		/// declared by the type will be used.
		/// 
		/// </summary>
		/// <returns> the collection policy
		/// </returns>
		CollectionPolicy CollectionPolicy
		{
			get;
			set;
		}
		
		/// <summary> Return the logging categories. </summary>
		/// <returns> the logging categories
		/// </returns>
		CategoriesDirective Categories
		{
			get;
			set;
		}
		
		/// <summary> Set the activation policy for the model. </summary>
		/// <param name="policy">the activaltion policy
		/// </param>
		ActivationPolicy ActivationPolicy 
		{
			get;
			set;
		}
		
		/// <summary> Set the activation policy for the model to the default value. </summary>
		void RevertActivationPolicy();
		
		/// <summary> Return the class for the deployable target.</summary>
		/// <returns> the class
		/// </returns>
		System.Type DeploymentType
		{
			get;
		}
		
		/// <summary> Set the configuration to the supplied value.  The supplied 
		/// configuration will replace the existing configuration.
		/// 
		/// </summary>
		/// <param name="config">the supplied configuration
		/// </param>
		void SetConfiguration(IConfiguration config);
		
		/// <summary> Set the configuration to the supplied value.  The supplied 
		/// configuration may suppliment or replace the existing configuration.
		/// 
		/// </summary>
		/// <param name="config">the supplied configuration
		/// </param>
		/// <param name="policy">if TRUE the supplied configuration replaces the current
		/// configuration otherwise the resoved configuration shall be layed above
		/// the configuration supplied with the profile which in turn is layer above 
		/// the type default configuration (if any)
		/// </param>
		void SetConfiguration(IConfiguration config, bool policy);
		
		/// <summary> Return the configuration to be applied to the component.
		/// The implementation returns the current configuration state.
		/// If the the component type does not implementation the 
		/// Configurable interface, the implementation returns null. 
		/// 
		/// </summary>
		/// <returns> the qualified configuration
		/// </returns>
		IConfiguration Configuration
		{
			get;
		}
		
		/// <summary> Set the parameters to the supplied value.  The supplied 
		/// parameters value will replace the existing parameters value.
		/// 
		/// </summary>
		/// <param name="parameters">the supplied parameters value
		/// </param>
		// void  setParameters(Parameters parameters);
		
		/// <summary> Set the parameters to the supplied value.  The supplied 
		/// parameters value may suppliment or replace the existing 
		/// parameters value.
		/// 
		/// </summary>
		/// <param name="parameters">the supplied parameters
		/// </param>
		/// <param name="policy">if TRUE the supplied parameters replaces the current
		/// parameters value otherwise the existing and supplied values
		/// are aggregrated
		/// </param>
		// void  setParameters(Parameters parameters, bool policy);
		
		/// <summary> Return the parameters to be applied to the component.
		/// If the the component type does not implementation the 
		/// Parameterizable interface, the implementation returns null. 
		/// 
		/// </summary>
		/// <returns> the assigned parameters
		/// </returns>
		// Parameters getParameters();
		
		/// <summary> Rest if the component type backing the model requires the 
		/// establishment of a runtime context.
		/// 
		/// </summary>
		/// <returns> TRUE if the component type requires a runtime
		/// context otherwise FALSE
		/// </returns>
		bool IsContextDependent
		{
			get;
		}
		
		/// <summary> Return the context model for this deployment model.
		/// 
		/// </summary>
		/// <returns> the context model if this model is context dependent, else
		/// the return value is null
		/// </returns>
		IContextModel ContextModel
		{
			get;
		}
		
		/// <summary> Return the dependency models for this deployment model.
		/// 
		/// </summary>
		/// <returns> the dependency models
		/// </returns>
		IDependencyModel[] DependencyModels
		{
			get;
		}
		
		/// <summary> Return a dependency model for a supplied descriptor or null
		/// if no match found.
		/// 
		/// </summary>
		/// <returns> the dependency model
		/// </returns>
		IDependencyModel GetDependencyModel(DependencyDescriptor dependency);
		
		/// <summary> Return the stage models for this deployment model.
		/// 
		/// </summary>
		/// <returns> the stage models
		/// </returns>
		IStageModel[] StageModels
		{
			get;
		}
		
		/// <summary> Return a stage model matching the supplied descriptor or null
		/// if no match found.
		/// 
		/// </summary>
		/// <param name="stage">the stage descriptor
		/// </param>
		/// <returns> the matching stage model
		/// </returns>
		IStageModel GetStageModel(StageDescriptor stage);
		
		/// <summary> Return the set of services produced by the model as a array of classes.
		/// 
		/// </summary>
		/// <returns> the service classes
		/// </returns>
		System.Type[] Interfaces
		{
			get;
		}
	}
}