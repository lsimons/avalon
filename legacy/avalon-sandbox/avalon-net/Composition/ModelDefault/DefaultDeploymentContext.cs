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
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Default implementation of a deployment context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultDeploymentContext : Apache.Avalon.Framework.DefaultContext, IDeploymentContext
	{
		//---------------------------------------------------------
		// immutable state
		//---------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_name '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private String m_name;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_partition '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private String m_partition;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_logger '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ILogger m_logger;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_mode '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private Mode m_mode;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_graph '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private DependencyGraph m_graph;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_system '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ISystemContext m_system;
		
		//---------------------------------------------------------
		// constructor
		//---------------------------------------------------------
		
		/// <summary> Creation of a new deployment context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign
		/// </param>
		/// <param name="partition">the assigned partition name
		/// </param>
		/// <param name="name">the profile name
		/// </param>
		/// <param name="mode">the deployment mode
		/// </param>
		/// <param name="graph">the parent deployment assembly graph
		/// </param>
		public DefaultDeploymentContext(ILogger logger, ISystemContext system, String partition, String name, Mode mode, DependencyGraph graph)
		{
			if (logger == null)
			{
				throw new System.ArgumentNullException("logger");
			}
			if ((System.Object) name == null)
			{
				throw new System.ArgumentNullException("name");
			}
			if (system == null)
			{
				throw new System.ArgumentNullException("system");
			}
			
			m_graph = new DependencyGraph(graph);
			if (graph != null)
			{
				graph.AddChild(m_graph);
			}
			
			m_logger = logger;
			m_system = system;
			m_partition = partition;
			m_name = name;
			m_mode = mode;
		}
		
		//---------------------------------------------------------
		// DeploymentContext
		//---------------------------------------------------------
		
		/// <summary> Return the system context.
		/// 
		/// </summary>
		/// <returns> the system context
		/// </returns>
		public virtual ISystemContext SystemContext
		{
			get
			{
				return m_system;
			}
		}
		
		/// <summary> Return the profile name.</summary>
		/// <returns> the name
		/// </returns>
		public virtual String Name
		{
			get
			{
				return m_name;
			}
		}
		
		/// <summary> Return the assigned partition name.
		/// 
		/// </summary>
		/// <returns> the partition name
		/// </returns>
		public virtual String PartitionName
		{
			get
			{
				return m_partition;
			}
		}
		
		/// <summary> Return the model fully qualified name.</summary>
		/// <returns> the fully qualified name
		/// </returns>
		public virtual String QualifiedName
		{
			get
			{
				if (null == PartitionName)
				{
					return IDeploymentContext_Fields.SEPARATOR;
				}
				else
				{
					return PartitionName + Name;
				}
			}
		}
		
		/// <summary> Return the mode of establishment.</summary>
		/// <returns> the mode
		/// </returns>
		public virtual Mode Mode
		{
			get
			{
				return m_mode;
			}
		}
		
		/// <summary> Return the assigned logger.</summary>
		/// <returns> the logging channel
		/// </returns>
		public virtual ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}
		
		/// <summary> Return the dependency graph used to construct 
		/// deployment and decommissioning sequences.
		/// 
		/// </summary>
		/// <returns> the dependency graph
		/// </returns>
		public virtual DependencyGraph DependencyGraph
		{
			get
			{
				return m_graph;
			}
		}
	}
}