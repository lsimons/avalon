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

namespace Apache.Avalon.Container.Extension
{
	using System;

	/// <summary>
	/// Defines the entry point for extensions hook up into the
	/// lifecycle management of Container.
	/// </summary>
	/// <remarks>
	/// An extension implementation could extend or even change the
	/// default behavior from Avalon Container. To enable this implement
	/// this interface and on the Init method, subscribe for the
	/// events you want. 
	/// </remarks>
	public interface IExtensionModule
	{
		/// <summary>
		/// Gives an opportunity of subscribe for interesting events raised
		/// by the Avalon Container Framework.
		/// </summary>
		/// <param name="manager"><see cref="LifecycleManager"/>The source of events.</param>
		void Init(LifecycleManager manager);
	}
}
