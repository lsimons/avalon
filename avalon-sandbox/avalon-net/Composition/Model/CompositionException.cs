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
	
	/// <summary> Exception to indicate that there was a appliance related error.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	[Serializable]
	public class CompositionException : System.Exception
	{
		
		/// <summary> Construct a new <code>CompositionException</code> instance.
		/// 
		/// </summary>
		/// <param name="message">The detail message for this exception.
		/// </param>
		public CompositionException(System.String message):this(message, null)
		{
		}
		
		/// <summary> Construct a new <code>CompositionException</code> instance.
		/// 
		/// </summary>
		/// <param name="message">The detail message for this exception.
		/// </param>
		/// <param name="throwable">the root cause of the exception
		/// </param>
		public CompositionException(System.String message, System.Exception throwable):base(message, throwable)
		{
		}

		public CompositionException(System.Runtime.Serialization.SerializationInfo info, 
			System.Runtime.Serialization.StreamingContext context) : base(info, context)
		{
		}
	}
}