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

	using Apache.Avalon.Framework;
	
	/// <summary> Definition of an extension handler that handles the Contextualize
	/// stage of a component lifecycle.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	public interface IContextualizationHandler
	{
		/// <summary> Handle the contextualization stage of a component lifecycle.
		/// 
		/// </summary>
		/// <param name="context">the context to apply
		/// </param>
		/// <param name="object">the object to contextualize
		/// </param>
		/// <exception cref=""> ContextException if a contextualization error occurs
		/// </exception>
		void Contextualize(System.Object object_Renamed, IContext context);
	}
}