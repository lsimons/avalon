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

namespace Apache.Avalon.Castle.Logger.Config
{
	using System;
	using System.Xml;

	/// <summary>
	/// 
	/// </summary>
	internal enum LoggerType
	{
		Null,
		Console,
		Log4net
	}

	/// <summary>
	/// Summary description for LoggerConfig.
	/// </summary>
	internal class LoggerConfig
	{
		private LoggerType Logger = LoggerType.Console;

		public LoggerConfig(LoggerConfig parent, XmlNode section)
		{
			ParseLoggerEntry(section);
		}

		#region Parsing

		private void ParseLoggerEntry(XmlNode loggerNode)
		{
			if (loggerNode != null && loggerNode.InnerText != String.Empty)
			{
				Logger = (LoggerType) Enum.Parse( typeof(LoggerType), loggerNode.InnerText, true );
			}
		}

		#endregion

		public LoggerType LoggerType
		{
			get
			{
				return this.Logger;
			}
		}
	}
}
