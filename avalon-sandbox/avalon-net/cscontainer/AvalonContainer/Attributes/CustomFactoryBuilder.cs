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

	/// <summary>
	/// Marks that a ComponentFactory have a Custom FactoryBuilder 
	/// (used instantiate ComponentFactories)
	/// </summary>
	[AttributeUsage(AttributeTargets.Class,AllowMultiple=false)]
	public class CustomFactoryBuilderAttribute : Attribute
	{
		private Type m_customBuilder;

		/// <summary>
		/// Creates a new instance of CustomFactoryBuilderAttribute and 
		/// specify the type of the Custom FactoryBuilder.
		/// </summary>
		/// <param name="customBuilder">The type derived from FactoryBuilder</param>
		/// <exception cref="System.ArgumentException">If the type is not a subclass of FactoryBuilder</exception>
		/// <exception cref="System.ArgumentNullException">If a null type is specified</exception>
		public CustomFactoryBuilderAttribute(Type customBuilder)
		{
			if ( customBuilder == null )
			{
				throw new ArgumentNullException( "customBuilder" );
			}
			if (!customBuilder.IsSubclassOf( typeof(Apache.Avalon.Container.Factory.FactoryBuilder) ) )
			{
				throw new ArgumentException( "Specified type is not a subclass of FactoryBuilder.", "customBuilder" );
			}

			m_customBuilder = customBuilder;
		}

		/// <summary>
		/// Returns the Custom FactoryBuilder type;
		/// </summary>
		public Type FactoryBuilder
		{
			get
			{
				return m_customBuilder;
			}
		}
	}
}
