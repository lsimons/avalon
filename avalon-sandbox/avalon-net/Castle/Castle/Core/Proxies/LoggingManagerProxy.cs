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

namespace Apache.Avalon.Castle.Core.Proxies
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Composition.Logging;

	/// <summary>
	/// Summary description for LoggingManagerProxy.
	/// </summary>
	public class LoggingManagerProxy : AbstractManagedObjectProxy, ILoggingManager
	{
		public LoggingManagerProxy( MServer server, ManagedObjectName name ) : base( server, name )
		{
		}

		#region ILoggingManager Members

		public void AddCategories(string path, Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			MXUtil.InvokeOn( server, target, "AddCategories", path, descriptor );
		}

		public void AddCategories(Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			MXUtil.InvokeOn( server, target, "AddCategories", descriptor );
		}

		public ILogger GetLoggerForCategory(string category)
		{
			return (ILogger) MXUtil.InvokeOn( server, target, "GetLoggerForCategory", category );
		}

		public ILogger GetLoggerForCategory(Apache.Avalon.Composition.Data.CategoryDirective category)
		{
			return (ILogger) MXUtil.InvokeOn( server, target, "GetLoggerForCategory", category );
		}

		public ILogger GetLoggerForCategory(string name, string target, string priority)
		{
			return (ILogger) MXUtil.InvokeOn( server, base.target, "GetLoggerForCategory", name, target, priority );
		}

		#endregion
	}
}
