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
		private Activation m_activation;
		private ILogger m_logger;
		private IConfiguration m_config;
		private IContext m_context;
		private IDependencyModel[] m_dependencies;
		private IConstructionModel m_constructionModel;

		protected DefaultComponentModel()
		{
			m_context = new DefaultContext();
		}

		public DefaultComponentModel(
			ComponentData data,
			Type service,
			ILogger logger,
			IConfiguration configuration,
			IConstructionModel constructionModel) : this()
		{
			AssertUtil.ArgumentNotNull(data, "data");
			AssertUtil.ArgumentNotNull(service, "service");
			AssertUtil.ArgumentNotNull(logger, "logger");
			AssertUtil.ArgumentNotNull(configuration, "configuration");
			AssertUtil.ArgumentNotNull(constructionModel, "constructionModel");

			m_name = data.Name;
			m_service = service;
			m_lifestyle = data.SupportedLifestyle;
            m_activation = data.ActivationPolicy;
            m_logger = logger;
			m_config = configuration;
			m_dependencies = data.DependencyModel;
			m_constructionModel = constructionModel;
		}

		#region IComponentModel Members

		public String Name
		{
			get { return m_name; }
		}

		public Type Service
		{
			get { return m_service; }
		}

		public Lifestyle SupportedLifestyle
		{
			get { return m_lifestyle; }
		}

		public Activation ActivationPolicy
		{
			get { return m_activation; }
		}

		public ILogger Logger
		{
			get { return m_logger; }
			set { m_logger = value; }
		}

		public IConfiguration Configuration
		{
			get { return m_config; }
			set { m_config = value; }
		}

		public IContext Context
		{
			get { return m_context; }
		}

		public IDependencyModel[] Dependencies
		{
			get { return m_dependencies; }
		}

		public IConstructionModel ConstructionModel
		{
			get { return m_constructionModel; }
		}

		#endregion
	}
}