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
	
	/// <summary> <p>Utility class to aquire an ordered graph of
	/// consumers and providers models.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public class DependencyGraph
	{
		private void  InitBlock()
		{
			m_models = new System.Collections.ArrayList();
			m_children = new System.Collections.ArrayList();
		}

		/// <summary> Get the serilized graph of {@link IDeploymentModel} objects
		/// required when starting up the target. This makes sure
		/// that all providers are established before their coresponding
		/// consumers in the graph.
		/// 
		/// </summary>
		/// <returns> the ordered list of models
		/// </returns>
		public virtual IDeploymentModel[] StartupGraph
		{
			get
			{
				try
				{
					return WalkGraph(true);
				}
				catch (System.Exception e)
				{
					System.String error = "Unexpect error while resolving startup graph.";
					throw new ModelRuntimeException(error, e);
				}
			}
			
		}
		/// <summary> Get the serilized graph of {@link IDeploymentModel} instances
		/// required when shutting down all the components. This makes
		/// sure that all consumer shutdown actions occur before their
		/// coresponding providers in graph.
		/// 
		/// </summary>
		/// <returns> the ordered list of model instances
		/// </returns>
		public virtual IDeploymentModel[] ShutdownGraph
		{
			get
			{
				try
				{
					return WalkGraph(false);
				}
				catch (System.Exception e)
				{
					System.String error = "Unexpect error while resolving shutdown graph.";
					throw new ModelRuntimeException(error, e);
				}
			}
			
		}
		/// <summary> Parent Map. Components in the parent
		/// Map are potential Providers for services
		/// if no model in the current graph satisfies
		/// a dependency.
		/// </summary>
		private DependencyGraph m_parent;
		
		/// <summary> The set of models declared by the container as available.
		/// Used when searching for providers/consumers.
		/// </summary>
		private System.Collections.ArrayList m_models;
		
		/// <summary> The child {@link DependencyGraph} objects.
		/// Possible consumers of services in this assembly.
		/// </summary>
		private System.Collections.ArrayList m_children;
		
		/// <summary> Creation of a new empty dependency graph.</summary>
		public DependencyGraph():this(null)
		{
		}
		
		/// <summary> Creation of a new dependecy graph holding a reference to a parent
		/// graph.  IDeploymentModel instances in the parent graph are potential providers
		/// for services if no model in current assembly satisfies a dependency.
		/// 
		/// </summary>
		/// <param name="parent">the parent graph
		/// </param>
		public DependencyGraph(DependencyGraph parent)
		{
			InitBlock();
			m_parent = parent;
		}
		
		/// <summary> Addition of a consumer dependency graph.
		/// 
		/// </summary>
		/// <param name="child">the child map
		/// </param>
		public virtual void AddChild(DependencyGraph child)
		{
			m_children.Add(child);
		}
		
		/// <summary> Removal of a consumer dependency graph.
		/// 
		/// </summary>
		/// <param name="child">the child map
		/// </param>
		public virtual void RemoveChild(DependencyGraph child)
		{
			m_children.Remove(child);
		}
		
		/// <summary> Add a model to current dependency graph.
		/// 
		/// </summary>
		/// <param name="model">the model to add to the graph
		/// </param>
		public virtual void Add(IDeploymentModel model)
		{
			if (!m_models.Contains(model))
			{
				m_models.Add(model);
			}
		}
		
		/// <summary> Remove a model from the dependency graph.
		/// 
		/// </summary>
		/// <param name="model">the model to remove
		/// </param>
		public virtual void Remove(IDeploymentModel model)
		{
			m_models.Remove(model);
		}
		
		/// <summary> Get the serilized graph of {@link IDeploymentModel} instances
		/// that use services of the specified model.
		/// 
		/// </summary>
		/// <param name="model">the model
		/// </param>
		/// <returns> the ordered list of consumer model instances
		/// </returns>
		public virtual IDeploymentModel[] GetConsumerGraph(IDeploymentModel model)
		{
			if (m_parent != null)
				return m_parent.GetConsumerGraph(model);
			
			try
			{
				IDeploymentModel[] graph = GetComponentGraph(model, false);
				return ReferencedModels(model, graph);
			}
			catch (System.Exception e)
			{
				System.String error = "Unexpect error while resolving consumer graph for model: " + model;
				throw new ModelRuntimeException(error, e);
			}
		}
		
		/// <summary> Get the serilized graph of {@link IDeploymentModel} istances
		/// that provide specified model with services.
		/// 
		/// </summary>
		/// <param name="model">the model
		/// </param>
		/// <returns> the ordered list of providers
		/// </returns>
		public virtual IDeploymentModel[] GetProviderGraph(IDeploymentModel model)
		{
			try
			{
				return ReferencedModels(model, GetComponentGraph(model, true));
			}
			catch (System.Exception e)
			{
				System.String error = "Unexpect error while resolving provider graph for: " + model;
				throw new ModelRuntimeException(error, e);
			}
		}
		
		/// <summary> Return an model array that does not include the provided model.</summary>
		private IDeploymentModel[] ReferencedModels(IDeploymentModel model, IDeploymentModel[] models)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < models.Length; i++)
			{
				if (!models[i].Equals(model))
				{
					list.Add(models[i]);
				}
			}
			
			return (IDeploymentModel[]) list.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Get the graph of a single model.
		/// 
		/// </summary>
		/// <param name="model">the target model
		/// </param>
		/// <param name="providers">true if traversing providers, false if consumers
		/// </param>
		/// <returns> the list of models 
		/// </returns>
		private IDeploymentModel[] GetComponentGraph(IDeploymentModel model, bool providers)
		{
			System.Collections.ArrayList result = new System.Collections.ArrayList();
			Visitcomponent(model, providers, new System.Collections.ArrayList(), result);
			
			return (IDeploymentModel[]) result.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Method to generate an ordering of nodes to traverse.
		/// It is expected that the specified components have passed
		/// verification tests and are well formed.
		/// 
		/// </summary>
		/// <param name="direction">true if forward dependencys traced, false if 
		/// dependencies reversed
		/// </param>
		/// <returns> the ordered model list
		/// </returns>
		private IDeploymentModel[] WalkGraph(bool direction)
		{
			System.Collections.ArrayList result = new System.Collections.ArrayList();
			System.Collections.ArrayList done = new System.Collections.ArrayList();
			
			int size = m_models.Count;
			for (int i = 0; i < size; i++)
			{
				IDeploymentModel model = (IDeploymentModel) m_models[i];
				Visitcomponent(model, direction, done, result);
			}
			
			return (IDeploymentModel[]) result.ToArray( typeof(IDeploymentModel) );
		}
		
		/// <summary> Visit a model when traversing dependencies.
		/// 
		/// </summary>
		/// <param name="model">the model
		/// </param>
		/// <param name="direction">true if walking tree looking for providers, else false
		/// </param>
		/// <param name="done">those nodes already traversed
		/// </param>
		/// <param name="order">the order in which nodes have already been
		/// traversed
		/// </param>
		private void  Visitcomponent(IDeploymentModel model, bool direction, System.Collections.ArrayList done, System.Collections.ArrayList order)
		{
			//If already visited this model return
			
			if (done.Contains(model))
				return ;
			
			done.Add(model);
			
			if (direction)
			{
				VisitProviders(model, done, order);
			}
			else
			{
				VisitConsumers(model, done, order);
			}
			
			order.Add(model);
		}
		
		/// <summary> Traverse graph of components that provide services to
		/// the specified model.
		/// </summary>
		/// <param name="model">the model
		/// </param>
		private void VisitProviders(IDeploymentModel model, System.Collections.ArrayList done, System.Collections.ArrayList order)
		{
			IDeploymentModel[] providers = model.Providers;
			for (int i = (providers.Length - 1); i > - 1; i--)
			{
				Visitcomponent(providers[i], true, done, order);
			}
		}
		
		/// <summary> Traverse all consumers of a model. I.e. all models that use
		/// service provided by the supplied model.
		/// </summary>
		/// <param name="model">the IDeploymentModel
		/// </param>
		private void VisitConsumers(IDeploymentModel model, System.Collections.ArrayList done, System.Collections.ArrayList order)
		{
			int size = m_models.Count;
			for (int i = 0; i < size; i++)
			{
				IDeploymentModel other = (IDeploymentModel) m_models[i];
				IDeploymentModel[] providers = other.Providers;
				for (int j = 0; j < providers.Length; j++)
				{
					IDeploymentModel provider = providers[j];
					if (provider.Equals(model))
					{
						Visitcomponent(other, false, done, order);
					}
				}
			}
			int childCount = m_children.Count;
			for (int i = 0; i < childCount; i++)
			{
				DependencyGraph map = (DependencyGraph) m_children[i];
				map.VisitConsumers(model, done, order);
			}
		}
	}
}