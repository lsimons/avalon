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
	using DependencyDescriptor = Apache.Avalon.Meta.DependencyDescriptor;
	using ServiceDescriptor = Apache.Avalon.Meta.ServiceDescriptor;
	
	/// <summary> Dependency model handles the establishment of an explicit source 
	/// provider defintion or service provider selection rules.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IDependencyModel : IDependent
	{
		/// <summary> Return the dependency descriptor for the model.
		/// 
		/// </summary>
		/// <returns> the descriptors declaring the component 
		/// dependency
		/// </returns>
		DependencyDescriptor Dependency
		{
			get;
		}
		
		/// <summary> Return an explicit path to a component.  
		/// If a dependency directive has been declared
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
		/// <param name="candidates">the set of candidate services for the dependency
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		ServiceDescriptor[] Filter(ServiceDescriptor[] candidates);
	}
}