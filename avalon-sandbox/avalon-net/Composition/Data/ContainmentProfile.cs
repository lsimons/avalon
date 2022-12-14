// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Composition.Data
{
	using System;

	using Apache.Avalon.Meta;
	
	/// <summary> A containment profile describes a containment context including
	/// a classloader and the set of profiles explicitly included within
	/// the a container.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	public class ContainmentProfile : DeploymentProfile
	{

		//========================================================================
		// static
		//========================================================================
		
		/// <summary> Container path delimiter.</summary>
		public const System.String DELIMITER = "/";
		
		private static readonly ServiceDirective[] EMPTY_SERVICES;
		
		private static readonly DeploymentProfile[] EMPTY_PROFILES;
		
		private static readonly CategoriesDirective EMPTY_CATEGORIES = new CategoriesDirective();
		
		private static readonly TypeLoaderDirective EMPTY_TYPELOADER = new TypeLoaderDirective(new LibraryDirective(), new ClasspathDirective());
		
		//========================================================================
		// state
		//========================================================================
		
		/// <summary> The classloader directive.</summary>
		private TypeLoaderDirective m_typeloader;
		
		/// <summary> The published service directives.</summary>
		private ServiceDirective[] m_export;
		
		/// <summary> The profiles described within the scope of the containment profile.</summary>
		private DeploymentProfile[] m_profiles;
		
		/// <summary> The assigned logging categories.</summary>
		private CategoriesDirective m_categories;
		
		//--------------------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------------------
		
		/// <summary> Creation of a new empty containment profile.</summary>
		public ContainmentProfile():this("container", null, null, null, null)
		{
		}
		
		/// <summary> Creation of a new containment profile.
		/// 
		/// </summary>
		/// <param name="name">the profile name
		/// </param>
		/// <param name="classloader">the description of the classloader to be 
		/// created for this containment profile
		/// </param>
		/// <param name="exports">the set of servides that this component is 
		/// dependent on for normal execution
		/// </param>
		/// <param name="profiles">the set of profiles contained within this 
		/// containment profile
		/// </param>
		public ContainmentProfile(System.String name, TypeLoaderDirective typeloader, 
			ServiceDirective[] exports, CategoriesDirective categories, 
			DeploymentProfile[] profiles) : 
			base(name, ActivationPolicy.Startup, Mode.Explicit)
		{
			
			m_categories = categories;
			m_typeloader = typeloader;
			m_profiles = profiles;
			m_export = exports;
		}
		
		//--------------------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------------------
		
		/// <summary> Return the logging categories for the profile.
		/// 
		/// </summary>
		/// <returns> the categories
		/// </returns>
		public virtual CategoriesDirective Categories
		{
			get
			{
				if (m_categories == null)
					return EMPTY_CATEGORIES;
				return m_categories;
			}
			
		}
		/// <summary> Return the classloader directive that describes the creation
		/// arguments for the classloader required by this container.
		/// 
		/// </summary>
		/// <returns> the classloader directive
		/// </returns>
		public virtual TypeLoaderDirective TypeLoaderDirective
		{
			get
			{
				if (m_typeloader == null)
					return EMPTY_TYPELOADER;
				return m_typeloader;
			}
			
		}
		/// <summary> Return the set of service directives that describe the mapping 
		/// between services exposrted by the container and its implementation
		/// model.
		/// 
		/// </summary>
		/// <returns> the array of service directives
		/// </returns>
		public virtual ServiceDirective[] ExportDirectives
		{
			get
			{
				if (m_export == null)
					return EMPTY_SERVICES;
				return m_export;
			}
			
		}

		/// <summary> Retrieve a service directive matching a supplied class.
		/// 
		/// </summary>
		/// <param name="clazz">the class to match
		/// </param>
		/// <returns> the service directive or null if it does not exist
		/// </returns>
		public virtual ServiceDirective GetExportDirective(System.Type clazz)
		{
			System.String classname = clazz.FullName;
			ServiceDescriptor[] services = ExportDirectives;
			for (int i = 0; i < services.Length; i++)
			{
				ServiceDirective virtual_Renamed = (ServiceDirective) services[i];
				if (virtual_Renamed.Reference.Type.Equals(clazz))
				{
					return virtual_Renamed;
				}
			}
			return null;
		}
		
		/// <summary> Return the set of nested profiles wihin this containment profile.
		/// 
		/// </summary>
		/// <returns> the profiles nested in this containment profile
		/// </returns>
		public virtual DeploymentProfile[] Profiles
		{
			get
			{
				if (m_profiles == null)
					return EMPTY_PROFILES;
				return m_profiles;
			}
		}
		
		/// <summary> Return the set of nested profiles contained within this profile matching
		/// the supplied mode.
		/// 
		/// </summary>
		/// <param name="mode">one of enumerated value {@link Mode#IMPLICIT}, 
		/// {@link Mode#PACKAGED}, or {@link Mode#EXPLICIT}
		/// </param>
		/// <returns> the profiles matching the supplied creation mode
		/// </returns>
		public virtual DeploymentProfile[] GetProfiles(Mode mode)
		{
			DeploymentProfile[] profiles = Profiles;
			return selectProfileByMode(profiles, mode);
		}
		
		/// <summary> Returns a sub-set of the supplied containers matching the supplied creation mode.</summary>
		/// <param name="profiles">the profiles to select from
		/// </param>
		/// <param name="mode">the creation mode to retrict the returned selection to
		/// </param>
		/// <returns> the subset of the supplied profiles with a creation mode matching
		/// the supplied mode value
		/// </returns>
		private DeploymentProfile[] selectProfileByMode(DeploymentProfile[] profiles, Mode mode)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			for (int i = 0; i < profiles.Length; i++)
			{
				DeploymentProfile profile = profiles[i];
				if (profile.Mode.Equals(mode))
				{
					list.Add(profile);
				}
			}
			return (DeploymentProfile[]) list.ToArray( typeof(DeploymentProfile) );
		}

		static ContainmentProfile()
		{
			EMPTY_SERVICES = new ServiceDirective[0];
			EMPTY_PROFILES = new DeploymentProfile[0];
		}
	}
}