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

namespace Apache.Avalon.Container.Logger
{
	using System;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for LoggerManager.
	/// </summary>
	public sealed class LoggerManager : ILogEnabled, IConfigurable, ILoggerManager
	{
		private ILogger m_logger;

		public LoggerManager()
		{
		}

		#region ILogEnabled Members

		public void EnableLogging(ILogger logger)
		{
			m_logger = logger;
		}

		#endregion

		#region IConfigurable Members

		public void Configure(IConfiguration config)
		{
			// TODO: Configure factories
		}

		#endregion

		#region ILoggerManager Members

		public ILogger DefaultLogger
		{
			get
			{
				// TODO: property DefaultLogger
				return m_logger;
			}
		}

		public ILogger this[String category]
		{
			get
			{
				// TODO:  Add LoggerManager.this getter implementation
				return m_logger.CreateChildLogger(category);
			}
		}

		#endregion
	}
}
