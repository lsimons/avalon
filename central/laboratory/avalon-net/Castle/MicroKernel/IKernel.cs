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

namespace Apache.Avalon.Castle.MicroKernel
{
	using System;

	using Apache.Avalon.Castle.MicroKernel.Model;

	public delegate void DependencyListenerDelegate( Type service, IHandler handler );

    /// <summary>
	/// Defines the Kernel service
	/// </summary>
    public interface IKernel : IKernelEvents
    {
		/// <summary>
		/// Adds a component to kernel.
		/// </summary>
		/// <param name="key">The unique key that identifies the component</param>
		/// <param name="service">The service exposed by this component</param>
		/// <param name="implementation">The actual implementation</param>
		void AddComponent(String key, Type service, Type implementation);

        /// <summary>
		/// IComponentModel instance builder.
		/// </summary>
		IComponentModelBuilder ModelBuilder { get; set; }

		/// <summary>
		/// Obtains a component handler using the 
		/// unique component key
		/// </summary>
		IHandler this[String key] { get; }

		/// <summary>
		/// 
		/// </summary>
		/// <param name="key"></param>
		/// <param name="criteria"></param>
		/// <returns></returns>
		IHandler GetHandler(String key, object criteria);

		/// <summary>
		/// Gets or Sets the IHandlerFactory implementation
		/// </summary>
		IHandlerFactory HandlerFactory { get; set; }

		/// <summary>
		/// Gets or Sets the ILifestyleManagerFactory implementation
		/// </summary>
		ILifestyleManagerFactory LifestyleManagerFactory { get; set; }

		/// <summary>
		/// Returns true if kernel "knows" the specified 
		/// service
		/// </summary>
		/// <param name="service">The service interface</param>
		/// <returns>true if is already registered</returns>
		bool HasService(Type service);

		/// <summary>
		/// Used by handlers to register itself as 
		/// and dependency to be satisfied.
		/// </summary>
		/// <param name="service">The service interface</param>
		/// <param name="depDelegate">Delegate to be invoked</param>
		void AddDependencyListener(Type service, DependencyListenerDelegate depDelegate);

		/// <summary>
		/// Returns a IHandler implementation for 
		/// the specified service
		/// </summary>
		/// <param name="service">The service interface</param>
		/// <returns>IHandler implementation</returns>
		IHandler GetHandlerForService(Type service);

		/// <summary>
		/// Adds a subsystem.
		/// </summary>
		/// <param name="key">Name of this subsystem</param>
		/// <param name="system">Subsystem implementation</param>
		void AddSubsystem(String key, IKernelSubsystem system);

		/// <summary>
		/// Returns a registered subsystem;
		/// </summary>
		/// <param name="key">Key used when registered subsystem</param>
		/// <returns>Subsystem implementation</returns>
		IKernelSubsystem GetSubsystem(String key);
	}
}