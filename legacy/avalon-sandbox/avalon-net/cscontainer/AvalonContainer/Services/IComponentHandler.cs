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

namespace Apache.Avalon.Container.Services
{
	using System;

	/// <summary>
	/// Manages component instances. Generally handles lifecycles methods.
	/// </summary>
	/// <remarks>
	/// <para>
	/// A IComponentHandler implementation usually will hold a 
	/// <see cref="IComponentFactory"/> instance 
	/// - althrough it's not really necessary - but respects 
	/// the Single Responsability principle.
	/// </para>
	/// <para>A handler shall return a <b>ready for use</b> 
	/// component instance.
	/// </para>
	/// </remarks>
	public interface IComponentHandler
	{
		/// <summary>
		/// Returns a "ready for use" component instance.
		/// </summary>
		/// <returns>The component instance</returns>
		object GetInstance();

		/// <summary>
		/// Shall release or do whatever is necessary to a 
		/// used component instance (add to a pool, for instance)
		/// </summary>
		/// <param name="instance">The component instance</param>
		void PutInstance(object instance);
	}
}
