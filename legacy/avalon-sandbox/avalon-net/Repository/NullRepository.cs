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

namespace Apache.Avalon.Repository
{
	using System;

	/// <summary>
	/// Summary description for NullRepository.
	/// </summary>
	public class NullRepository : IRepository
	{
		public static readonly NullRepository Empty = new NullRepository();

		private NullRepository()
		{
		}

		/// <summary>
		/// Pending
		/// </summary>
		/// <param name="artifact"></param>
		/// <returns></returns>
		public Uri GetResource( Artifact artifact )
		{
			return null;
		}
	}
}
