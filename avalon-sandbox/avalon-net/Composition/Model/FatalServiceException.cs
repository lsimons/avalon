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
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	[Serializable]
	public class FatalServiceException : ServiceException
	{
		/// <summary> Construct a new <code>FatalServiceException</code> instance.
		/// 
		/// </summary>
		/// <param name="key">the lookup key
		/// </param>
		/// <param name="message">The detail message for this exception.
		/// </param>
		/// <param name="cause">expected service availability delay in milliseconds 
		/// </param>
		public FatalServiceException(System.String key, System.String message, System.Exception cause):base(key, message, cause)
		{
		}

		public FatalServiceException(System.Runtime.Serialization.SerializationInfo info, 
			System.Runtime.Serialization.StreamingContext context) : base(info, context)
		{
		}
	}
}