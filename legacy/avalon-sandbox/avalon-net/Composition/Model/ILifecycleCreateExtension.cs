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
	using StageDescriptor = Apache.Avalon.Meta.StageDescriptor;
	
	public interface ILifecycleCreateExtension
	{
		/// <summary> Invocation of the deployment creation stage extension.</summary>
		/// <param name="model">the model representing the object under deployment
		/// </param>
		/// <param name="stage">the extension stage descriptor
		/// </param>
		/// <param name="object">the object under deployment
		/// </param>
		/// <exception cref=""> if a deployment error occurs
		/// </exception>
		void Create(IComponentModel model, StageDescriptor stage, System.Object obj);
	}
}