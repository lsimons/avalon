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
using System;
namespace Apache.Avalon.Composition.Model.Default
{
	
	/// <summary> The State class desribes a enabled versus disabled state.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	
	class DefaultState
	{
		/// <summary> Return the enabled state of the state.</summary>
		/// <returns> TRUE if the state has been enabled else FALSE
		/// </returns>
		/// <summary> Set the enabled state of the state.</summary>
		/// <param name="enabled">the enabled state to assign
		/// </param>
		public virtual bool Enabled
		{
			get
			{
				return m_enabled;
			}
			
			set
			{
				m_enabled = value;
			}
			
		}
		
		//---------------------------------------------------------------------------
		// state
		//---------------------------------------------------------------------------
		
		private bool m_enabled = false;
		
		//---------------------------------------------------------------------------
		// State
		//---------------------------------------------------------------------------
		
		//---------------------------------------------------------------------------
		// implementation
		//---------------------------------------------------------------------------
	}
}