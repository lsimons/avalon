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
	
	/// <summary> A service repository provides support for the storage and retrival
	/// of service defintions.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public interface IServiceRepository
	{
		/// <summary> Locate a {@link Service} instances associated with the
		/// supplied classname and version. If a service defintion is not
		/// found locally, the implementation redirects the request to
		/// the parent service manager.
		/// 
		/// </summary>
		/// <param name="type">the service class name
		/// </param>
		/// <returns> the service matching the supplied classname and version.
		/// </returns>
		/// <exception cref=""> UnknownServiceException if a matching service cannot be found
		/// </exception>
		Service GetService(System.Type type);
		
		/// <summary> Locate a {@link Service} instances associated with the
		/// supplied referecne descriptor. If a service defintion is not
		/// found locally, the implementation redirects the request to
		/// the parent service manager.
		/// 
		/// </summary>
		/// <param name="reference">the reference descriptor
		/// </param>
		/// <returns> the service matching the supplied descriptor.
		/// </returns>
		/// <exception cref=""> UnknownServiceException if a matching service cannot be found
		/// </exception>
		Service GetService(ReferenceDescriptor reference);
	}
}