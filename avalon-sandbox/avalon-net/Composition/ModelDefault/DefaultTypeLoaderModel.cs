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
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> <p>Implementation of a classloader model within which a 
	/// repository, a base directory and a classloader directive 
	/// are associated together enabling the creation of a fully 
	/// qualified classpath.</p>
	/// 
	/// <p>The classpath established by this model implementation
	/// applies the following logic:</p>
	/// <ul>
	/// <li>establish an extensions manager relative to the 
	/// &lt;library/&gt> directives</li>
	/// <li>build an uqualifed classpath relative to the  
	/// &lt;classpath/&gt> directives</li>
	/// <li>resolve any optional jar file extension jar file
	/// entries based on the manifest declarations of 
	/// the unqualified classpath, together with recursive
	/// resolution of resolved optional extensions</li>
	/// <li>consolidate the generated classpath relative to 
	/// the optional extensions established by any parent
	/// classloader models</li>
	/// </ul>
	/// <p>
	/// Class dependecies include the Excalibur i18n, the assembly 
	/// repository package, the avalon framework and meta packages,
	/// and the extensions package.
	/// </p>
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultTypeLoaderModel : AbstractLogEnabled, ITypeLoaderModel
	{
		//==============================================================
		// state
		//==============================================================
		
		private ITypeLoaderContext m_context;
		
		private String[] m_classpath;
		
		private System.Uri[] m_urls;
		
		private DefaultTypeRepository m_types;
		
		private DefaultServiceRepository m_services;
		
		private ILogger m_local;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new classloader model.  The model associated a 
		/// repository, a base directory and a classloader directive 
		/// enabling the creation of a fully populated classpath.
		/// 
		/// </summary>
		/// <param name="context">the classloader context
		/// </param>
		public DefaultTypeLoaderModel(ITypeLoaderContext context)
		{
			if (context == null)
			{
				throw new System.ArgumentNullException("context");
			}
			
			m_context = context;
			EnableLogging(context.Logger);
			m_local = Logger.CreateChildLogger("classloader");
			/*
			if (Logger.IsDebugEnabled)
			{
				LocalLogger.debug("base: " + StringHelper.toString(context.BaseDirectory));
			}*/
			System.IO.FileInfo base_Renamed = context.BaseDirectory;
			IRepository repository = context.Repository;
			TypeLoaderDirective directive = context.TypeLoaderDirective;
			// ExtensionManager manager = context.ExtensionManager();
			System.Uri[] implicit_Renamed = context.ImplicitURLs;
			
			try
			{
				/*
				if (manager != null)
				{
					DefaultExtensionManager local = new DefaultExtensionManager(directive.Library.getOptionalExtensionDirectories(base_Renamed));
					m_extension = new DelegatingExtensionManager(new ExtensionManager[]{manager, local});
				}
				else
				{
					m_extension = new DefaultExtensionManager(directive.Library.getOptionalExtensionDirectories(base_Renamed));
				}
				
				m_manager = new PackageManager(m_extension);
				*/
				m_classpath = createClassPath(base_Renamed, repository, directive, implicit_Renamed);
				// m_permissions = createPermissions(directive.GrantDirective);
				// if (LocalLogger.IsDebugEnabled)
				// {
					// String str = "classpath: " + StringHelper.toString(m_classpath);
					// LocalLogger.debug(str);
				// }
				
				// m_packages = buildOptionalPackages(m_classpath, context.getOptionalPackages());
				m_urls = buildQualifiedClassPath();
				//UPGRADE_ISSUE: Constructor 'java.net.URLClassLoader.URLClassLoader' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000"'
				// m_classLoader = new URLClassLoader(m_urls, context.getClassLoader());
				
				//
				// scan the classpath for component type and service
				// definitions
				//
				
				System.Collections.ArrayList types = new System.Collections.ArrayList();
				System.Collections.ArrayList services = new System.Collections.ArrayList();
				ILogger scannerLogger = LocalLogger.CreateChildLogger("scanner");
				// // Scanner scanner = new Scanner(scannerLogger);
				// // scanner.Scan(m_urls, types, services);
				
				//
				// create the repository supporting type and service lookup
				//
				
				ILogger typeLogger = LocalLogger.CreateChildLogger("types");
				m_types = new DefaultTypeRepository(typeLogger, context.TypeRepository, types);
				ILogger serviceLogger = LocalLogger.CreateChildLogger("services");
				m_services = new DefaultServiceRepository(serviceLogger, context.ServiceRepository, services);
			}
			catch (System.Exception e)
			{
				String error = "Could not create classloader.";
				throw new ModelException(error, e);
			}
		}
		
		//==============================================================
		// ITypeLoaderModel
		//==============================================================
		
		private String[] ClassPath
		{
			get
			{
				return m_classpath;
			}
			
		}
		private ILogger LocalLogger
		{
			get
			{
				return m_local;
			}
			
		}

		/// <summary> Creation of a classloader model using this model as the 
		/// relative parent.
		/// 
		/// </summary>
		/// <param name="logger">the loggiong channel
		/// </param>
		/// <param name="profile">the profile directive
		/// </param>
		/// <param name="implied">a sequence of implied urls
		/// </param>
		/// <returns> a new classloader context
		/// </returns>
		public virtual ITypeLoaderModel CreateTypeLoaderModel(ILogger logger, ContainmentProfile profile, System.Uri[] implied)
		{
			ITypeLoaderContext context = CreateChildContext(logger, profile, implied);
			return new DefaultTypeLoaderModel(context);
		}
		
		/// <summary> Return the type repository managed by this containment
		/// context.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		public virtual ITypeRepository TypeRepository
		{
			get
			{
				return m_types;
			}
		}
		
		/// <summary> Return the classloader model service repository.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		public virtual IServiceRepository ServiceRepository
		{
			get
			{
				return m_services;
			}
		}
		

		/// <summary> Return the fully qualified classpath including extension jar files
		/// resolved relative to the classpath directives in the meta-data
		/// and any parent classloader models.
		/// 
		/// </summary>
		/// <returns> an array of URL representing the complete classpath 
		/// </returns>
		public virtual System.Uri[] QualifiedClassPath
		{
			get
			{
				return m_urls;
			}
		}
		
		//==============================================================
		// private implementation
		//==============================================================
		
		/// <summary> Creation of a classloader context using this model as the 
		/// relative parent.
		/// 
		/// </summary>
		/// <param name="logger">the loggiong channel
		/// </param>
		/// <param name="profile">the profile directive
		/// </param>
		/// <param name="implied">a sequence of implied urls
		/// </param>
		/// <returns> a new classloader context
		/// </returns>
		private ITypeLoaderContext CreateChildContext(ILogger logger, ContainmentProfile profile, System.Uri[] implied)
		{
			IRepository repository = m_context.Repository;
			System.IO.FileInfo base_Renamed = m_context.BaseDirectory;
			// OptionalPackage[] packages = getOptionalPackages();
			TypeLoaderDirective directive = profile.TypeLoaderDirective;
			
			return new DefaultTypeLoaderContext(logger, repository, base_Renamed, 
				m_types, m_services, directive, implied);
		}
		
		/// <summary> Build the fully qulalified classpath including extension jar files
		/// resolved relative to the classpath directives in the meta-data.
		/// 
		/// </summary>
		/// <returns> an array of URL representing the complete classpath 
		/// </returns>
		private System.Uri[] buildQualifiedClassPath()
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			String[] classpath = ClassPath;
			for (int i = 0; i < classpath.Length; i++)
			{
				list.Add(new System.Uri(classpath[i]));
			}
			/* System.IO.FileInfo[] extensions = OptionalPackage.toFiles(getOptionalPackages());
			for (int i = 0; i < extensions.Length; i++)
			{
				list.Add(extensions[i].toURL());
			}
			*/
			return (System.Uri[]) list.ToArray( typeof(System.Uri) );
		}
		
		private String[] createClassPath(System.IO.FileInfo base_Renamed, IRepository repository, 
			TypeLoaderDirective directive, System.Uri[] implicit_Renamed)
		{
			System.Collections.ArrayList classpath = new System.Collections.ArrayList();
			
			if (implicit_Renamed.Length > 0)
			{
				if (Logger.IsDebugEnabled)
				{
					Logger.Debug("implicit entries: " + implicit_Renamed.Length);
				}
				
				for (int i = 0; i < implicit_Renamed.Length; i++)
				{
					classpath.Add(implicit_Renamed[i].ToString());
				}
			}
			
			System.IO.FileInfo[] files = expandFileSetDirectives(base_Renamed, directive.ClasspathDirective.Filesets);
			addToClassPath(classpath, files);
			
			if (files.Length > 0)
			{
				if (Logger.IsDebugEnabled)
				{
					Logger.Debug("included entries: " + files.Length);
				}
			}
			
			/*
			RepositoryDirective[] repositories = directive.ClasspathDirective.RepositoryDirectives;
			
			if (repositories.Length > 0)
			{
				if (Logger.IsDebugEnabled)
				{
					Logger.Debug("repository declarations: " + repositories.Length);
				}
			}
			
			for (int i = 0; i < repositories.Length; i++)
			{
				ResourceDirective[] resources = repositories[i].Resources;
				if (Logger.IsDebugEnabled)
				{
					Logger.Debug("repository " + i + " contains " + resources.Length + " entries.");
				}
				
				for (int j = 0; j < resources.Length; j++)
				{
					ResourceDirective resource = resources[j];
					String id = resource.Id;
					String version = resource.Version;
					if (resource.Type.Equals("jar"))
					{
						System.Uri url = repository.getResource(Artifact.createArtifact(resource.Group, resource.Name, resource.Version, resource.Type));
						classpath.Add(url.ToString());
					}
				}
			}
			*/
			
			return (String[]) classpath.ToArray( typeof(String) );
		}
		
		private void  addToClassPath(System.Collections.IList list, System.IO.FileInfo[] files)
		{
			for (int i = 0; i < files.Length; i++)
			{
				addToClassPath(list, files[i]);
			}
		}
		
		private void  addToClassPath(System.Collections.IList list, System.IO.FileInfo file)
		{
			// System.IO.FileInfo canonical = file.FullName();
			// String uri = canonical.toURL().ToString();
			list.Add(file);
		}
		
		/// <summary> Return an array of files corresponding to the expansion 
		/// of the filesets declared within the directive.
		/// 
		/// </summary>
		/// <param name="base">the base directory against which relative 
		/// file references will be resolved
		/// </param>
		/// <returns> the classpath
		/// </returns>
		public virtual System.IO.FileInfo[] expandFileSetDirectives(System.IO.FileInfo base_Renamed, 
			FilesetDirective[] filesets)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			
			for (int i = 0; i < filesets.Length; i++)
			{
				FilesetDirective fileset = filesets[i];
				System.IO.FileInfo anchor = getDirectory(base_Renamed, fileset.BaseDirectory);
				IncludeDirective[] includes = fileset.Includes;
				if (includes.Length > 0)
				{
					for (int j = 0; j < includes.Length; j++)
					{
						System.IO.FileInfo file = new System.IO.FileInfo(anchor.FullName + "\\" + includes[j].Path);
						list.Add(file);
					}
				}
				else
				{
					list.Add(anchor);
				}
			}
			
			return (System.IO.FileInfo[]) list.ToArray( typeof(System.IO.FileInfo) );
		}
		
		private System.IO.FileInfo getDirectory(System.IO.FileInfo base_Renamed, String path)
		{
			System.IO.FileInfo file = new System.IO.FileInfo(path);
			if ( System.IO.Path.IsPathRooted(file.FullName) )
			{
				return verifyDirectory(file);
			}
			file = new System.IO.FileInfo( System.IO.Path.Combine( base_Renamed.FullName, path ) );
			return verifyDirectory( file );
		}
		
		private System.IO.FileInfo verifyDirectory(System.IO.FileInfo dir)
		{
			if (System.IO.Directory.Exists(dir.FullName))
			{
				return dir;
			}
			
			String error = "Path does not correspond to a directory: " + dir;
			throw new System.IO.IOException(error);
		}

	}
}