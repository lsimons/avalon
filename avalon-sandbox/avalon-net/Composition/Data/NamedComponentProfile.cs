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
	
	/// <summary> A named deployment profile.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	public class NamedComponentProfile : Profile
	{
		/// <summary> The component classname.</summary>
		private System.String m_classname;
		
		/// <summary> The profile key.</summary>
		private System.String m_key;
		
		//--------------------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------------------
		
		public NamedComponentProfile(System.String name, System.String classname, System.String key, ActivationPolicy activation):base(name, activation, Mode.Explicit)
		{
			m_classname = classname;
			m_key = key;
		}
		
		//--------------------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------------------

		/// <summary> Return the component type classname.
		/// 
		/// </summary>
		/// <returns> classname of the component type
		/// </returns>
		public virtual System.String Classname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the component profile key.
		/// 
		/// </summary>
		/// <returns> the name of a profile pacikaged with the component type
		/// </returns>
		public virtual System.String Key
		{
			get
			{
				return m_key;
			}
			
		}
	
		/// <summary> Returns a string representation of the profile.</summary>
		/// <returns> a string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[" + Name + "-" + Key + "]";
		}
	}
}