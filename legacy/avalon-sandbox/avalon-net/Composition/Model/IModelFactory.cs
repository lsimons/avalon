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
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;

	
	/// <summary> A factory enabling the establishment of new containment model instances.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IModelFactory
	{
		/// <summary> Creation of a new root containment model.
		/// 
		/// </summary>
		/// <param name="url">a url of a containment profile 
		/// </param>
		/// <returns> the containment model
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		IContainmentModel CreateRootContainmentModel(System.Uri url);
		
		/// <summary> Creation of a new root containment model.
		/// 
		/// </summary>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the containment model
		/// </returns>
		/// <exception cref=""> ModelException if an error occurs during model establishment
		/// </exception>
		IContainmentModel CreateRootContainmentModel(ContainmentProfile profile);
		
		/// <summary> Creation of a new nested component model using a supplied component
		/// context.
		/// 
		/// </summary>
		/// <param name="context">a potentially foreign component context
		/// </param>
		/// <returns> the compoent model
		/// </returns>
		IComponentModel CreateComponentModel(IComponentContext context);
		
		
		/// <summary> Creation of a new nested containment model using a supplied 
		/// containment context.
		/// 
		/// </summary>
		/// <param name="context">a potentially foreign containment context
		/// </param>
		/// <returns> the containment model
		/// </returns>
		IContainmentModel CreateContainmentModel(IContainmentContext context);
	}
}