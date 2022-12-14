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

	using Apache.Avalon.Castle.MicroKernel.Concerns;
	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Configuration.Default;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Logger.Default;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Context.Default;

	/// <summary>
	/// Specialization of BaseKernel to adhere to Avalon 
	/// constraints and semantics.
	/// </summary>
	public class DefaultKernel : BaseKernel, AvalonKernel
	{
		protected ConcernManager m_concerns = new ConcernManager();

		/// <summary>
		/// 
		/// </summary>
		public DefaultKernel()
		{
			m_handlerFactory = new Handler.Default.DefaultHandlerFactory();
			m_lifestyleManagerFactory = new Lifestyle.Default.SimpleLifestyleManagerFactory();

			AddSubsystem( KernelConstants.CONFIGURATION, new DefaultConfigurationManager() );
			AddSubsystem( KernelConstants.LOGGER, new LoggerManager() );
			// AddSubsystem( KernelConstants.CONTEXT, new ContextManager() );
		}

		#region AvalonKernel Members

		/// <summary>
		/// Manages the concerns related
		/// to Avalon Framework
		/// </summary>
		public ConcernManager Concerns
		{
			get
			{
				return m_concerns;
			}
		}

		#endregion
	}
}
