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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	
	
	/// <summary> The model repository interface declares operations through which 
	/// clients may resolve registered model instances relative to
	/// a stage or service dependencies.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultModelRepository : IModelRepository
	{
		private void  InitBlock()
		{
			m_models = new System.Collections.Hashtable();
		}

		//------------------------------------------------------------------
		// immutable state
		//------------------------------------------------------------------
		
		/// <summary> The parent appliance repository.</summary>
		private IModelRepository m_parent;
		
		private ILogger m_logger;
		
		/// <summary> Table of registered appliance instances keyed by name.</summary>
		private System.Collections.Hashtable m_models;
		
		//------------------------------------------------------------------
		// constructor
		//------------------------------------------------------------------
		
		public DefaultModelRepository(IModelRepository parent, ILogger logger)
		{
			InitBlock();
			m_parent = parent;
			m_logger = logger;
		}
		
		//------------------------------------------------------------------
		// IModelRepository
		//------------------------------------------------------------------

		/// <summary> Return a sequence of all of the local models.
		/// 
		/// </summary>
		/// <returns> the model sequence
		/// </returns>
		public virtual IDeploymentModel[] Models
		{
			get
			{
				return (IDeploymentModel[]) 
					new System.Collections.ArrayList( m_models.Values ).ToArray( typeof(IDeploymentModel) );
			}
		}

		/// <summary> Locate an model meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="dependency">a component service dependency
		/// </param>
		/// <returns> the model or null if no matching model is resolved
		/// </returns>
		public virtual IDeploymentModel GetModel(DependencyDescriptor dependency)
		{
			//
			// attempt to locate a solution locally
			//
			
			System.Collections.IEnumerator iterator = m_models.Values.GetEnumerator();
			while (iterator.MoveNext())
			{
				IDeploymentModel model = (IDeploymentModel) iterator.Current;
				if (model.IsaCandidate(dependency))
				{
					return model;
				}
			}
			
			//
			// attempt to locate a solution from the parent
			//
			
			if (m_parent != null)
			{
				return m_parent.GetModel(dependency);
			}
			
			return null;
		}
		
		/// <summary> Locate all models meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="stage">a component stage dependency
		/// </param>
		/// <returns> the candidate models
		/// </returns>
		public virtual IDeploymentModel[] GetCandidateProviders(StageDescriptor stage)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			System.Collections.IEnumerator iterator = m_models.Values.GetEnumerator();
			while (iterator.MoveNext())
			{
				IDeploymentModel model = (IDeploymentModel) iterator.Current;
				if (model.IsaCandidate(stage))
				{
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(model);
				}
			}
			
			if (m_parent != null)
			{
				IDeploymentModel[] models = m_parent.GetCandidateProviders(stage);
				for (int i = 0; i < models.Length; i++)
				{
					list.Add(models[i]);
				}
			}

			return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Locate all models meeting the supplied dependency criteria.
		/// 
		/// </summary>
		/// <param name="dependency">a component service dependency
		/// </param>
		/// <returns> the candidate models
		/// </returns>
		public virtual IDeploymentModel[] GetCandidateProviders(DependencyDescriptor dependency)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			System.Collections.IEnumerator iterator = m_models.Values.GetEnumerator();
			while (iterator.MoveNext())
			{
				IDeploymentModel model = (IDeploymentModel) iterator.Current;
				if (model.IsaCandidate(dependency))
				{
					list.Add(model);
				}
			}
			
			if (m_parent != null)
			{
				IDeploymentModel[] models = m_parent.GetCandidateProviders(dependency);
				for (int i = 0; i < models.Length; i++)
				{
					//UPGRADE_TODO: The equivalent in .NET for method 'java.util.ArrayList.add' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					list.Add(models[i]);
				}
			}
			return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Locate a model meeting the supplied criteria.
		/// 
		/// </summary>
		/// <param name="stage">a component stage dependency
		/// </param>
		/// <returns> the model
		/// </returns>
		public virtual IDeploymentModel GetModel(StageDescriptor stage)
		{
			System.Collections.IEnumerator iterator = m_models.Values.GetEnumerator();
			while (iterator.MoveNext())
			{
				IDeploymentModel model = (IDeploymentModel) iterator.Current;
				
				if (model.IsaCandidate(stage))
				{
					return model;
				}
			}
			
			if (m_parent != null)
			{
				return m_parent.GetModel(stage);
			}
			
			return null;
		}
		
		//------------------------------------------------------------------
		// implementation
		//------------------------------------------------------------------
		
		/// <summary> Add an model to the repository.
		/// 
		/// </summary>
		/// <param name="model">the model to add
		/// </param>
		public virtual void AddModel(IDeploymentModel model)
		{
			object tempObject;
			tempObject = model;
			m_models[model.Name] = tempObject;
			System.Object generatedAux = tempObject;
		}
		
		/// <summary> Add an model to the repository.
		/// 
		/// </summary>
		/// <param name="name">the name to register the model under
		/// </param>
		/// <param name="model">the model to add
		/// </param>
		public virtual void AddModel(String name, IDeploymentModel model)
		{
			object tempObject;
			tempObject = model;
			m_models[name] = tempObject;
			System.Object generatedAux = tempObject;
		}
		
		/// <summary> Remove an model from the repository.
		/// 
		/// </summary>
		/// <param name="model">the model to remove
		/// </param>
		public virtual void RemoveModel(IDeploymentModel model)
		{
			m_models.Remove(model.Name);
		}
		
		/// <summary> Locate a local model matching the supplied name.
		/// 
		/// </summary>
		/// <param name="name">the model name
		/// </param>
		/// <returns> the model or null if the model name is unknown
		/// </returns>
		public virtual IDeploymentModel GetModel(String name)
		{
			IDeploymentModel model = (IDeploymentModel) m_models[name];
			if (model == null && m_logger != null)
			{
				m_logger.Debug("Can't find '" + name + "' in model repository: " + m_models);
			}
			return model;
		}
	}
}