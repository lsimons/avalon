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
	
	/// <summary> The Comissionable interface defines the contract for an manager 
	/// of deployable components. 
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface ICommissionable
	{
		/// <summary> Commission the runtime handler. 
		/// 
		/// </summary>
		/// <exception cref=""> Exception if a hanfdler commissioning error occurs
		/// </exception>
		void Commission();
		
		/// <summary> Invokes the decommissioning phase.  Once a handler is 
		/// decommissioned it may be re-commissioned.
		/// </summary>
		void Decommission();
	}
}