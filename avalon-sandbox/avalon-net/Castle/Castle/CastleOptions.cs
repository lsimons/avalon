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

namespace Apache.Avalon.Castle
{
	using System;

	/// <summary>
	/// Summary description for CastleOptions.
	/// </summary>
	public class CastleOptions
	{
		private String defaultDomain = Castle.CASTLE_DOMAIN;
		private String homePath;
		private bool isolatedDomain = true;
		private bool noJoin = false;
		private bool remoting = true;
		private String serverConnectorUrl = "provider:tcp:binary:server.rem";

		public CastleOptions()
		{
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
	}
}
