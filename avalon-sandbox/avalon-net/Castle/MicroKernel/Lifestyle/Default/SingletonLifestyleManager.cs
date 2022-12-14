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

namespace Apache.Avalon.Castle.MicroKernel.Lifestyle.Default
{
	using System;

	/// <summary>
	/// Summary description for SingletonLifestyleManager.
	/// </summary>
	public class SingletonLifestyleManager : AbstractLifestyleManager
	{
		private Object m_instance;

		public SingletonLifestyleManager(IComponentFactory componentFactory) : base(componentFactory)
		{
		}

		~SingletonLifestyleManager()
		{
			base.Release( m_instance );
		}

		#region IResolver Members

		public override object Resolve()
		{
			lock(m_componentFactory)
			{
				if (m_instance == null)
				{
					m_instance = base.Resolve();
				}
			}

			return m_instance;
		}

		public override void Release( object instance )
		{
			// Do nothing
		}

		#endregion
	}
}
