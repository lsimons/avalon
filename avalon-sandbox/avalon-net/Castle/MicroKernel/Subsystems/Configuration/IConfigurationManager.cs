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

namespace Apache.Avalon.Castle.MicroKernel.Subsystems.Configuration
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.MicroKernel.Model;

	/// <summary>
	/// Summary description for IConfigurationManager.
	/// </summary>
	public interface IConfigurationManager : IKernelSubsystem
	{
		/// <summary>
		/// Implementation should return a configuration for 
		/// the component.
		/// </summary>
		/// <param name="model"></param>
		/// <returns></returns>
		IConfiguration GetConfiguration( String componentName );
	}
}
