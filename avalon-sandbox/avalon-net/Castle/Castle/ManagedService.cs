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

namespace Apache.Avalon.Castle
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for ManagedService.
	/// </summary>
	public abstract class ManagedService : MService
	{
		/// <summary>
		/// 
		/// </summary>
		protected MServer server;

		/// <summary>
		/// 
		/// </summary>
		private ManagedObjectName name;

		public ManagedService()
		{
		}

		#region MService Members

		[ManagedAttribute]
		public ManagedObjectName ManagedObjectName
		{
			get
			{
				return name;
			}
		}

		[ManagedOperation]
		public virtual void Create()
		{
		}

		[ManagedOperation]
		public virtual void Start()
		{
		}

		[ManagedOperation]
		public virtual void Stop()
		{
		}

		[ManagedOperation]
		public virtual void Destroy()
		{
		}

		#endregion

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			this.server = server;
			this.name = name;

			BeforeRegister();
		}

		public virtual void AfterDeregister()
		{
			this.server = null;
			this.name = null;
		}

		public virtual void AfterRegister()
		{
		}

		public virtual void BeforeDeregister()
		{
		}

		#endregion

		protected virtual void BeforeRegister()
		{
		}
	}
}
