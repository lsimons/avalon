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
	using Mode = Apache.Avalon.Composition.Data.Mode;
	using DependencyDescriptor = Apache.Avalon.Meta.DependencyDescriptor;
	using ServiceDescriptor = Apache.Avalon.Meta.ServiceDescriptor;
	using StageDescriptor = Apache.Avalon.Meta.StageDescriptor;
	using Apache.Avalon.Framework;
	
	/// <summary> Model desribing a deployment scenario.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public struct IDeploymentModel_Fields
	{
		public readonly static System.String SEPARATOR = "/";
		public readonly static System.String DEPLOYMENT_TIMEOUT_KEY = "urn:composition:deployment.timeout";
	}

	public interface IDeploymentModel : ICommissionable, IResolver
	{
		/// <summary> Return the name of the model.</summary>
		/// <returns> the name
		/// </returns>
		System.String Name
		{
			get;
		}
		
		/// <summary> Return the model partition path.</summary>
		/// <returns> the path
		/// </returns>
		System.String Path
		{
			get;
		}
		
		/// <summary> Return the model fully qualified name.</summary>
		/// <returns> the fully qualified name
		/// </returns>
		System.String QualifiedName
		{
			get;
		}
		
		/// <summary> Return the mode of model establishment.</summary>
		/// <returns> the mode
		/// </returns>
		Mode Mode
		{
			get;
		}
		
		/// <summary> Return the assigned logging channel.</summary>
		/// <returns> the logging channel
		/// </returns>
		ILogger Logger
		{
			get;
		}
		
		//-----------------------------------------------------------
		// service production
		//-----------------------------------------------------------
		
		/// <summary> Return the set of services produced by the model.</summary>
		/// <returns> the services
		/// </returns>
		ServiceDescriptor[] Services
		{
			get;
		}
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// depedendency.
		/// </summary>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		bool IsaCandidate(DependencyDescriptor dependency);
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// stage dependency.
		/// </summary>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		bool IsaCandidate(StageDescriptor stage);
		
		//-----------------------------------------------------------
		// composite assembly
		//-----------------------------------------------------------
		
		/// <summary> Returns the assembled state of the model.</summary>
		/// <returns> true if this model is assembled
		/// </returns>
		bool IsAssembled
		{
			get;
		}
		
		/// <summary> Assemble the model.</summary>
		/// <param name="subjects">a list of deployment models that make up the assembly chain
		/// </param>
		/// <exception cref=""> Exception if an error occurs during model assembly
		/// </exception>
		void Assemble(System.Collections.IList subjects);
		
		/// <summary> Return the set of models consuming this model.</summary>
		/// <returns> the consumers
		/// </returns>
		IDeploymentModel[] ConsumerGraph
		{
			get;
		}
		
		/// <summary> Return the set of models supplying this model.</summary>
		/// <returns> the providers
		/// </returns>
		IDeploymentModel[] ProviderGraph
		{
			get;
		}
		
		/// <summary> Disassemble the model.</summary>
		void Disassemble();
		
		/// <summary> Return the set of models assigned as providers.</summary>
		/// <returns> the providers consumed by the model
		/// </returns>
		/// <exception cref=""> IllegalStateException if invoked prior to 
		/// the completion of the assembly phase 
		/// </exception>
		IDeploymentModel[] Providers
		{
			get;
		}
		
		/// <summary> Return the default deployment timeout value declared in the 
		/// kernel configuration.  The implementation looks for a value
		/// assigned under the property key "urn:composition:deployment.timeout"
		/// and defaults to 1000 msec if undefined.
		/// 
		/// </summary>
		/// <returns> the default deployment timeout value
		/// </returns>
		long DeploymentTimeout
		{
			get;
		}
	}
}