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
	
	/// <summary> <p>Specification of a context enty model from which a 
	/// a context value can be established.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IEntryModel
	{
		/// <summary> Return the context entry key.
		/// 
		/// </summary>
		/// <returns> the key
		/// </returns>
		System.String Key
		{
			get;
		}
		
		/// <summary> Return the context entry value.
		/// 
		/// </summary>
		/// <returns> the context entry value
		/// </returns>
		System.Object Value
		{
			get;
		}
	}
}