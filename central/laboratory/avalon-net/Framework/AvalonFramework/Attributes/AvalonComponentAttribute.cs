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

namespace Apache.Avalon.Framework
{
	using System;

	/// <summary>	/// Enumeration used to mark the component's lifestyle.	/// </summary>	public enum Lifestyle	{		/// <summary>		/// Singleton components are instantiated once, and shared		/// between all clients.		/// </summary>		Singleton,		/// <summary>		/// Thread components have a unique instance per thread.		/// </summary>		Thread,		/// <summary>		/// Pooled components have a unique instance per client,		/// but they are managed in a pool.		/// </summary>		Pooled,		/// <summary>		/// Transient components are created on demand.		/// </summary>		Transient,		/// <summary>		/// Custom lifestyle components should be managed by custom component factories.		/// </summary>		Custom	}	///<summary>	///  Attribute used to mark a component as an Avalon component.	///</summary>	[AttributeUsage(AttributeTargets.Class,AllowMultiple=false,Inherited=false)]	public sealed class AvalonComponentAttribute : Attribute	{		private Lifestyle m_lifestyle;		private string m_name;		/// <summary>		/// Marks a class as a component, providing a configuration name and preferred lifestyle		/// </summary>		/// <param name="name">The component logical name (may be used for configuration elements)</param>		/// <param name="lifestyle">The lifestyle used for the component</param>		public AvalonComponentAttribute( string name, Lifestyle lifestyle )		{			m_lifestyle = lifestyle;			m_name      = name;		}		/// <summary>		/// The component name assigned to this component.		/// </summary>		public string Name		{			get			{				return m_name;			}		}		/// <summary>		/// The lifestyle associated with the component		/// </summary>		public Lifestyle Lifestyle		{			get			{				return m_lifestyle;			}		}	}
}
