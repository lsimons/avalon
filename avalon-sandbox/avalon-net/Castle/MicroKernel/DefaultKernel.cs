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
		}

		#region AvalonKernel Members

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
