// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
//
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
//
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
//
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
//    must not be used to endorse or promote products derived from this  software
//    without  prior written permission. For written permission, please contact
//    apache@apache.org.
//
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
//
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

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
