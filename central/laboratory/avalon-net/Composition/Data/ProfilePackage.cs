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
	
	/// <summary> A collection of profiles packaged with a component type.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	[Serializable]
	public class ProfilePackage
	{

		//--------------------------------------------------------------------------
		// static
		//--------------------------------------------------------------------------
		
		public static readonly ProfilePackage EMPTY_PACKAGE = new ProfilePackage();
		
		//--------------------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------------------
		
		/// <summary> The set of deployment profiles contained within the package.</summary>
		private ComponentProfile[] m_profiles;
		
		//--------------------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------------------
		
		public ProfilePackage():this(new ComponentProfile[0])
		{
		}
		
		/// <summary> Create a new profile package instance.
		/// 
		/// </summary>
		/// <param name="profiles">the set of contained profiles
		/// </param>
		public ProfilePackage(ComponentProfile[] profiles)
		{
			m_profiles = profiles;
		}
		
		//--------------------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------------------

		/// <summary> Return the set of profile.
		/// 
		/// </summary>
		/// <returns> the profiles
		/// </returns>
		public virtual ComponentProfile[] ComponentProfiles
		{
			get
			{
				return m_profiles;
			}
			
		}
	}
}