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
	using DeploymentProfile = Apache.Avalon.Composition.Data.DeploymentProfile;
	
	/// <summary> Interface defining the contract for profile selection.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IProfileSelector
	{
		/// <summary> Returns the preferred profile from an available selection of
		/// candidates profiles.
		/// 
		/// </summary>
		/// <param name="profiles">the set of candidate profiles
		/// </param>
		/// <param name="dependency">a service dependency
		/// </param>
		/// <returns> the preferred profile or null if no satisfactory profile 
		/// can be established
		/// </returns>
		DeploymentProfile Select(DeploymentProfile[] profiles, DependencyDescriptor dependency);
		
		/// <summary> Returns the preferred profile from an available selection of
		/// candidates profiles.
		/// 
		/// </summary>
		/// <param name="profiles">the set of candidate profiles
		/// </param>
		/// <param name="stage">a stage dependency
		/// </param>
		/// <returns> the preferred extension provider profile or null if 
		/// no satisfactory profile can be established
		/// </returns>
		DeploymentProfile Select(DeploymentProfile[] profiles, StageDescriptor stage);
	}
}