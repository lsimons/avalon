// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.Util
{
	using System;

	/// <summary>
	/// Summary description for Assert.
	/// </summary>
	public sealed class Assert
	{
		private Assert()
		{
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="target"></param>
		public static void ArgumentNotNull( object target )
		{
			if ( target == null )
			{
				throw new ArgumentNullException( "target", "Argument can't be null" );
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="target"></param>
		/// <param name="argumentName"></param>
		public static void ArgumentNotNull( object target, String argumentName )
		{
			if ( target == null )
			{
				throw new ArgumentNullException( argumentName, String.Format("Argument '{0}' can't be null", argumentName) );
			}
		}
	}
}
