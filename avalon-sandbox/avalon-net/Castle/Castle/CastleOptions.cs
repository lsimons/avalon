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
		private String m_defaultDomain = Castle.CASTLE_DOMAIN;
		
		/// <summary>
		/// 
		/// </summary>
		private String m_homePath;

		/// <summary>
		/// 
		/// </summary>
		private String m_tempPath;

		/// <summary>
		/// 
		/// </summary>
		private bool m_isolatedDomain = true;

		/// <summary>
		/// 
		/// </summary>
		private bool m_noJoin = false;

		/// <summary>
		/// 
		/// </summary>
		private bool m_remoting = true;

		/// <summary>
		/// 
		/// </summary>
		private String m_serverConnectorUrl;

		/// <summary>
		/// 
		/// </summary>
		private String m_systemConfig;

		/// <summary>
		/// 
		/// </summary>
		private long m_timeout = 10000;

		/// <summary>
		/// 
		/// </summary>
		private bool m_traceEnabled;

		/// <summary>
		/// 
		/// </summary>
		public CastleOptions()
		{
			m_homePath = Environment.GetFolderPath( Environment.SpecialFolder.ApplicationData );
			m_tempPath = Path.GetTempPath( );
			m_serverConnectorUrl = "provider:tcp:binary:server.rem";
		}

		public String DomainName
		{
			get
			{
				return m_defaultDomain;
			}
			set
			{
				m_defaultDomain = value;
			}
		}

		public String HomePath
		{
			get
			{
				return m_homePath;
			}
			set
			{
				m_homePath = value;
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
				return m_tempPath;
			}
			set
			{
				m_tempPath = value;
			}
		}

		public String SystemConfig
		{
			get
			{
				return m_systemConfig;
			}
			set
			{
				m_systemConfig = value;
			}
		}

		public bool IsolatedDomain
		{
			get
			{
				return m_isolatedDomain;
			}
			set
			{
				m_isolatedDomain = value;
			}
		}

		public bool NoThreadJoin
		{
			get
			{
				return m_noJoin;
			}
			set
			{
				m_noJoin = value;
			}
		}

		public bool EnableRemoteManagement
		{
			get
			{
				return m_remoting;
			}
			set
			{
				m_remoting = value;
			}
		}

		public String ServerConnectorUrl
		{
			get
			{
				return m_serverConnectorUrl;
			}
			set
			{
				m_serverConnectorUrl = value;
			}
		}

		public bool TraceEnabled 
		{
			get
			{
				return m_traceEnabled;
			}
			set
			{
				m_traceEnabled = value;
			}
		}

		public long DeploymentTimeout 
		{
			get
			{
				return m_timeout;
			}
			set
			{
				m_timeout = value;
			}
		}
	}
}
