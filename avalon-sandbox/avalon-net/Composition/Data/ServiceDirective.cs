// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Composition.Data
{
	using System;
	using Apache.Avalon.Meta;
	
	/// <summary> A ServiceDirective is a class that holds a reference to a published
	/// service together with a component implementation path reference, referencing 
	/// the component implementing the service.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
	/// </author>
	/// <version>  CVS $Revision: 1.2 $ $Date: 2004/02/28 22:15:36 $
	/// </version>
	public sealed class ServiceDirective : ServiceDescriptor
	{
		/// <summary> The relative path to the component implementing the service.</summary>
		private System.String m_path;
		
		/// <summary> Creation of a new service directive.
		/// 
		/// </summary>
		/// <param name="descriptor">the published service
		/// </param>
		/// <param name="path">the relative path of the implementing component
		/// </param>
		public ServiceDirective(ServiceDescriptor descriptor, System.String path):base(descriptor)
		{
			
			// TODO: put in place relative and absolute addressing
			
			if (path.StartsWith("/"))
			{
				m_path = path.Substring(1, (path.Length) - (1));
			}
			else
			{
				m_path = path;
			}
		}

		/// <summary> Return the virtual service component relative path.</summary>
		/// <returns> the relative component path
		/// </returns>
		public System.String Path
		{
			get
			{
				return m_path;
			}
			
		}
	}
}