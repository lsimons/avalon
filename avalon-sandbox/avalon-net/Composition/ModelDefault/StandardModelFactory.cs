/* 
* Copyright 2003-2004 The Apache Software Foundation
* Licensed  under the  Apache License,  Version 2.0  (the "License");
* you may not use  this file  except in  compliance with the License.
* You may obtain a copy of the License at 
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed  under the  License is distributed on an "AS IS" BASIS,
* WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
* implied.
* 
* See the License for the specific language governing permissions and
* limitations under the License.
*/

namespace Apache.Avalon.Composition.Model.Default
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Repository;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Data.Builder;
	
	/// <summary> A factory enabling the establishment of new composition model instances.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class StandardModelFactory : IModelFactory
	{
		
		//-------------------------------------------------------------------
		// static
		//-------------------------------------------------------------------
		
		private static readonly ContainmentProfileCreator CREATOR = new ContainmentProfileCreator();
		
		// private static readonly ContainmentProfileBuilder BUILDER = new ContainmentProfileBuilder();
		
		
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		private ISystemContext m_system;
		
		private ILogger m_logger;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		public StandardModelFactory(ISystemContext system)
		{
			if (system == null)
			{
				throw new System.ArgumentNullException("system");
			}
			m_system = system;
			m_logger = system.Logger;
		}
		
		//-------------------------------------------------------------------
		// ModelFactory
		//-------------------------------------------------------------------
		
		private ILogger Logger
		{
			get
			{
				return m_logger;
			}
			
		}

		/// <summary> Creation of a new root containment model using 
		/// a URL referring to a containment profile.
		/// 
		/// </summary>
		/// <param name="url">a composition profile source
		/// </param>
		/// <returns> the containment model
		/// </returns>
		public virtual IContainmentModel CreateRootContainmentModel(System.Uri url)
		{
			return null;

			//
			// START WORKAROUND
			// The code in the following if statement should not
			// not be needed, however, when attempting to load a 
			// url the referenes an XML source document we get a 
			// SAXParseException with the message "Content not 
			// allowed in prolog."  To get around this the if
			// statement forces loading via the XML creator.
			//
			
			/*
			if (url.ToString().EndsWith(".xml"))
			{
				try
				{
					DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
					IConfiguration config = builder.build(url.ToString());
					ContainmentProfile profile = CREATOR.createContainmentProfile(config);
					return createRootContainmentModel(profile);
				}
				catch (ModelException e)
				{
					throw e;
				}
				catch (System.Exception e)
				{
					String error = "Could not create model due to a build related error.";
					throw new ModelException(error, e);
				}
			}
			*/
			
			//
			// This should work but does not.
			//
			
			/*
			try
			{
				System.Net.HttpWebRequest connection = (System.Net.HttpWebRequest) System.Net.WebRequest.Create(url);
				System.IO.Stream stream = connection.GetResponse().GetResponseStream();
				ContainmentProfile profile = BUILDER.createContainmentProfile(stream);
				return createRootContainmentModel(profile);
			}
			catch (System.Exception e)
			{
				String error = "todo: error message"; //"factory.containment.create-url.error", url.ToString());
				throw new ModelException(error, e);
			}
			*/
		}
		
		/// <summary> Creation of a new root containment model using 
		/// a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the containment model
		/// </returns>
		public virtual IContainmentModel CreateRootContainmentModel(ContainmentProfile profile)
		{
			try
			{
				IContainmentContext context = CreateRootContainmentContext(profile);
				return CreateContainmentModel(context);
			}
			catch (System.Exception e)
			{
				String error = "factory.containment.create.error"  + " " +  profile.Name;
				throw new ModelException(error, e);
			}
		}
		
		/// <summary> Creation of a new nested deployment model.  This method is called
		/// by a container implementation when constructing model instances.  The 
		/// factory is identified by its implementation typename.
		/// 
		/// </summary>
		/// <param name="context">a potentially foreign deployment context
		/// </param>
		/// <returns> the deployment model
		/// </returns>
		public virtual IComponentModel CreateComponentModel(IComponentContext context)
		{
			return new DefaultComponentModel(context);
		}
		
		/// <summary> Creation of a new nested containment model.  This method is called
		/// by a container implementation when constructing model instances.  The 
		/// factory is identified by its implementation typename.
		/// 
		/// </summary>
		/// <param name="context">a potentially foreign containment context
		/// </param>
		/// <returns> the containment model
		/// </returns>
		public virtual IContainmentModel CreateContainmentModel(IContainmentContext context)
		{
			return new DefaultContainmentModel(context);
		}
		
		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------
		
		/// <summary> Creation of a new root containment context.
		/// 
		/// </summary>
		/// <param name="profile">a containment profile 
		/// </param>
		/// <returns> the containment context
		/// </returns>
		private IContainmentContext CreateRootContainmentContext(ContainmentProfile profile)
		{
			if (profile == null)
			{
				throw new System.ArgumentNullException("profile");
			}
			
			m_system.LoggingManager.AddCategories(profile.Categories);
			ILogger logger = m_system.LoggingManager.GetLoggerForCategory("");
			
			try
			{
				IRepository repository = m_system.Repository;
				System.IO.FileInfo base_Renamed = m_system.BaseDirectory;
				
				// ClassLoader root = m_system.getAPIClassLoader();
				TypeLoaderDirective classLoaderDirective = profile.TypeLoaderDirective;
				
				ITypeLoaderContext classLoaderContext = new DefaultTypeLoaderContext(logger, 
					repository, base_Renamed, classLoaderDirective);
				
				ITypeLoaderModel classLoaderModel = new DefaultTypeLoaderModel(classLoaderContext);
				
				return new DefaultContainmentContext(logger, m_system, classLoaderModel, null, null, profile);
			}
			catch (System.Exception e)
			{
				String error = "factory.containment.create.error" + " " +  profile.Name;
				throw new ModelException(error, e);
			}
		}
	}
}