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

namespace Apache.Avalon.Activation
{
	using System;

	/// <summary>
	/// A factory that provides object instantiation. A instance factory will
	/// typically encapsulate component constuction semantic, lifecycle processing
	/// and end-of-life processing.
	/// </summary>
	public interface IComponentFactory
	{
		/// <summary>
		/// Creation of a new instance including all deployment stage handling.
		/// </summary>
		/// <returns>the new instance</returns>
		/// <exception cref="Exception">If errors occur</exception>
		object Incarnate();

		/// <summary>
		/// Termination of the instance including all end-of-life processing.
		/// </summary>
		/// <param name="instance">the component instance</param>
		void Etherialize( object instance );
	}
}
