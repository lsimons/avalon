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

namespace Apache.Avalon.Castle.Logger
{
	using System;
	using System.Configuration;

	using log4net;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.Logger.Config;

	/// <summary>
	/// Summary description for LoggerFactory.
	/// </summary>
	public sealed class LoggerFactory
	{
		private static LoggerFactory instance;

		private ILogger root;

		private LoggerFactory()
		{
			LoggerConfig config = (LoggerConfig)
				ConfigurationSettings.GetConfig("Castle/Container.Logger");

			if (config != null)
			{
				switch(config.LoggerType)
				{
					case LoggerType.Console:
						root = new ConsoleLogger("Castle");
						break;
					case LoggerType.Null:
						root = new NullLogger();
						break;
					case LoggerType.Log4net:
						ILog log = LogManager.GetLogger("Castle");
						root = new Log4netLogger(log, "Castle");
						break;
				}
			}
		}

		public static ILogger GetLogger(String name)
		{
			lock(typeof(LoggerFactory))
			{
				if (instance == null)
				{
					instance = new LoggerFactory();
				}
			}

			if (instance.root != null)
			{
				return instance.root.CreateChildLogger(name);
			}
			
			return new ConsoleLogger("Castle").CreateChildLogger(name);
		}
	}
}
