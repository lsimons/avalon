// Copyright 2004 Apache Software Foundation
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

namespace Apache.Avalon.Composition.Logging
{
	using System;
	
	/// <summary> A logging target descriptor.
	/// 
	/// <p><b>XML</b></p>
	/// A logging target declares target name and a logging mechanism.</p>
	/// 
	/// <pre>
	/// &lt;target name="<font color="darkred">kernel</font>"&gt;
	/// &lt;file location="<font color="darkred">kernel.log</font>" /&gt;
	/// &lt;/target&gt;
	/// </pre>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:02 $
	/// </version>
	[Serializable]
	public sealed class TargetDescriptor
	{
		
		/// <summary> The target name.</summary>
		private System.String m_name;
		
		/// <summary> The target provider descriptor.</summary>
		private TargetProvider m_provider;
		
		/// <summary> Create a LoggingDescriptor instance.
		/// 
		/// </summary>
		/// <param name="name">the target name
		/// </param>
		/// <param name="provider">the target provider description
		/// </param>
		public TargetDescriptor(System.String name, TargetProvider provider)
		{
			m_name = name;
			m_provider = provider;
		}

		/// <summary> Return the target name.
		/// 
		/// </summary>
		/// <returns> the target name.
		/// </returns>
		public System.String Name
		{
			get
			{
				return m_name;
			}
			
		}
		/// <summary> Return the target provider descriptor
		/// 
		/// </summary>
		/// <returns> the provider descriptor
		/// </returns>
		public TargetProvider Provider
		{
			get
			{
				return m_provider;
			}
			
		}
	}
}