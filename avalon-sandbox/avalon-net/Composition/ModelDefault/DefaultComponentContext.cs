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
	using Apache.Avalon.Meta;
	
	/// <summary> Default implementation of a deployment context that is used
	/// as the primary constructor argument when creating a new component
	/// model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultComponentContext : DefaultDeploymentContext, IComponentContext
	{
		private void  InitBlock()
		{
			m_map = new System.Collections.Hashtable();
		}

		//==============================================================
		// static
		//==============================================================
		
		//==============================================================
		// immutable state
		//==============================================================
		
		private ComponentProfile m_profile;
		
		private TypeDescriptor m_type;
		
		private System.Type m_class;
		
		private System.IO.FileInfo m_home;
		
		private System.IO.FileInfo m_temp;
		
		private IContainmentModel m_model;
		
		/// <summary> Map containing context entry models 
		/// keyed by entry key.
		/// </summary>
		private System.Collections.IDictionary m_map;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new deployment context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign
		/// </param>
		/// <param name="name">the deployment context name
		/// </param>
		/// <param name="system">the system context
		/// </param>
		/// <param name="classloader">the containers classloader
		/// </param>
		/// <param name="graph">the containers dependency graph
		/// </param>
		/// <param name="profile">the deployment profile
		/// </param>
		/// <param name="type">the underlying component type
		/// </param>
		/// <param name="type">the compoent deployment class
		/// </param>
		/// <param name="home">the home working directory
		/// </param>
		/// <param name="temp">a temporary directory 
		/// </param>
		/// <param name="partition">the partition name 
		/// </param>
		public DefaultComponentContext(ILogger logger, String name, 
			ISystemContext system, DependencyGraph graph, IContainmentModel model, 
			ComponentProfile profile, TypeDescriptor typeDesc, System.Type type, 
			System.IO.FileInfo home, System.IO.FileInfo temp, String partition) : 
			base(logger, system, partition, name, profile.Mode, graph)
		{
			InitBlock();
			
			if ((System.Object) partition == null)
			{
				throw new System.ArgumentNullException("partition");
			}
			if (type == null)
			{
				throw new System.ArgumentNullException("type");
			}
			if (type == null)
			{
				throw new System.ArgumentNullException("type");
			}
			if (profile == null)
			{
				throw new System.ArgumentNullException("profile");
			}
			if (model == null)
			{
				throw new System.ArgumentNullException("model");
			}
			
			bool tmpBool;
			if (System.IO.File.Exists(home.FullName))
				tmpBool = true;
			else
				tmpBool = System.IO.Directory.Exists(home.FullName);
			if (tmpBool && !System.IO.Directory.Exists(home.FullName))
			{
				String error = "deployment.context.home.not-a-directory.error " + home.FullName;
				throw new System.ArgumentException(error);
			}
			bool tmpBool2;
			if (System.IO.File.Exists(temp.FullName))
				tmpBool2 = true;
			else
				tmpBool2 = System.IO.Directory.Exists(temp.FullName);
			
			if (tmpBool2 && !System.IO.Directory.Exists(temp.FullName))
			{
				String error = "deployment.context.temp.not-a-directory.error" + temp.FullName;
				throw new System.ArgumentException(error);
			}
			
			m_home = home;
			m_temp = temp;
			m_type = typeDesc;
			m_profile = profile;
			m_class = type;
			m_model = model;
		}
		
		//==============================================================
		// IContainmentContext
		//==============================================================
		
		/// <summary> Return the enclosing containment model.</summary>
		/// <returns> the containment model that component is within
		/// </returns>
		public virtual IContainmentModel ContainmentModel
		{
			get
			{
				return m_model;
			}
		}
		
		/// <summary> Return the working directory.
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
		
		/// <summary> Return the temporary directory.
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
		
		/// <summary> Return the deployment profile.
		/// 
		/// </summary>
		/// <returns> the profile
		/// </returns>
		public virtual ComponentProfile Profile
		{
			get
			{
				return m_profile;
			}
		}
		
		/// <summary> Return the component type.
		/// 
		/// </summary>
		/// <returns> the type defintion
		/// </returns>
		public virtual TypeDescriptor Type
		{
			get
			{
				return m_type;
			}
		}
		
		/// <summary> Return the component class.
		/// 
		/// </summary>
		/// <returns> the class
		/// </returns>
		public virtual System.Type DeploymentType
		{
			get
			{
				return m_class;
			}
		}
		
		/// <summary> Return the classloader for the component.
		/// 
		/// </summary>
		/// <returns> the classloader
		/// </returns>
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		// public virtual ClassLoader getClassLoader()
		// {
		// 	return m_classloader;
		// }
		
		/// <summary> Add a context entry model to the deployment context.</summary>
		/// <param name="model">the entry model
		/// </param>
		/// <exception cref=""> IllegalArgumentException if model key is unknown
		/// </exception>
		public virtual void Register(IEntryModel model)
		{
			String key = model.Key;
			if (m_map[key] == null)
			{
				object tempObject;
				tempObject = model;
				m_map[key] = tempObject;
				System.Object generatedAux = tempObject;
			}
			else
			{
				String error = "deployment.registration.override.error " + key;
				throw new System.ArgumentException(error);
			}
		}
		
		/// <summary> Get a context entry from the deployment context.</summary>
		/// <param name="alias">the entry lookup key
		/// </param>
		/// <returns> value the corresponding value
		/// </returns>
		/// <exception cref=""> ContextException if the key is unknown
		/// </exception>
		/// <exception cref=""> ModelRuntimeException if the key is unknown
		/// </exception>
		public virtual System.Object Resolve(String alias)
		{
			if ((System.Object) alias == null)
				throw new System.ArgumentNullException("alias");
			
			String key = alias;
			EntryDescriptor entry = Type.Context.GetEntry(alias);
			
			if (entry != null)
			{
				key = entry.Key;
			}
			
			if (key.Equals(Apache.Avalon.Composition.Model.IContainmentModel_Fields.KEY))
			{
				return ContainmentModel;
			}
			else if (key.StartsWith("urn:composition:"))
			{
				return SystemContext[key];
			}
			else if (key.Equals(Apache.Avalon.Composition.Model.IComponentContext_Fields.NAME_KEY))
			{
				return Name;
			}
			else if (key.Equals(Apache.Avalon.Composition.Model.IComponentContext_Fields.PARTITION_KEY))
			{
				return PartitionName;
			}
			else if (key.Equals(Apache.Avalon.Composition.Model.IComponentContext_Fields.HOME_KEY))
			{
				return HomeDirectory;
			}
			else if (key.Equals(Apache.Avalon.Composition.Model.IComponentContext_Fields.TEMP_KEY))
			{
				return TempDirectory;
			}
			else
			{
				System.Object object_Renamed = m_map[key];
				if (null != object_Renamed)
				{
					String typename = object_Renamed.GetType().FullName;
					try
					{
						return ((IEntryModel) object_Renamed).Value;
					}
					catch (System.Exception e)
					{
						String error = "deployment.context.runtime-get " + key + " " + typename;
						throw new ModelRuntimeException(error, e);
					}
				}
				else
				{
					String error = "deployment.context.runtime-get " + key;
					throw new ModelRuntimeException(error);
				}
			}
		}
	}
}