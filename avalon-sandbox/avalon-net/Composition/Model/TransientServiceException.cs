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
	
	/// <summary> Exception to indicate that there was a transient service error. The 
	/// exception exposes a delay value which is the anticipated delay in 
	/// service availability.  A delay value of 0 indicates an unknown delay.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public class TransientServiceException : ServiceException
	{
		private long m_delay;
		
		/// <summary> Construct a new <code>TransientServiceException</code> instance.
		/// 
		/// </summary>
		/// <param name="key">the lookup key
		/// </param>
		/// <param name="message">The detail message for this exception.
		/// </param>
		/// <param name="delay">expected service availability delay in milliseconds 
		/// </param>
		public TransientServiceException(System.String key, System.String message, long delay):base(key, message)
		{
			m_delay = delay;
		}

		/// <summary> Returns the expected duration of service non-availability.</summary>
		/// <returns> the non-availability duration
		/// </returns>
		public virtual long Delay
		{
			get
			{
				return m_delay;
			}
			
		}
	}
}