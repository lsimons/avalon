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
	
	/// <summary> Interface implemented by a service selection implementation mechanism.  Classes
	/// implementing the selector interface may be activated during the selection of
	/// candidate service providers in an automated assembly process. 
	/// A component author may declare a selection class explicitly via a
	/// service dependency attribute with the attribute name of 
	/// <code>urn:avalon:profile.selector</code> (but this will change to a model
	/// driven approach).
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IModelSelector
	{
		/// <summary> Returns the preferred model from an available selection of
		/// candidates capable of fulfilling a supplied service dependency.
		/// 
		/// </summary>
		/// <param name="models">the set of candidate models
		/// </param>
		/// <param name="dependency">a service dependency
		/// </param>
		/// <returns> the preferred model or null if no satisfactory provider 
		/// can be established
		/// </returns>
		IDeploymentModel Select(IDeploymentModel[] models, DependencyDescriptor dependency);
		
		/// <summary> Returns the preferred model from an available selection of candidates</summary>
		/// <param name="models">the set of candidate models 
		/// </param>
		/// <param name="stage">the stage dependency
		/// </param>
		/// <returns> the preferred provider or null if no satisfactory provider 
		/// can be established
		/// </returns>
		IDeploymentModel Select(IDeploymentModel[] models, StageDescriptor stage);
	}
}