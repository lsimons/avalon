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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Logger.Default
{
	using System;
	using System.Collections;
	using System.Xml;

	using Apache.Avalon.Framework;

	internal enum LoggerType
	{
		Console,
		Null,
		Log4net,
		Diagnostics
	}

	/// <summary>
	/// Summary description for LoggerConfiguration.
	/// </summary>
	public class LoggerConfiguration
	{
		private static readonly String LOG_LEVEL = "level";

		private static readonly String DEFAULT_LOG_LEVEL = "info";

		private static readonly String LOG_TYPE = "type";
		
		private static readonly String DEFAULT_LOG_TYPE = "console";

		private static readonly String DEFAULT_NAME = "Castle.Microkernel";

		private LoggerLevel m_logLevel;

		private ILogger m_logType;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="section"></param>
		public LoggerConfiguration(XmlNode section)
		{
			IConfiguration config = 
				DefaultConfigurationSerializer.Deserialize( section );

			IConfiguration logLevelConfig = config.GetChild( LOG_LEVEL, true );
			IConfiguration logTypeConfig = config.GetChild( LOG_TYPE, true );

			String level = (String) logLevelConfig.GetValue( typeof(String), DEFAULT_LOG_LEVEL );
			String type = (String) logTypeConfig.GetValue( typeof(String), DEFAULT_LOG_TYPE );

			m_logLevel = ConvertToLogLevel( level );
			m_logType = BuildLogType( type );
		}

		private LoggerLevel ConvertToLogLevel( String level )
		{
			try
			{
				return (LoggerLevel) Enum.Parse( typeof(LoggerLevel), level, true );
			}
			catch(Exception ex)
			{
				throw new ConfigurationException(
					"Could not convert specified logger level to a known one", ex);
			}
		}

		private ILogger BuildLogType( String type )
		{
			LoggerType loggerType = LoggerType.Console;

			try
			{
				loggerType = (LoggerType) Enum.Parse( typeof(LoggerType), type, true );
			}
			catch(Exception ex)
			{
				throw new ConfigurationException(
					"Could not convert specified logger type to a known one", ex);
			}

			switch(loggerType)
			{
				case LoggerType.Log4net: // Not sure if its good to depend on log4net
				case LoggerType.Console:
					return new ConsoleLogger(DEFAULT_NAME, m_logLevel);
				case LoggerType.Diagnostics:
					return new DiagnosticsLogger(DEFAULT_NAME);
				//case LoggerType.Log4net:
					// ILog log = LogManager.GetLogger(DEFAULT_NAME);
					// m_rootLogger = new Log4netLogger(log, DEFAULT_NAME);
				//	break;
				case LoggerType.Null:
					return new NullLogger();
			}

			return null;
		}

		public ILogger RootLogger
		{
			get
			{
				return m_logType;
			}
		}

		public LoggerLevel LoggerLevel
		{
			get
			{
				return m_logLevel;
			}
		}
	}
}
