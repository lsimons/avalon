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

namespace Apache.Avalon.Castle.MicroKernel.Logger.Default
{
	using System;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for LoggerManager.
	/// </summary>
	public class LoggerManager : ILoggerManager
	{
		public LoggerManager()
		{
		}

		#region ILoggerManager Members

		public ILogger CreateLogger(String loggerName, String implementationName, AvalonLoggerAttribute loggerAtt)
		{
			return null;
		}

		#endregion
	}
}
