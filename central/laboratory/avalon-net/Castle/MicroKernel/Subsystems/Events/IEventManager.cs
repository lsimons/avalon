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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Events
{
	using System;

	public delegate void KernelDelegate( EventManagerData eventData );

	/// <summary>
	/// Allows hooks into the Kernel.
	/// </summary>
	public interface IEventManager : IKernelSubsystem
	{
		event KernelDelegate ComponentAdded;

		event KernelDelegate ComponentCreated;

		event KernelDelegate ComponentDestroyed;

		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		void OnComponentAdded( EventManagerData data );

		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		void OnComponentCreated( EventManagerData data );
		
		/// <summary>
		/// Allows kernel and Subsystems to raise events.
		/// </summary>
		/// <param name="data"></param>
		void OnComponentDestroyed( EventManagerData data );
	}
}
