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

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Subsystems;

	/// <summary>
	/// The default implementation of IConfigurationManager uses the 
	/// .config associated with the AppDomain to extract the components
	/// configurations
	/// </summary>
	public class DefaultConfigurationManager : AbstractSubsystem, IConfigurationManager
	{
		protected ContainerConfiguration m_config;

		public DefaultConfigurationManager()
		{
			m_config = (ContainerConfiguration) ConfigurationSettings.GetConfig( 
				AvalonConfigurationSectionHandler.Section );
		}

		#region IConfigurationManager Members

		public IConfiguration GetConfiguration( String componentName )
		{
			AssertUtil.ArgumentNotNull( componentName, "componentName" );

			return m_config.Configuration.GetChild( componentName, true );
		}

		#endregion
	}
}