// Copyright 2004 Apache Software Foundation
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
	
	/// <summary> Abstract base class for DeploymentProfile and ContainmentProfile.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	[Serializable]
	public abstract class Profile
	{
		//--------------------------------------------------------------------------
		// state
		//--------------------------------------------------------------------------
		
		/// <summary> The name of the component profile. This is an
		/// abstract name used during assembly.
		/// </summary>
		private System.String m_name;
		
		/// <summary> The activation policy.</summary>
		private ActivationPolicy m_activation;
		
		/// <summary> The mode under which this profile was established.</summary>
		private Mode m_mode;
		
		//--------------------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------------------
		
		public Profile(System.String name, ActivationPolicy activation, Mode mode)
		{
			m_activation = activation;
			m_mode = mode;
			
			if ((System.Object) name == null)
			{
				m_name = "untitled";
			}
			else
			{
				m_name = name;
			}
		}

		/// <summary> Return the name of meta-data instance.
		/// 
		/// </summary>
		/// <returns> the name of the component.
		/// </returns>
		public virtual System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		/// <summary> Get the activation policy for the profile.</summary>
		public virtual ActivationPolicy ActivationPolicy
		{
			get
			{
				return m_activation;
			}
			
		}
		/// <summary> Returns the creation mode for this profile.</summary>
		/// <returns> a value of EXPLICIT, PACKAGED or IMPLICIT
		/// </returns>
		public virtual Mode Mode
		{
			get
			{
				return m_mode;
			}
		}
		
		//--------------------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------------------
		
		/// <summary> Returns a string representation of the profile.</summary>
		/// <returns> a string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[" + Name + "]";
		}
	}
}