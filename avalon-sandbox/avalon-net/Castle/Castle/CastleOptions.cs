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

namespace Apache.Avalon.Castle
{
	using System;
	using System.IO;

	/// <summary>
	/// Summary description for CastleOptions.
	/// </summary>
	[Serializable]
	public class CastleOptions
	{
		/// <summary>
		/// 
		/// </summary>
		private String defaultDomain = Castle.CASTLE_DOMAIN;
		
		/// <summary>
		/// 
		/// </summary>
		private String homePath;

		/// <summary>
		/// 
		/// </summary>
		private String tempPath;

		/// <summary>
		/// 
		/// </summary>
		private bool isolatedDomain = true;

		/// <summary>
		/// 
		/// </summary>
		private bool noJoin = false;

		/// <summary>
		/// 
		/// </summary>
		private bool remoting = true;

		/// <summary>
		/// 
		/// </summary>
		private String serverConnectorUrl;

		/// <summary>
		/// 
		/// </summary>
		private String systemConfig;

		/// <summary>
		/// 
		/// </summary>
		private long timeout;

		/// <summary>
		/// 
		/// </summary>
		private bool traceEnabled;

		public CastleOptions()
		{
			homePath = Environment.GetFolderPath( Environment.SpecialFolder.ApplicationData );
			tempPath = Path.GetTempPath( );
			serverConnectorUrl = "provider:tcp:binary:server.rem";
		}

		public String DomainName
		{
			get
			{
				return defaultDomain;
			}
			set
			{
				defaultDomain = value;
			}
		}

		public String HomePath
		{
			get
			{
				return homePath;
			}
			set
			{
				homePath = value;
			}
		}

		public String BasePath
		{
			get
			{
				return AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
			}
		}

		public String TempPath
		{
			get
			{
				return tempPath;
			}
			set
			{
				tempPath = value;
			}
		}

		public String SystemConfig
		{
			get
			{
				return systemConfig;
			}
			set
			{
				systemConfig = value;
			}
		}

		public bool IsolatedDomain
		{
			get
			{
				return isolatedDomain;
			}
			set
			{
				isolatedDomain = value;
			}
		}

		public bool NoThreadJoin
		{
			get
			{
				return noJoin;
			}
			set
			{
				noJoin = value;
			}
		}

		public bool EnableRemoteManagement
		{
			get
			{
				return remoting;
			}
			set
			{
				remoting = value;
			}
		}

		public String ServerConnectorUrl
		{
			get
			{
				return serverConnectorUrl;
			}
			set
			{
				serverConnectorUrl = value;
			}
		}

		public bool TraceEnabled 
		{
			get
			{
				return traceEnabled;
			}
			set
			{
				traceEnabled = value;
			}
		}

		public long DeploymentTimeout 
		{
			get
			{
				return timeout;
			}
			set
			{
				timeout = value;
			}
		}
	}
}
