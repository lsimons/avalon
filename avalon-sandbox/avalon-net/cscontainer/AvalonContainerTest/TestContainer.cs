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

namespace Apache.Avalon.Container.Test
{
	using System;
	using System.Collections;
	using System.Security.Permissions;
	using System.IO;
	using System.Reflection;
	using NUnit.Framework;

	using Apache.Avalon.Container;
	using Apache.Avalon.Container.Configuration;
	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for TestContainer.
	/// </summary>
	public sealed class TestContainer : DefaultContainer
	{
		internal TestContainer(ContainerConfiguration config) : base(config)
		{
		}

		public static TestContainer CreateContainer()
		{
			String configFile = 
				AppDomain.CurrentDomain.SetupInformation.ConfigurationFile;

			Assertion.Assert(
				"Configuration file does not exists. " + configFile, 
				File.Exists(configFile));

			ContainerConfiguration config = new ContainerConfiguration(configFile);

			return new TestContainer(config);
		}

		/*
		public Vertex[] ShutDownOrder
		{
			get
			{
				return base.m_shutDownOrder;
			}
		}*/
	}
}
