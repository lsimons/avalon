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

namespace Apache.Avalon.Castle.Default.Repository
{
	using System;
	using System.IO;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Repository;

	/// <summary>
	/// Summary description for DefaultRepository.
	/// </summary>
	[ManagedComponent]
	public class DefaultRepository : ManagedService, IRepository
	{
		String basePath;

		public DefaultRepository()
		{
			basePath = AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
		}
	
		#region IRepository Members

		[ManagedOperation]
		public Uri GetResource(Artifact artifact)
		{
			// TODO:  Add DefaultRepository.GetResource implementation
			return null;
		}

		#endregion
	}
}
