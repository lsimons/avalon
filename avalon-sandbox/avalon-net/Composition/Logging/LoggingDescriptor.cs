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

	using Apache.Avalon.Composition.Data;
	
	
	/// <summary> Description of a top level logging system.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A logging element declares the top level defaults for priority and target name, a set of
	/// targets to which logging events shall be directed.
	/// The logging element declares the application wide default logging priority.
	/// A target element enables defintion of a logging file to which log entries will
	/// be directed.  The target name attribute is the name referenced by category elements
	/// defined within the loggers element. The priority attribute may container one of the values
	/// <code>DEBUG</code>, <code>INFO</code>, <code>WARN</code> or <code>ERROR</code>.
	/// The target must contain a single file element with the attribute <code>location</code>
	/// the corresponds to the name of the logging file.</p>
	/// 
	/// <pre>
	/// <font color="gray"><i>&lt;!--
	/// Definition of a logging system.
	/// --&gt;</i></font>
	/// 
	/// &lt;logging name="" priority="<font color="darkred">INFO</font>" target="<font color="darkred">kernel</font>"&gt;
	/// &lt;category name="logging" priority="<font color="darkred">WARN</font>"/&gt;
	/// &lt;target name="<font color="darkred">kernel</font>"&gt;
	/// &lt;file location="<font color="darkred">kernel.log</font>" /&gt;
	/// &lt;/target&gt;
	/// &lt;/logging&gt;
	/// </pre>
	/// 
	/// </summary>
	/// <seealso cref="TargetDescriptor">
	/// </seealso>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:02 $
	/// </version>
	public sealed class LoggingDescriptor : CategoriesDirective
	{
		/// <summary> The dependencies keyed by role name.</summary>
		private TargetDescriptor[] m_targets;
		
		/// <summary> Create a LoggingDescriptor instance.</summary>
		public LoggingDescriptor():this("", null, null, new CategoryDirective[0], new TargetDescriptor[0])
		{
		}
		
		/// <summary> 
		/// Create a LoggingDescriptor instance.
		/// </summary>
		/// <param name="root">the root logger category name
		/// </param>
		/// <param name="priority">the default logging priority
		/// </param>
		/// <param name="target">the default logging target
		/// </param>
		/// <param name="categories">the system categories
		/// </param>
		/// <param name="targets">the set of logging targets
		/// </param>
		public LoggingDescriptor(System.String root, System.String priority, System.String target, CategoryDirective[] categories, TargetDescriptor[] targets):base(root, priority, target, categories)
		{
			if (targets == null)
			{
				m_targets = new TargetDescriptor[0];
			}
			else
			{
				m_targets = targets;
			}
		}

		/// <summary> 
		/// Return the set of logging target descriptors.
		/// </summary>
		/// <returns> the target descriptors
		/// </returns>
		public TargetDescriptor[] TargetDescriptors
		{
			get
			{
				return m_targets;
			}
			
		}
	}
}