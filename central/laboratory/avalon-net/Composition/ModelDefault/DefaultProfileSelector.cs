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

namespace Apache.Avalon.Composition.Model.Default
{
	using System;

	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> Default profile selector class. The default selector selects profiles based
	/// of ranking of profile relative to EXPLICIT, PACKAGED and IMPLICIT
	/// status. For each category, if a supplied profile matches the category
	/// the first profile matching the category is returned.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultProfileSelector : IProfileSelector
	{
		//==============================================================
		// ProfileSelector
		//==============================================================
		
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
		public virtual DeploymentProfile Select(DeploymentProfile[] profiles, DependencyDescriptor dependency)
		{
			return Select(profiles);
		}
		
		/// <summary> Returns the preferred profile from an available selection of
		/// candidates profiles.
		/// 
		/// </summary>
		/// <param name="profiles">the set of candidate profiles
		/// </param>
		/// <param name="dependency">a stage dependency
		/// </param>
		/// <returns> the preferred extension provider profile or null if 
		/// no satisfactory profile can be established
		/// </returns>
		public virtual DeploymentProfile Select(DeploymentProfile[] profiles, StageDescriptor stage)
		{
			return Select(profiles);
		}
		
		//==============================================================
		// implementation
		//==============================================================
		
		/// <summary> Select a profile from a set of profiles based on a priority ordering
		/// of EXPLICIT, PACKAGE and lastly IMPLICIT.  If multiple candidates
		/// exist for a particular mode, return the first candidate.
		/// 
		/// </summary>
		/// <param name="profiles">the set of candidate profiles
		/// </param>
		/// <param name="dependency">the service dependency
		/// </param>
		/// <returns> the preferred profile or null if no satisfactory 
		/// provider can be established
		/// </returns>
		private DeploymentProfile Select(DeploymentProfile[] profiles)
		{
			if (profiles.Length == 0)
			{
				return null;
			}
			
			for (int i = 0; i < profiles.Length; i++)
			{
				if (profiles[i].Mode.Equals(Mode.Explicit))
				{
					return profiles[i];
				}
			}
			
			for (int i = 0; i < profiles.Length; i++)
			{
				if (profiles[i].Mode.Equals(Mode.Packaged))
				{
					return profiles[i];
				}
			}
			
			for (int i = 0; i < profiles.Length; i++)
			{
				if (profiles[i].Mode.Equals(Mode.Implicit))
				{
					return profiles[i];
				}
			}
			
			return null;
		}
	}
}