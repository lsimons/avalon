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

namespace Apache.Avalon.Composition.Logging
{
	using System;
	
	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;

	/*public struct LoggingManager_Fields
	{
		/// <summary> Standard context key for the logging manager.</summary>
		public readonly static System.String KEY = "urn:assembly:logging";
		/// <summary> The default logging priority value.</summary>
		public readonly static System.String DEFAULT_PRIORITY = "INFO";
		/// <summary> The default logging target name.</summary>
		public readonly static System.String DEFAULT_TARGET = "default";
		/// <summary> The default logging format.</summary>
		public readonly static System.String DEFAULT_FORMAT = "[%7.7{priority}] (%{category}): %{message}\\n%{throwable}";
	}*/

	/// <summary> A <code>LoggerManager</code> that supports the management of a logging hierarchy.</summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	public interface ILoggingManager
	{
		/// <summary> Add a set of category entries using the supplied categories descriptor.</summary>
		/// <param name="descriptor">a set of category descriptors to be added under the path
		/// </param>
		void  AddCategories(CategoriesDirective descriptor);
		
		/// <summary> Add a set of category entries relative to the supplied base category
		/// path, using the supplied descriptor as the definition of subcategories.
		/// </summary>
		/// <param name="path">the category base path
		/// </param>
		/// <param name="descriptor">a set of category descriptors to be added under
		/// the base path
		/// </param>
		void  AddCategories(System.String path, CategoriesDirective descriptor);
		
		/// <summary> Create a logging channel configured with the supplied category path,
		/// priority and target.
		/// 
		/// </summary>
		/// <param name="name">logging category path
		/// </param>
		/// <param name="target">the logging target to assign the channel to
		/// </param>
		/// <param name="priority">the priority level to assign to the channel
		/// </param>
		/// <returns> the logging channel
		/// @throws Exception if an error occurs
		/// </returns>
		ILogger GetLoggerForCategory(System.String name, System.String target, System.String priority);
		
		/// <summary> Configure Logging channel based on the description supplied in a
		/// category descriptor.
		/// 
		/// </summary>
		/// <param name="category">defintion of the channel category, priority and target
		/// </param>
		/// <returns> the logging channel
		/// @throws Exception if an error occurs
		/// </returns>
		ILogger GetLoggerForCategory(CategoryDirective category);
		
		/// <summary> Return the Logger for the specified category.</summary>
		/// <param name="category">the category path
		/// </param>
		/// <returns> the logging channel
		/// </returns>
		ILogger GetLoggerForCategory(System.String category);
	}
}