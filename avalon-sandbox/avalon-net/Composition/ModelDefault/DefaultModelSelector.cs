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
	
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Meta;
	
	/// <summary> Default selector class. The default selector selcts profiles based
	/// of ranking of profile relative to EXPLICIT, PACKAGED and IMPLICIT
	/// status. For each category, if a supplied profile matches the category
	/// the first profile matching the category is returned.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultModelSelector : IModelSelector
	{
		//==============================================================
		// ModelSelector
		//==============================================================
		
		/// <summary> Returns the preferred model from an available selection of
		/// candidates capable of fulfilling a supplied service dependency.
		/// 
		/// </summary>
		/// <param name="models">the set of candidate models
		/// </param>
		/// <param name="dependency">a service dependency
		/// </param>
		/// <returns> the preferred model or null if no satisfactory 
		/// provider can be established
		/// </returns>
		public virtual IDeploymentModel Select(IDeploymentModel[] models, DependencyDescriptor dependency)
		{
			IDeploymentModel[] candidates = FilterCandidateProviders(models, dependency);
			return Select(candidates);
		}
		
		/// <summary> Returns the preferred model from an available selection of candidates</summary>
		/// <param name="models">the set of candidate models 
		/// </param>
		/// <param name="stage">the stage dependency
		/// </param>
		/// <returns> the preferred provider or null if no satisfactory 
		/// provider can be established
		/// </returns>
		public virtual IDeploymentModel Select(IDeploymentModel[] models, StageDescriptor stage)
		{
			IDeploymentModel[] candidates = FilterCandidateProviders(models, stage);
			return Select(candidates);
		}
		
		//==============================================================
		// implementation
		//==============================================================
		
		private IDeploymentModel[] FilterCandidateProviders(IDeploymentModel[] models, DependencyDescriptor dependency)
		{
			//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < models.Length; i++)
			{
				IDeploymentModel model = models[i];
				if (model.IsaCandidate(dependency))
				{
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(model);
				}
			}
			return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
		}
		
		private IDeploymentModel[] FilterCandidateProviders(IDeploymentModel[] models, StageDescriptor stage)
		{
			//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < models.Length; i++)
			{
				IDeploymentModel model = models[i];
				if (model.IsaCandidate(stage))
				{
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(model);
				}
			}
			return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Select a model from a set of models based on a priority ordering
		/// of EXPLICIT, PACKAGE and lastly IMPLICIT.  If multiple candidates
		/// exist for a particulr mode, return the first candidate.
		/// 
		/// </summary>
		/// <param name="profiles">the set of candidate profiles
		/// </param>
		/// <param name="dependency">the service dependency
		/// </param>
		/// <returns> the preferred profile or null if no satisfactory 
		/// provider can be established
		/// </returns>
		private IDeploymentModel Select(IDeploymentModel[] models)
		{
			if (models.Length == 0)
			{
				return null;
			}
			
			for (int i = 0; i < models.Length; i++)
			{
				if (models[i].Mode.Equals(Mode.Explicit))
				{
					return models[i];
				}
			}
			
			for (int i = 0; i < models.Length; i++)
			{
				if (models[i].Mode.Equals(Mode.Packaged))
				{
					return models[i];
				}
			}
			
			for (int i = 0; i < models.Length; i++)
			{
				if (models[i].Mode.Equals(Mode.Implicit))
				{
					return models[i];
				}
			}
			
			return null;
		}
	}
}