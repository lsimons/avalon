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
	using Apache.Avalon.Meta;
	using Apache.Avalon.Repository;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Implementation of a system context that exposes a system wide set of parameters.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultTypeLoaderContext : Apache.Avalon.Framework.DefaultContext, ITypeLoaderContext
	{
		//==============================================================
		// immutable state
		//==============================================================
		
		/// <summary> The logging channel to apply to the classloader model.</summary>
		private ILogger m_logger;
		
		/// <summary> Option packages established through the chain of parent 
		/// models.
		/// </summary>
		// private OptionalPackage[] m_packages;
		
		/// <summary> The base directory for resolution of extension directories and 
		/// fileset directories.
		/// </summary>
		private System.IO.FileInfo m_base;
		
		/// <summary> The local jar repository.</summary>
		private IRepository m_repository;
		
		/// <summary> The optional extensions package manager.</summary>
		// private ExtensionManager m_manager;
		
		/// <summary> The parent type manager.</summary>
		private ITypeRepository m_types;
		
		/// <summary> The parent service manager.</summary>
		private IServiceRepository m_services;
		
		/// <summary> The classloader directive.</summary>
		private TypeLoaderDirective m_directive;
		
		/// <summary> Implied url to include in the classpath.</summary>
		private System.Uri[] m_implied;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a root classloader context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign to the classloader model
		/// </param>
		/// <param name="repository">a local repository
		/// </param>
		/// <param name="base">the system base directory
		/// </param>
		/// <param name="parent">the parent classloader
		/// </param>
		/// <param name="directive">the classloader directive
		/// </param>
		public DefaultTypeLoaderContext(ILogger logger, IRepository repository, 
			System.IO.FileInfo base_Renamed, TypeLoaderDirective directive):
			this(logger, repository, base_Renamed, null, null, directive, null)
		{
		}
		
		/// <summary> Creation of a new classloader context.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel to assign to the classloader model
		/// </param>
		/// <param name="repository">a local repository
		/// </param>
		/// <param name="base">the system base directory
		/// </param>
		/// <param name="parent">the parent classloader
		/// </param>
		/// <param name="packages">the set of optional packages established under 
		/// current classloader chain
		/// </param>
		/// <param name="manager">the optional extions package manager established 
		/// by the parent classloader
		/// </param>
		/// <param name="types">the parent type manager
		/// </param>
		/// <param name="services">the parent service manager
		/// </param>
		/// <param name="directive">the classloader directive
		/// </param>
		//UPGRADE_ISSUE: Class 'java.lang.ClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
		public DefaultTypeLoaderContext(ILogger logger, IRepository repository, 
			System.IO.FileInfo base_Renamed, ITypeRepository types, 
			IServiceRepository services, TypeLoaderDirective directive, System.Uri[] implied)
		{
			if (logger == null)
			{
				throw new System.ArgumentNullException("logger");
			}
			/*if (repository == null)
			{
				throw new System.ArgumentNullException("repository");
			}*/
			if (base_Renamed == null)
			{
				throw new System.ArgumentNullException("base");
			}
			if (directive == null)
			{
				throw new System.ArgumentNullException("directive");
			}
			
			m_logger = logger;
			m_repository = repository;
			m_base = base_Renamed;
			m_types = types;
			m_services = services;
			m_directive = directive;
			
			if (implied == null)
			{
				m_implied = new System.Uri[0];
			}
			else
			{
				m_implied = implied;
			}
		}
		
		//==============================================================
		// ITypeLoaderContext
		//==============================================================
		
		/// <summary> Return the system logging channel.
		/// 
		/// </summary>
		/// <returns> the system logging channel
		/// </returns>
		public virtual ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}
		
		/// <summary> Return the system context.
		/// 
		/// </summary>
		/// <returns> the system context
		/// </returns>
		public virtual IRepository Repository
		{
			get
			{
				return m_repository;
			}
		}
		
		/// <summary> Return the base directory from which relative library directives
		/// and fileset directory paths may be resolved.
		/// 
		/// </summary>
		/// <returns> the base directory
		/// </returns>
		public virtual System.IO.FileInfo BaseDirectory
		{
			get
			{
				return m_base;
			}
		}
		
		/// <summary> Return the containment classloader.
		/// 
		/// </summary>
		/// <returns> the classloader
		/// </returns>
		/*public virtual OptionalPackage[] getOptionalPackages()
		{
			return m_packages;
		}*/
		
		/// <summary> Return the extension manager established by the parent 
		/// classloader model.
		/// 
		/// </summary>
		/// <returns> the extension manager
		/// </returns>
		/*
		public virtual ExtensionManager getExtensionManager()
		{
			return m_manager;
		}*/
		
		/// <summary> Return the type repository established by the parent classloader.
		/// 
		/// </summary>
		/// <returns> the type repository
		/// </returns>
		public virtual ITypeRepository TypeRepository
		{
			get
			{
				return m_types;
			}
		}
		
		/// <summary> Return the service repository established by the parent classloader.
		/// 
		/// </summary>
		/// <returns> the service repository
		/// </returns>
		public virtual IServiceRepository ServiceRepository
		{
			get
			{
				return m_services;
			}
		}
		
		/// <summary> Return the classloader directive to be applied to the 
		/// classloader model.
		/// 
		/// </summary>
		/// <returns> the classloader directive
		/// </returns>
		public virtual TypeLoaderDirective TypeLoaderDirective
		{
			get
			{
				return m_directive;
			}
		}
		
		/// <summary> Return any implied urls to include in the classloader.
		/// 
		/// </summary>
		/// <returns> the implied urls
		/// </returns>
		public virtual System.Uri[] ImplicitURLs
		{
			get
			{
				return m_implied;
			}
		}
	}
}