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

namespace Apache.Avalon.Castle.Runtime.Extended
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;

	/// <summary>
	/// Summary description for Castle Runtime Extended.
	/// </summary>
	public sealed class Constants
	{
		/// <summary>
		/// Pending.
		/// </summary>
		public static readonly ManagedObjectName DEFAULT_RUNTIME_FACTORY_NAME = new ManagedObjectName("apache.avalon.castle.runtime.extended:name=RuntimeFactory");

		/// <summary>
		/// Pending.
		/// </summary>
		public static readonly String DEFAULT_RUNTIME_FACTORY_TYPE = typeof(Activation.RuntimeFactory).FullName + ", Apache.Avalon.Castle.Runtime.Extended";
	}
}
