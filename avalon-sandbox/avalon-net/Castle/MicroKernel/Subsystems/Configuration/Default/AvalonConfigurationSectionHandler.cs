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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Configuration.Default
{
	using System;
	using System.Configuration;
	using System.Xml;

	/// <summary>
	/// Implementation of <see cref="System.Configuration.IConfigurationSectionHandler"/>
	/// to allow natural mapping of a section in the configuration file associated with an 
	/// AppDomain to a <see cref="ContainerConfiguration"/>.
	/// </summary>
	public class AvalonConfigurationSectionHandler : IConfigurationSectionHandler
	{
		/// <summary>
		/// The static name of the section in the configuration file.
		/// </summary>
		private static readonly String SECTION_NAME = "castle.container";

		public AvalonConfigurationSectionHandler()
		{
		}

		/// <summary>
		/// Returns the default name of the section in the
		/// configuration file.
		/// </summary>
		internal static String Section
		{
			get
			{
				return SECTION_NAME;
			}
		}

		#region IConfigurationSectionHandler Members

		/// <summary>
		/// Invoke by Configuration API - Should return any object represeting the
		/// actual configuration
		/// </summary>
		/// <param name="parent">If hierarquical configuration is being used, 
		/// a non-null ContainerConfiguration to be overrided.
		/// </param>
		/// <param name="configContext">API specific.</param>
		/// <param name="section">The <see cref="XmlNode"/> to be parsed.</param>
		/// <returns>A ContainerConfiguration instance.</returns>
		public object Create(object parent, object configContext, XmlNode section)
		{
			return new ContainerConfiguration(
				(ContainerConfiguration) parent, section);
		}

		#endregion
	}
}
