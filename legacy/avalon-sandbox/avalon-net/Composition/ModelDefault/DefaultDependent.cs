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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> Default dependent model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultDependent : AbstractLogEnabled, IDependent
	{
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		private IDeploymentModel m_provider;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of a new stage model.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel
		/// </param>
		public DefaultDependent(ILogger logger)
		{
			if (logger == null)
			{
				throw new System.ArgumentNullException("logger");
			}
			EnableLogging(logger);
		}
		
		//--------------------------------------------------------------
		// Dependent
		//--------------------------------------------------------------
		
		/// <summary> Set the provider model.
		/// 
		/// </summary>
		/// <param name="model">the provider model
		/// </param>
		public virtual IDeploymentModel Provider
		{
			set
			{
				m_provider = value;
			}
			get
			{
				return m_provider;
			}
		}
		
		/// <summary> Clear the assigned provider.</summary>
		public virtual void ClearProvider()
		{
			m_provider = null;
		}
	}
}