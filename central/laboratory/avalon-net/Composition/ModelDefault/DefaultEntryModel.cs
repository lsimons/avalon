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
	
	/// <summary> Abstract implementation of a the context entry model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public abstract class DefaultEntryModel : IEntryModel
	{
		//==============================================================
		// immmutable state
		//==============================================================
		
		private EntryDescriptor m_descriptor;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new context entry import model.
		/// 
		/// </summary>
		/// <param name="descriptor">the context entry descriptor
		/// </param>
		public DefaultEntryModel(EntryDescriptor descriptor)
		{
			if (descriptor == null)
			{
				throw new System.ArgumentNullException("descriptor");
			}
			m_descriptor = descriptor;
		}
		
		//==============================================================
		// IEntryModel
		//==============================================================
		
		/// <summary> Return the context entry key.
		/// 
		/// </summary>
		/// <returns> the key
		/// </returns>
		public virtual String Key
		{
			get
			{
				return m_descriptor.Key;
			}
		}
		
		/// <summary> Return the context entry value.
		/// 
		/// </summary>
		/// <returns> the context entry value
		/// </returns>
		public abstract Object Value
		{
			get;
		}
	}
}