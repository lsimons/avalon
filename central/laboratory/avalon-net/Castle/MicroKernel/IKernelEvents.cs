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
	using Apache.Avalon.Castle.MicroKernel.Interceptor;

    public delegate void ComponentDataDelegate( IComponentModel model, String key, IHandler handler );

	public delegate void WrapDelegate( IComponentModel model, String key, IHandler handler, IInterceptedComponent interceptedComponent );

    public delegate void UnWrapDelegate( IComponentModel model, String key, IHandler handler, IInterceptedComponent interceptedComponent );

	public delegate void ComponentInstanceDelegate( IComponentModel model, String key, IHandler handler, object instance );

	public delegate void ComponentModelDelegate( IComponentModel model, String key );

    /// <summary>
    /// 
    /// </summary>
    public interface IKernelEvents
    {
        /// <summary>
        /// 
        /// </summary>
        event ComponentDataDelegate ComponentRegistered;

		/// <summary>
		/// 
		/// </summary>
		event ComponentDataDelegate ComponentUnregistered;

		/// <summary>
		/// 
		/// </summary>
		event WrapDelegate ComponentWrap;

		/// <summary>
		/// 
		/// </summary>
		event UnWrapDelegate ComponentUnWrap;

		/// <summary>
		/// 
		/// </summary>
		event ComponentInstanceDelegate ComponentReady;

		/// <summary>
		/// 
		/// </summary>
		event ComponentInstanceDelegate ComponentReleased;

		/// <summary>
		/// 
		/// </summary>
		event ComponentModelDelegate ComponentModelConstructed;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="instance"></param>
        /// <param name="handler"></param>
        /// <returns></returns>
        object RaiseWrapEvent( IHandler handler, object instance );

        /// <summary>
        /// 
        /// </summary>
        /// <param name="instance"></param>
        /// <param name="handler"></param>
        /// <returns></returns>
        object RaiseUnWrapEvent( IHandler handler, object instance );

        /// <summary>
        /// 
        /// </summary>
        /// <param name="instance"></param>
        /// <param name="handler"></param>
        void RaiseComponentReadyEvent( IHandler handler, object instance );

        /// <summary>
        /// 
        /// </summary>
        /// <param name="instance"></param>
        /// <param name="handler"></param>
        void RaiseComponentReleasedEvent( IHandler handler, object instance );
    }
}
