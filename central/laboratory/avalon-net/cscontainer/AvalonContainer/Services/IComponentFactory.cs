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

namespace Apache.Avalon.Container.Services{	using System;	using Apache.Avalon.Framework;
	/// <summary>	/// <para>Describes a Component Factory, usually binded to	/// a kind of <see cref="Lifestyle"/>.	/// </para>	/// </summary>	/// <remarks>	/// A implementation of IComponentFactory should use a 	/// <see cref="Apache.Avalon.Container.Factory.FactoryBuilder"/> 	/// to manage instantiations of Component Factories instances. 	/// This could be accomplished by using 	/// <see cref="Apache.Avalon.Container.Attributes.CustomFactoryBuilderAttribute"/> 	/// attribute.	/// </remarks>	public interface IComponentFactory : IDisposable	{		/// <summary>
		/// Should return a component instance using the specific 
		/// strategy (pool, thread or simply instantiating a new object).
		/// </summary>
		/// <param name="componentType">The type (concrete) of the component</param>
		/// <returns>The component instance</returns>	    object Create(Type componentType);
		/// <summary>
		/// Returns true if the specified instanced was created by 
		/// this component factory implementation.
		/// </summary>
		/// <param name="componentInstance">The component instance</param>
		/// <returns><c>true</c> if this factory owns the specified component</returns>
		bool IsOwner(object componentInstance);

		/// <summary>
		/// Releases the component instance.
		/// </summary>
		/// <remarks>
		/// The factory shall only release instances created by itself.
		/// </remarks>
		/// <param name="componentInstance">The component instance</param>
        void Release(object componentInstance);	}}
