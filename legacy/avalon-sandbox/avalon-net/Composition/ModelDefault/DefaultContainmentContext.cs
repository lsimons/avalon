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
	
	/// <summary> Implementation of a containment supplied to a containment model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultContainmentContext : DefaultDeploymentContext, IContainmentContext
	{
		//---------------------------------------------------------
		// static
		//---------------------------------------------------------
		
		//---------------------------------------------------------
		// immutable state
		//---------------------------------------------------------
		
		private System.IO.FileInfo m_home;
		
		private System.IO.FileInfo m_temp;
		
		private ITypeLoaderModel m_classloader;
		
		private ContainmentProfile m_profile;
		
		private IModelRepository m_repository;
		
		private IContainmentModel m_parent;
		
		//---------------------------------------------------------
		// constructor
		//---------------------------------------------------------
		
		/// <summary> Creation of a new root containment context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign
		/// </param>
		/// <param name="system">the system context
		/// </param>
		/// <param name="model">the classloader model
		/// </param>
		/// <param name="repository">the parent model repository
		/// </param>
		/// <param name="graph">the parent dependency graph
		/// </param>
		/// <param name="profile">the containment profile
		/// </param>
		public DefaultContainmentContext(ILogger logger, ISystemContext system, ITypeLoaderModel model, 
			IModelRepository repository, DependencyGraph graph, ContainmentProfile profile):
			this(logger, system, model, repository, graph, system.HomeDirectory, system.TempDirectory,
			null, profile, null, "")
		{
		}
		
		/// <summary> Creation of a new containment context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign
		/// </param>
		/// <param name="system">the system context
		/// </param>
		/// <param name="model">the classloader model
		/// </param>
		/// <param name="repository">the parent model repository
		/// </param>
		/// <param name="graph">the parent dependency graph
		/// </param>
		/// <param name="home">the directory for the container
		/// </param>
		/// <param name="temp">a temporary directory for the container
		/// </param>
		/// <param name="profile">the containment profile
		/// </param>
		/// <param name="partition">the partition that this containment
		/// context is established within
		/// </param>
		/// <param name="name">the assigned containment context name
		/// </param>
		public DefaultContainmentContext(ILogger logger, ISystemContext system, ITypeLoaderModel model,
			IModelRepository repository, DependencyGraph graph, System.IO.FileInfo home,
			System.IO.FileInfo temp, IContainmentModel parent, ContainmentProfile profile, 
			String partition, String name):
			base(logger, system, partition, name, profile.Mode, graph)
		{
			if (system == null)
			{
				throw new System.ArgumentNullException("system");
			}
			if (model == null)
			{
				throw new System.ArgumentNullException("model");
			}
			if (home == null)
			{
				throw new System.ArgumentNullException("home");
			}
			if (temp == null)
			{
				throw new System.ArgumentNullException("temp");
			}
			if (profile == null)
			{
				throw new System.ArgumentNullException("profile");
			}
			
			bool tmpBool;
			if (System.IO.File.Exists(home.FullName))
				tmpBool = true;
			else
				tmpBool = System.IO.Directory.Exists(home.FullName);

			if (tmpBool && !System.IO.Directory.Exists(home.FullName))
			{
				String error = "containment.context.home.not-a-directory.error" + " " + home.ToString();
				throw new System.ArgumentException(error);
			}
			bool tmpBool2;
			if (System.IO.File.Exists(temp.FullName))
				tmpBool2 = true;
			else
				tmpBool2 = System.IO.Directory.Exists(temp.FullName);
			if (tmpBool2 && !System.IO.Directory.Exists(temp.FullName))
			{
				String error = "containment.context.temp.not-a-directory.error" + " " + temp.ToString();
				throw new System.ArgumentException(error);
			}
			
			m_repository = new DefaultModelRepository(repository, logger);
			
			m_classloader = model;
			m_home = home;
			m_temp = temp;
			m_parent = parent;
			m_profile = profile;
		}
		
		//---------------------------------------------------------
		// IContainmentContext
		//---------------------------------------------------------
		
		/// <summary> Return the working directory from which containers may 
		/// establish persistent content.
		/// 
		/// </summary>
		/// <returns> the working directory
		/// </returns>
		public virtual System.IO.FileInfo HomeDirectory
		{
			get
			{
				return m_home;
			}
		}
		
		/// <summary> Return the temporary directory from which a container 
		/// may use to establish a transient content directory. 
		/// 
		/// </summary>
		/// <returns> the temporary directory
		/// </returns>
		public virtual System.IO.FileInfo TempDirectory
		{
			get
			{
				return m_temp;
			}
		}
		
		/// <summary> Return the containment profile.
		/// 
		/// </summary>
		/// <returns> the containment profile
		/// </returns>
		public virtual ContainmentProfile ContainmentProfile
		{
			get
			{
				return m_profile;
			}
		}
		
		/// <summary> Return the model repository.
		/// 
		/// </summary>
		/// <returns> the model repository
		/// </returns>
		public virtual IModelRepository ModelRepository
		{
			get
			{
				return m_repository;
			}
		}
		
		/// <summary> Return the containment classloader model.
		/// 
		/// </summary>
		/// <returns> the classloader model
		/// </returns>
		public virtual ITypeLoaderModel TypeLoaderModel
		{
			get
			{
				return m_classloader;
			}
		}
		
		/// <summary> Return the containment classloader.  This method is a 
		/// convinience operation equivalent to 
		/// TypeLoaderModel.getClassLoader(); 
		/// 
		/// </summary>
		/// <returns> the classloader
		/// </returns>
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		// public virtual ClassLoader getClassLoader()
		// {
		// 	return m_classloader.getClassLoader();
		// }
		
		/// <summary> Return the parent containment model.
		/// 
		/// </summary>
		/// <returns> the model parent container
		/// </returns>
		public virtual IContainmentModel ParentContainmentModel
		{
			get
			{
				return m_parent;
			}
		}
	}
}