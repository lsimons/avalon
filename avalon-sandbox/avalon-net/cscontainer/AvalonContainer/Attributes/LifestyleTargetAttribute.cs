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

namespace Apache.Avalon.Container.Attributes
{
	using System;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Attribute used to define which lifestyle a 
	/// component factory handles.
	/// <seealso cref="Apache.Avalon.Container.Services.IComponentFactory"/>
	/// </summary>
	[AttributeUsage(AttributeTargets.Class,AllowMultiple=false)]
	public class LifestyleTargetAttribute : Attribute
	{
		private Lifestyle m_lifestyle;

		/// <summary>
		/// Constructs a LifestyleTargetAttribute.
		/// </summary>
		/// <param name="lifestyle"></param>
		public LifestyleTargetAttribute(Lifestyle lifestyle)
		{
			m_lifestyle = lifestyle;
		}

		/// <summary>
		/// Gets the supported lifestyle of the component factory.
		/// </summary>
		public Lifestyle Lyfestyle
		{
			get
			{
				return m_lifestyle;
			}
		}
	}
}
