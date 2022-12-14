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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;
	
	/// <summary> Deployment context that is supplied to a deployment model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public struct IDeploymentContext_Fields
	{
		public readonly static System.String SEPARATOR = "/";
	}
	
	public interface IDeploymentContext : IContext
	{
		/// <summary> Return the deployment target name.</summary>
		/// <returns> the name
		/// </returns>
		System.String Name		
		{
			get;
		}
		
		/// <summary> Return the deployment poartition.</summary>
		/// <returns> the partition
		/// </returns>
		System.String PartitionName		
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
		
		/// <summary> Return the mode of establishment.</summary>
		/// <returns> the mode
		/// </returns>
		Mode Mode
		{
			get;
		}
		
		/// <summary> Return the assigned logger.</summary>
		/// <returns> the logging channel
		/// </returns>
		ILogger Logger		
		{
			get;
		}
		
		/// <summary> Return the system context.
		/// 
		/// </summary>
		/// <returns> the system context
		/// </returns>
		ISystemContext SystemContext		
		{
			get;
		}
		
		/// <summary> Return the dependency graph used to construct 
		/// deployment and decommissioning sequences.
		/// 
		/// </summary>
		/// <returns> the dependency graph
		/// </returns>
		DependencyGraph DependencyGraph		
		{
			get;
		}
	}
}