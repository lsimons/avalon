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

namespace Apache.Avalon.Composition.Logging.Default
{
	using System;

	using Apache.Avalon.Composition.Data;

	/// <summary>
	/// Summary description for DefaultLoggingManager.
	/// </summary>
	public class DefaultLoggingManager : ILoggingManager
	{
		public DefaultLoggingManager()
		{
		}

		public DefaultLoggingManager(System.IO.FileInfo baseDir, LoggingDescriptor logging)
		{
		}

		#region ILoggingManager Members

		public void AddCategories(Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			// TODO:  Add DefaultLoggingManager.AddCategories implementation
		}

		void Apache.Avalon.Composition.Logging.ILoggingManager.AddCategories(String path, Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			// TODO:  Add DefaultLoggingManager.Apache.Avalon.Composition.Logging.ILoggingManager.AddCategories implementation
		}

		public Apache.Avalon.Framework.ILogger GetLoggerForCategory(String name, String target, String priority)
		{
			// TODO:  Add DefaultLoggingManager.GetLoggerForCategory implementation
			return null;
		}

		Apache.Avalon.Framework.ILogger Apache.Avalon.Composition.Logging.ILoggingManager.GetLoggerForCategory(Apache.Avalon.Composition.Data.CategoryDirective category)
		{
			// TODO:  Add DefaultLoggingManager.Apache.Avalon.Composition.Logging.ILoggingManager.GetLoggerForCategory implementation
			return null;
		}

		Apache.Avalon.Framework.ILogger Apache.Avalon.Composition.Logging.ILoggingManager.GetLoggerForCategory(String category)
		{
			// TODO:  Add DefaultLoggingManager.Apache.Avalon.Composition.Logging.ILoggingManager.GetLoggerForCategory implementation
			return null;
		}

		#endregion
	}
}
