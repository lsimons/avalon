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

namespace Apache.Avalon.Castle.Default.Logger
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.Logger;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Composition.Logging;

	/// <summary>
	/// Summary description for DefaultLoggerManager.
	/// </summary>
	[ManagedComponent]
	public class DefaultLoggerManager : ManagedService, MContributeLifecycle, ILoggingManager
	{
		public DefaultLoggerManager()
		{
		}

		#region MContributeLifecycle Members

		[ManagedOperation]
		public void RegisterForPhases(Apache.Avalon.Castle.Core.OrchestratorNotificationSystem notification)
		{
			notification.AddListener( base.ManagedObjectName, LifecyclePhase.EnableLogging );
		}

		[ManagedOperation]
		public void Perform(object target)
		{
		}

		#endregion

		#region ILoggingManager Members

		[ManagedOperation]
		public void AddCategories(string path, Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			// TODO:  Add DefaultLoggerManager.AddCategories implementation
		}

		[ManagedOperation]
		public void AddCategories(Apache.Avalon.Composition.Data.CategoriesDirective descriptor)
		{
			// TODO:  Add DefaultLoggerManager.Apache.Avalon.Composition.Logging.ILoggingManager.AddCategories implementation
		}

		[ManagedOperation]
		public ILogger GetLoggerForCategory(string category)
		{
			return LoggerFactory.GetLogger( category );
		}

		[ManagedOperation]
		public ILogger GetLoggerForCategory(Apache.Avalon.Composition.Data.CategoryDirective category)
		{
			return LoggerFactory.GetLogger( category.Name );
		}

		[ManagedOperation]
		public ILogger GetLoggerForCategory(string name, string target, string priority)
		{
			return LoggerFactory.GetLogger( target );
		}

		#endregion
	}
}
