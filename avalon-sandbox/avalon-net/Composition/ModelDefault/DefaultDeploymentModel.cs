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
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Meta;
	
	/// <summary> Abstract model base class.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public abstract class DefaultDeploymentModel : IDeploymentModel
	{
		//--------------------------------------------------------------
		// static
		//--------------------------------------------------------------
		
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		private IDeploymentContext m_context;
		
		//--------------------------------------------------------------
		// muttable state
		//--------------------------------------------------------------
		
		// private Commissionable m_handler = null;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of an abstract model.  The model associated a 
		/// name and a partition.
		/// 
		/// </summary>
		/// <param name="context">the deployment context
		/// </param>
		public DefaultDeploymentModel(IDeploymentContext context)
		{
			if (null == context)
			{
				throw new System.ArgumentNullException("context");
			}
			m_context = context;
		}
		
		//--------------------------------------------------------------
		// Commssionable
		//--------------------------------------------------------------
		
		/// <summary> Commission the model. 
		/// 
		/// </summary>
		/// <exception cref=""> Exception if a commissioning error occurs
		/// </exception>
		public virtual void Commission()
		{
			m_context.SystemContext.Commission(this);
		}
		
		/// <summary> Decommission the model.  Once an model is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		public virtual void Decommission()
		{
			m_context.SystemContext.Decommission(this);
		}
		
		//--------------------------------------------------------------
		// Resolver
		//--------------------------------------------------------------
		
		/// <summary> Resolve a object to a value.
		/// 
		/// </summary>
		/// <returns> the resolved object
		/// @throws Exception if an error occurs
		/// </returns>
		public virtual System.Object Resolve()
		{
			return m_context.SystemContext.Resolve(this);
		}
		
		/// <summary> Release an object
		/// 
		/// </summary>
		/// <param name="instance">the object to be released
		/// </param>
		public virtual void Release(System.Object instance)
		{
			m_context.SystemContext.Release(this, instance);
		}
		
		//--------------------------------------------------------------
		// IDeploymentModel
		//--------------------------------------------------------------
		
		/// <summary> Return the profile name.</summary>
		/// <returns> the name
		/// </returns>
		public virtual String Name
		{
			get
			{
				return m_context.Name;
			}
		}
		
		/// <summary> Return the profile path.</summary>
		/// <returns> the path
		/// </returns>
		public virtual String Path
		{
			get
			{
				if (null == m_context.PartitionName)
				{
					return Apache.Avalon.Composition.Model.IDeploymentModel_Fields.SEPARATOR;
				}
				return m_context.PartitionName;
			}
		}
		
		/// <summary> Return the model fully qualified name.</summary>
		/// <returns> the fully qualified name
		/// </returns>
		public virtual String QualifiedName
		{
			get
			{
				return Path + Name;
			}
		}
		
		/// <summary> Return the mode of establishment.</summary>
		/// <returns> the mode
		/// </returns>
		public virtual Mode Mode
		{
			get
			{
				return m_context.Mode;
			}
		}
		
		/// <summary> Return the set of models consuming this model.</summary>
		/// <returns> the consumers
		/// </returns>
		public virtual IDeploymentModel[] ConsumerGraph
		{
			get
			{
				return m_context.DependencyGraph.GetConsumerGraph(this);
			}
		}
		
		/// <summary> Return the set of models supplying this model.</summary>
		/// <returns> the providers
		/// </returns>
		public virtual IDeploymentModel[] ProviderGraph
		{
			get
			{
				return m_context.DependencyGraph.GetProviderGraph(this);
			}
		}
		
		/// <summary> Return the assigned logging channel.</summary>
		/// <returns> the logging channel
		/// </returns>
		public virtual ILogger Logger
		{
			get
			{
				return m_context.Logger;
			}
		}

		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// depedendency.
		/// </summary>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		public abstract bool IsaCandidate(DependencyDescriptor dependency);
		
		/// <summary> Return TRUE is this model is capable of supporting a supplied 
		/// stage dependency.
		/// </summary>
		/// <returns> true if this model can fulfill the dependency
		/// </returns>
		public abstract bool IsaCandidate(StageDescriptor stage);
		
		//-----------------------------------------------------------
		// composite assembly
		//-----------------------------------------------------------
		
		/// <summary> Returns the assembled state of the model.</summary>
		/// <returns> true if this model is assembled
		/// </returns>
		public abstract bool IsAssembled
		{
			get;
		}
		
		/// <summary> Assemble the model.</summary>
		/// <param name="subjects">a list of deployment models that make up the assembly chain
		/// </param>
		/// <exception cref=""> Exception if an error occurs during model assembly
		/// </exception>
		public abstract void Assemble(System.Collections.IList subjects);

		/// <summary> Disassemble the model.</summary>
		public abstract void Disassemble();
		
		/// <summary> Return the set of models assigned as providers.</summary>
		/// <returns> the providers consumed by the model
		/// </returns>
		/// <exception cref=""> IllegalStateException if invoked prior to 
		/// the completion of the assembly phase 
		/// </exception>
		public abstract IDeploymentModel[] Providers
		{
			get;
		}

		/// <summary> Return the set of services produced by the model.</summary>
		/// <returns> the services
		/// </returns>
		public abstract ServiceDescriptor[] Services
		{
			get;
		}
		
		//--------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------
		
		public override String ToString()
		{
			return "[" + QualifiedName + "]";
		}
		
		public  override bool Equals(System.Object other)
		{
			bool equal = base.Equals(other);
			return equal;
		}

		public override int GetHashCode()
		{
			return base.GetHashCode ();
		}
 
		/// <summary> Return the default deployment timeout value declared in the 
		/// kernel configuration.  The implementation looks for a value
		/// assigned under the property key "urn:composition:deployment.timeout"
		/// and defaults to 1000 msec if undefined.
		/// 
		/// </summary>
		/// <returns> the default deployment timeout value
		/// </returns>
		public virtual long DeploymentTimeout
		{
			get
			{
				ISystemContext system = m_context.SystemContext;
				return system.DefaultDeploymentTimeout;
			}
		}
	}
}