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

namespace Apache.Avalon.Castle.MicroKernel.Model.Default
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Castle.MicroKernel.Model;

	/// <summary>
	/// Summary description for DefaultComponentModel.
	/// </summary>
	public class DefaultComponentModel : IComponentModel
	{
		private Type m_service;
		private String m_name;
		private Lifestyle m_lifestyle;
		private ILogger m_logger;
		private IConfiguration m_config;
		private IContext m_context;
		private IDependencyModel[] m_dependencies;
		private IConstructionModel m_constructionModel;

		public DefaultComponentModel(
			String name,
			Type service,
			Lifestyle lifestyle, 
			ILogger logger, 
			IConfiguration configuration, 
			IContext context, 
			IDependencyModel[] dependencies, 
			IConstructionModel constructionModel)
		{
			AssertUtil.ArgumentNotNull( name, "name" );
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( logger, "logger" );
			AssertUtil.ArgumentNotNull( configuration, "configuration" );
			AssertUtil.ArgumentNotNull( context, "context" );
			AssertUtil.ArgumentNotNull( dependencies, "dependencies" );
			AssertUtil.ArgumentNotNull( constructionModel, "constructionModel" );

			m_name = name;
			m_service = service;
			m_lifestyle = lifestyle;
			m_logger = logger;
			m_config = configuration;
			m_context = context;
			m_dependencies = dependencies;
			m_constructionModel = constructionModel;
		}

		#region IComponentModel Members

		public String Name
		{
			get
			{
				return m_name;
			}
		}

		public Type Service
		{
			get
			{
				return m_service;
			}
		}

		public Lifestyle SupportedLifestyle
		{
			get
			{
				return m_lifestyle;
			}
		}

		public ILogger Logger
		{
			get
			{
				return m_logger;
			}
			set
			{
				m_logger = value;
			}
		}

		public IConfiguration Configuration
		{
			get
			{
				return m_config;
			}
			set
			{
				m_config = value;
			}
		}

		public IContext Context
		{
			get
			{
				return m_context;
			}
		}

		public IDependencyModel[] Dependencies
		{
			get
			{
				return m_dependencies;
			}
		}

		public IConstructionModel ConstructionModel
		{
			get
			{
				return m_constructionModel;
			}
		}

		#endregion
	}
}
