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

	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> A type manager implemetation provides support for the creation,
	/// storage and retrival of component types.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface ITypeRepository
	{
		/// <summary> Return all availble types.</summary>
		/// <returns> the array of types
		/// </returns>
		TypeDescriptor[] Types
		{
			get;
		}
		
		/// <summary> Return all the types available within the repository.</summary>
		/// <param name="policy">if TRUE, return all available types, if FALSE
		/// return only the locally established types.
		/// </param>
		/// <returns> the array of types
		/// </returns>
		TypeDescriptor[] GetTypes(bool policy);
		
		/// <summary> Locate a {@link Type} instances associated with the
		/// supplied implementation classname.
		/// </summary>
		/// <param name="clazz">the component type implementation class.
		/// </param>
		/// <returns> the type matching the supplied implementation classname.
		/// </returns>
		/// <exception cref=""> UnknownTypeException if a matching type cannot be found
		/// </exception>
		TypeDescriptor GetType(System.Type clazz);
		
		/// <summary> Locate a {@link Type} instances associated with the
		/// supplied implementation classname.
		/// </summary>
		/// <param name="classname">the component type implementation class name.
		/// </param>
		/// <returns> the type matching the supplied implementation classname.
		/// </returns>
		/// <exception cref=""> UnknownTypeException if a matching type cannot be found
		/// </exception>
		TypeDescriptor GetType(System.String classname);
		
		/// <summary> Locate the set of component types capable of services the supplied
		/// dependency.
		/// </summary>
		/// <param name="dependency">a service dependency descriptor
		/// </param>
		/// <returns> a set of types capable of servicing the supplied dependency
		/// </returns>
		TypeDescriptor[] GetTypes(DependencyDescriptor dependency);
		
		/// <summary> Locate the set of component types capable of services the supplied
		/// dependency.
		/// </summary>
		/// <param name="dependency">a service dependency descriptor
		/// </param>
		/// <returns> a set of types capable of servicing the supplied dependency
		/// </returns>
		TypeDescriptor[] GetTypes(DependencyDescriptor dependency, bool search);
		
		/// <summary> Locate the set of component types that provide the supplied extension.</summary>
		/// <param name="stage">a stage descriptor
		/// </param>
		/// <returns> a set of types that support the supplied stage
		/// </returns>
		TypeDescriptor[] GetTypes(StageDescriptor stage);
		
		/// <summary> Return the set of deployment profiles for the supplied type. An 
		/// implementation is required to return a array of types > 0 in length
		/// or throw a TypeUnknownException.
		/// </summary>
		/// <param name="type">the type
		/// </param>
		/// <returns> a profile array containing at least one deployment profile
		/// </returns>
		/// <exception cref=""> TypeUnknownException if the supplied type is unknown
		/// </exception>
		ComponentProfile[] GetProfiles(TypeDescriptor type);
		
		/// <summary> Return a deployment profile for the supplied type and key.</summary>
		/// <param name="type">the type
		/// </param>
		/// <param name="key">the profile name
		/// </param>
		/// <returns> a profile matching the supplied key
		/// </returns>
		/// <exception cref=""> TypeUnknownException if the supplied type is unknown
		/// </exception>
		/// <exception cref=""> ProfileUnknownException if the supplied key is unknown
		/// </exception>
		ComponentProfile GetProfile(TypeDescriptor type, System.String key);
		
		/// <summary> Attempt to locate a packaged deployment profile meeting the 
		/// supplied dependency description.
		/// 
		/// </summary>
		/// <param name="dependency">the dependency description 
		/// </param>
		/// <param name="search">include profiles from parent repository in selection
		/// </param>
		/// <returns> the deployment profile (possibly null) 
		/// </returns>
		DeploymentProfile GetProfile(DependencyDescriptor dependency, bool search);
		
		/// <summary> Return a set of local deployment profile for the supplied dependency.</summary>
		/// <param name="dependency">the dependency descriptor
		/// </param>
		/// <param name="search">include profiles from parent repository in selection
		/// </param>
		/// <returns> a set of profiles matching the supplied dependency
		/// </returns>
		DeploymentProfile[] GetProfiles(DependencyDescriptor dependency, bool search);
	}
}