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

namespace Samples.Components
{
	using System;
	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for Authentication.
	/// </summary>
	[AvalonService( typeof(IAuthentication) )]
	[AvalonComponent( @"Samples.Components\Authentication", Lifestyle.Transient, LoggerName="AuthLog" )]
	public class Authentication : 
		IAuthentication, IInitializable, ILogEnabled, IConfigurable, IDisposable
	{
		private ILogger m_logger;
		private String  m_name = String.Empty;

		public String Name
		{
			get
			{
				return m_name;
			}
		}

		#region IInitializable Members
		public void Initialize()
		{
		}
		#endregion

		#region ILogEnabled Members
		public void EnableLogging(ILogger logger)
		{
			m_logger = logger;
		}
		#endregion

		#region IDisposable Members
		public void Dispose()
		{
			ContainerUtil.Shutdown(m_logger);
		}
		#endregion
	
		#region IConfigurable Members
		public void Configure(IConfiguration config)
		{
			IConfiguration usernameNode = config.GetChild("username", false);

			if (usernameNode != null)
			{
				m_name = (String) usernameNode.GetValue(typeof(String), String.Empty);
			}
		}
		#endregion
	}
}
