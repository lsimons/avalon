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
	using ExtensionDescriptor = Apache.Avalon.Meta.ExtensionDescriptor;
	
	/// <summary> Stage model handles the establishment of an explicit source 
	/// extension defintion or stage provider selection based on 
	/// extension qualification.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IStageModel : IDependent
	{
		/// <summary> Return the stage descriptor for the model.
		/// 
		/// </summary>
		/// <returns> the descriptor declaring the component 
		/// stage dependency
		/// </returns>
		StageDescriptor Stage
		{
			get;
		}
		
		/// <summary> Return an explicit path to a component.  
		/// If a stage directive has been declared
		/// and the directive contains a source declaration, the value 
		/// returned is the result of parsing the source value relative 
		/// to the absolute address of the dependent component.
		/// 
		/// </summary>
		/// <returns> the explicit path
		/// </returns>
		System.String Path
		{
			get;
		}
		
		/// <summary> Filter a set of candidate service descriptors and return the 
		/// set of acceptable service as a ordered sequence.
		/// 
		/// </summary>
		/// <param name="candidates">the set of candidate extension providers
		/// for the stage dependency
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		ExtensionDescriptor[] Filter(ExtensionDescriptor[] candidates);
	}
}