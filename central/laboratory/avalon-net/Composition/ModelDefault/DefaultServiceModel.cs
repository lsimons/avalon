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
namespace Apache.Avalon.Composition.Model.Default
{
	using System;

	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> Service model exposes an exported service class.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultServiceModel : IServiceModel
	{
		private IDeploymentModel m_provider;
		private ServiceDirective m_directive;
		private System.Type m_type;
		
		public DefaultServiceModel(ServiceDirective directive, System.Type type, IDeploymentModel provider)
		{
			m_provider = provider;
			m_directive = directive;
			m_type = type;
		}
		
		/// <summary> Return the service directive for the model.
		/// 
		/// </summary>
		/// <returns> the directive declaring the service export
		/// </returns>
		public virtual ServiceDirective ServiceDirective
		{
			get
			{
				return m_directive;
			}
		}
		
		/// <summary> Return the service class.  </summary>
		/// <returns> the service class
		/// </returns>
		public virtual System.Type ServiceClass
		{
			get
			{
				return m_type;
			}
		}

		/// <summary> Return the service provider.  </summary>
		/// <returns> the model identifying the provider implementation
		/// </returns>
		public virtual IDeploymentModel ServiceProvider
		{
			get
			{
				return m_provider;
			}
		}
	}
}