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

namespace Apache.Avalon.Container
{
	using System;
	using System.Collections;
	using System.Configuration;

	using Apache.Avalon.Framework;
	using ConfigurationException = Apache.Avalon.Framework.ConfigurationException;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Data.Builder;
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Composition.Logging.Default;
	// using Apache.Avalon.Composition.Model;
	// using Apache.Avalon.Composition.Model.Default;
	using Apache.Avalon.Container.Configuration;

	/// <summary>
	/// This is the default implementation of <b>Avalon Container</b>. It 
	/// can be extended to meet your requirements.
	/// </summary>
	/// <remarks>
	/// <para>The container initialization is the most important phase you have
	/// to deal with. A 
	/// <see cref="Apache.Avalon.Container.Configuration.ContainerConfiguration"/>
	/// must be correctly loaded and passed on to DefaultContainer through its
	/// constructor or implicitly, through Configuration files.
	/// </para>
	/// </remarks>
	public class DefaultContainer : IInitializable
	{
		private static ContainmentProfileCreator CREATOR = new ContainmentProfileCreator();

		protected IConfiguration m_rootConfiguration;

		protected ILoggingManager m_loggingManager;

		protected ILogger m_logger;

		public DefaultContainer()
		{
			ContainerConfiguration containerConf = 
				(ContainerConfiguration)
				ConfigurationSettings.GetConfig( ContainerConfigurationSectionHandler.Section );
			
			m_rootConfiguration = containerConf.Configuration;
		}

		public DefaultContainer(ContainerConfiguration configuration)
		{
			m_rootConfiguration = configuration.Configuration;
		}

		#region IInitializable Members

		public void Initialize()
		{
			InitializeLogger();

			InitializeContainerApplication();
		}

		#endregion

		#region Logger

		private void InitializeLogger()
		{
			IConfiguration loggerConfig = m_rootConfiguration.GetChild( "logging", true );

			LoggingDescriptor loggingDescriptor = CreateLoggingDescriptor( loggerConfig );

			m_loggingManager = new DefaultLoggingManager();

			m_logger = m_loggingManager.GetLoggerForCategory( loggingDescriptor.Name );
		}

		private LoggingDescriptor CreateLoggingDescriptor( IConfiguration configuration )
		{
			String name = (String) configuration.GetAttribute( "name", "kernel" );

			CategoriesDirective categories = null;

			try
			{
				categories = CREATOR.GetCategoriesDirective( configuration, name );
			}
			catch(Exception e)
			{
				throw new ContainerException("Exception obtaining logging directive.", e);
			}

			ArrayList list = new ArrayList();
			ConfigurationCollection configs = configuration.GetChildren( "target" );
			foreach( IConfiguration conf in configs )
			{
				try
				{
					list.Add( CreateTargetDescriptor( conf ) );
				}
				catch( Exception e )
				{
					throw new ContainerException( "Invalid target descriptor.", e );
				}
			}

			TargetDescriptor[] targets = (TargetDescriptor[]) list.ToArray( typeof(TargetDescriptor) );

			//
			// return the logging descriptor
			//

			return new LoggingDescriptor(
				categories.Name, 
				categories.Priority, 
				categories.Target, 
				categories.Categories, 
				targets );
		}

		private TargetDescriptor CreateTargetDescriptor( IConfiguration config )
		{
			String name = (String) config.GetAttribute( "name", String.Empty );

			if( config.Children.Count == 0 )
			{
				throw new ConfigurationException(
					String.Format("Missing target provider element in '{0}'", config.Name) );
			}

			IConfiguration conf = config.Children[0];
			
			TargetProvider provider = null;
			
			if( conf.Name.Equals( "file" ) )
			{
				// TODO: FileTargetProvider not supported yet
				// throw new ConfigurationException(
				//		"FileTargetProvider not supported yet." );
				// provider = createFileTargetProvider( conf );
			}
			else
			{
				throw new ConfigurationException(
					String.Format( "Unrecognized provider: {0} in {1}.", conf.Name, config.Name ) );
			}

			return new TargetDescriptor( name, provider );
		}

		#endregion

		#region Application

		private void InitializeContainerApplication()
		{
			IConfiguration config = m_rootConfiguration.GetChild( "container", false );

			// IContainmentModel application = CreateContainmentModel( config );
			CreateContainmentModel( config );
		}

		private void CreateContainmentModel( IConfiguration config )
		{
			//m_logger.Info( "building application model" );

			ContainmentProfile profile = CREATOR.CreateContainmentProfile( config );

			// return null;

			/* m_loggingManager.AddCategories( profile.Categories );

			ITypeLoaderContext loaderContext = new DefaultTypeLoaderContext( m_logger, 
					null, baseDir, typeRepository, serviceRepository, typeLoaderDirec, assm);

			ITypeLoaderModel loaderModel = new DefaultTypeLoaderModel( loaderContext );*/
		}

		#endregion
	}
}
