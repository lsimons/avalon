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
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> A type manager implemetation provides support for the creation,
	/// storage and retrival of component types.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	class DefaultTypeRepository : ITypeRepository
	{
		private void InitBlock()
		{
			m_types = new System.Collections.Hashtable();
			m_profiles = new System.Collections.Hashtable();
		}

		//==============================================================
		// static
		//==============================================================
		
		/// <summary> The profile builder.</summary>
		// private static readonly ProfilePackageBuilder PACKAGE_BUILDER = new ProfilePackageBuilder();
		
		//==============================================================
		// immutable state
		//==============================================================
		
		private ILogger m_logger;
		
		// private ClassLoader m_classloader;
		
		/// <summary> The parent type manager (may be null)</summary>
		private ITypeRepository m_parent;
		
		/// <summary> Table of component types keyed by implementation typename.</summary>
		private System.Collections.Hashtable m_types;
		
		/// <summary> Table of packaged profiles keyed by implementation typename.</summary>
		private System.Collections.Hashtable m_profiles;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new root type manager.</summary>
		/// <param name="types">the list of types local to the repository
		/// </param>
		/// <exception cref=""> NullPointerException if the type list is null
		/// </exception>
		public DefaultTypeRepository(ILogger logger, System.Collections.IList types):this(logger, null, types)
		{
		}
		
		/// <summary> Creation of a new type manager.</summary>
		/// <param name="logger">the assigned logging channel
		/// </param>
		/// <param name="classloader">the classloader
		/// </param>
		/// <param name="parent">the parent type repository
		/// </param>
		/// <param name="types">the list of types local to the repository
		/// </param>
		/// <exception cref=""> NullPointerException if the type list is null
		/// </exception>
		public DefaultTypeRepository(ILogger logger, ITypeRepository parent, System.Collections.IList types)
		{
			InitBlock();

			if (types == null)
			{
				throw new System.ArgumentNullException("types");
			}
			
			m_parent = parent;
			m_logger = logger;
			
			//
			// build up a map of types in the repository keyed by typename
			//
			
			if (Logger.IsDebugEnabled)
			{
				String message = "type.repository.count" + " " + types.Count;
				Logger.Debug(message);
			}
			
			System.Collections.IEnumerator iterator = types.GetEnumerator();
			while (iterator.MoveNext())
			{
				TypeDescriptor type = (TypeDescriptor) iterator.Current;
				String name = type.Info.Name;
				String typename = type.Info.Type.FullName;
				// System.Type type = m_classloader.loadClass(typename);
				
				// ProfilePackage pack = PACKAGE_BUILDER.createProfilePackage(name, type);
				// SupportClass.PutElement(m_profiles, typename, pack);
				// int n = pack.ComponentProfiles.Length;
				
				m_types.Add( typename, type );

				if (Logger.IsDebugEnabled)
				{
					String message = "type.repository.addition" + " " + typename;
					Logger.Debug(message);
				}
			}
		}
		
		//==============================================================
		// DefaultTypeRepository
		//==============================================================
		
		/// <summary> Return the logging channel assigned to the component.</summary>
		/// <returns> the assigned logging channel
		/// </returns>
		protected internal virtual ILogger Logger
		{
			get
			{
				return m_logger;
			}
			
		}

		/// <summary> Return all available types.</summary>
		/// <returns> the array of types
		/// </returns>
		public virtual TypeDescriptor[] Types
		{
			get
			{
				return GetTypes(true);
			}
		}
		
		/// <summary> Return all the types available within the repository.
		/// 
		/// </summary>
		/// <param name="policy">if TRUE, return all available types, if FALSE
		/// return only the locally established types.
		/// </param>
		/// <returns> the array of types
		/// </returns>
		public virtual TypeDescriptor[] GetTypes(bool policy)
		{
			if (policy && (m_parent != null))
			{
				System.Collections.ArrayList list = new System.Collections.ArrayList(m_types.Values);
				TypeDescriptor[] types = m_parent.GetTypes(policy);
				for (int i = 0; i < types.Length; i++)
				{
					list.Add(types[i]);
				}
				return (TypeDescriptor[]) list.ToArray( typeof(TypeDescriptor) );
			}
			else
			{
				return (TypeDescriptor[]) 
					new System.Collections.ArrayList( m_types.Values ).ToArray ( typeof(TypeDescriptor) );
			}
		}
		
		/// <summary> Locate a {@link Type} instances associated with the
		/// supplied implementation typename.
		/// 
		/// </summary>
		/// <param name="type">the component type implementation class.
		/// </param>
		/// <returns> the type matching the supplied implementation typename.
		/// </returns>
		/// <exception cref=""> TypeUnknownException if a matching type cannot be found
		/// </exception>
		public virtual TypeDescriptor GetType(System.Type type)
		{
			if (type == null)
			{
				throw new System.ArgumentNullException("type");
			}
			return GetType(type.FullName);
		}
		
		/// <summary> Locate a {@link Type} instances associated with the
		/// supplied implementation typename.
		/// 
		/// </summary>
		/// <param name="typename">the component type implementation class name.
		/// </param>
		/// <returns> the type matching the supplied implementation typename.
		/// </returns>
		/// <exception cref=""> TypeUnknownException if a matching type cannot be found
		/// </exception>
		public virtual TypeDescriptor GetType(String typename)
		{
			if ((System.Object) typename == null)
			{
				throw new System.ArgumentNullException("typename");
			}
			
			if (m_parent != null)
			{
				try
				{
					return m_parent.GetType(typename);
				}
				catch (TypeUnknownException )
				{
					// continue
				}
			}
			
			TypeDescriptor type = (TypeDescriptor) m_types[typename];
			if (type == null)
			{
				String error = "type.repository.unknown-type" + " " + typename;
				throw new TypeUnknownException(error);
			}
			
			return type;
		}
		
		/// <summary> Locate the set of component types in the local repository 
		/// capable of servicing the supplied dependency.
		/// 
		/// </summary>
		/// <param name="dependency">a service dependency descriptor
		/// </param>
		/// <returns> a set of types capable of servicing the supplied dependency
		/// </returns>
		public virtual TypeDescriptor[] GetTypes(DependencyDescriptor dependency)
		{
			return GetTypes(dependency, true);
		}
		
		/// <summary> Locate the set of component types in the local repository 
		/// capable of servicing the supplied dependency.
		/// 
		/// </summary>
		/// <param name="dependency">a service dependency descriptor
		/// </param>
		/// <returns> a set of types capable of servicing the supplied dependency
		/// </returns>
		public virtual TypeDescriptor[] GetTypes(DependencyDescriptor dependency, bool search)
		{
			if (dependency == null)
			{
				throw new System.ArgumentNullException("dependency");
			}
			
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			ReferenceDescriptor reference = dependency.Service;
			TypeDescriptor[] types = GetTypes(false);
			for (int i = 0; i < types.Length; i++)
			{
				TypeDescriptor type = types[i];
				if (type.GetService(reference) != null)
				{
					list.Add(type);
				}
			}
			
			if (search && m_parent != null)
			{
				TypeDescriptor[] suppliment = m_parent.GetTypes(dependency);
				for (int i = 0; i < suppliment.Length; i++)
				{
					list.Add(suppliment[i]);
				}
			}
			
			return (TypeDescriptor[]) list.ToArray( typeof(TypeDescriptor) );
		}
		
		/// <summary> Locate the set of local component types that provide the 
		/// supplied extension.
		/// </summary>
		/// <param name="stage">a stage descriptor
		/// </param>
		/// <returns> a set of types that support the supplied stage
		/// </returns>
		public virtual TypeDescriptor[] GetTypes(StageDescriptor stage)
		{
			if (stage == null)
			{
				throw new System.ArgumentNullException("stage");
			}
			
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			TypeDescriptor[] types = GetTypes(false);
			for (int i = 0; i < types.Length; i++)
			{
				TypeDescriptor type = types[i];
				if (type.GetExtension(stage) != null)
				{
					list.Add(type);
				}
			}
			
			if (m_parent != null)
			{
				TypeDescriptor[] suppliment = m_parent.GetTypes(stage);
				for (int i = 0; i < suppliment.Length; i++)
				{
					list.Add(suppliment[i]);
				}
			}
			
			return (TypeDescriptor[]) list.ToArray( typeof(TypeDescriptor) );
		}
		
		/// <summary> Return the set of deployment profiles for the supplied type. An 
		/// implementation is required to return a array of types > 0 in length
		/// or throw a TypeUnknownException.
		/// </summary>
		/// <param name="type">the type
		/// </param>
		/// <returns> a profile array containing at least one deployment profile
		/// </returns>
		/// <exception cref=""> TypeUnknownException if the supplied type is unknown
		/// </exception>
		public virtual ComponentProfile[] GetProfiles(TypeDescriptor type)
		{
			return GetProfiles(type, true);
		}
		
		/// <summary> Return the set of deployment profiles for the supplied type. An 
		/// implementation is required to return a array of types > 0 in length
		/// or throw a TypeUnknownException.
		/// </summary>
		/// <param name="type">the type
		/// </param>
		/// <param name="search">if the local search failes and search is TRUE then 
		/// delegate to a parent repository if available
		/// </param>
		/// <returns> a profile array containing at least one deployment profile
		/// </returns>
		/// <exception cref=""> TypeUnknownException if the supplied type is unknown
		/// </exception>
		private ComponentProfile[] GetProfiles(TypeDescriptor type, bool search)
		{
			Type typeTarget = type.Info.Type;
			ProfilePackage profiles = (ProfilePackage) m_profiles[typeTarget];
			if (profiles != null)
			{
				return profiles.ComponentProfiles;
			}
			else
			{
				if (search && m_parent != null)
				{
					return m_parent.GetProfiles(type);
				}
				else
				{
					String error = "type.repository.unknown-type" + " " + typeTarget.FullName;
					throw new TypeUnknownException(error);
				}
			}
		}
		
		/// <summary> Return a deployment profile for the supplied type and key.</summary>
		/// <param name="type">the type
		/// </param>
		/// <param name="key">the profile name
		/// </param>
		/// <returns> a profile matching the supplied key
		/// </returns>
		/// <exception cref=""> TypeUnknownException if the supplied type is unknown
		/// </exception>
		/// <exception cref=""> ProfileUnknownException if the supplied key is unknown
		/// </exception>
		public virtual ComponentProfile GetProfile(TypeDescriptor type, String key)
		{
			ComponentProfile[] profiles = GetProfiles(type);
			for (int i = 0; i < profiles.Length; i++)
			{
				ComponentProfile profile = profiles[i];
				String name = GetProfileName(type, key);
				if (profile.Name.Equals(name))
					return profile;
			}
			throw new ProfileUnknownException(key);
		}
		
		/// <summary> Attempt to locate a packaged deployment profile meeting the 
		/// supplied dependency description.
		/// 
		/// </summary>
		/// <param name="dependency">the dependency description 
		/// </param>
		/// <param name="search">the search policy  
		/// </param>
		/// <returns> the deployment profile (possibly null) 
		/// </returns>
		public virtual DeploymentProfile GetProfile(DependencyDescriptor dependency, bool search)
		{
			DeploymentProfile[] profiles = GetProfiles(dependency, search);
			IProfileSelector profileSelector = new DefaultProfileSelector();
			return profileSelector.Select(profiles, dependency);
		}
		
		/// <summary> Return a set of local deployment profile for the supplied dependency.</summary>
		/// <param name="dependency">the dependency descriptor
		/// </param>
		/// <returns> a set of profiles matching the supplied dependency
		/// </returns>
		public virtual DeploymentProfile[] GetProfiles(DependencyDescriptor dependency, bool search)
		{
			TypeDescriptor[] types = GetTypes(dependency, search);
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < types.Length; i++)
			{
				TypeDescriptor type = types[i];
				try
				{
					DeploymentProfile[] profiles = GetProfiles(type, false);
					for (int j = 0; j < profiles.Length; j++)
					{
						list.Add(profiles[j]);
					}
				}
				catch (TypeUnknownException e)
				{
					String error = "Unexpected condition: " + e.ToString();
					throw new System.SystemException(error);
				}
			}
			return (DeploymentProfile[]) list.ToArray( typeof(DeploymentProfile) );
		}
		
		/// <summary> Return the set of local profiles.</summary>
		/// <returns> a profile or null if a profile connot be resolve
		/// </returns>
		private DeploymentProfile[] GetProfiles()
		{
			return (DeploymentProfile[]) 
				new System.Collections.ArrayList( m_profiles.Values).ToArray( typeof(DeploymentProfile) );
		}
		
		/// <summary> Return the name of a packaged profile given the type and the 
		/// packaged profile key.  The key corresponds to the name attribute
		/// declared under the profile definition.
		/// 
		/// </summary>
		/// <param name="type">the component type
		/// </param>
		/// <param name="key">the profile name
		/// </param>
		/// <returns> the composite name
		/// </returns>
		private String GetProfileName(TypeDescriptor type, String key)
		{
			return type.Info.Name + "-" + key;
		}
	}
}