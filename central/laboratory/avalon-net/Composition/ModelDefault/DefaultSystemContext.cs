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
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> Implementation of a system context that exposes a system wide set of parameters.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.2 $ $Date: 2004/02/29 18:07:17 $
	/// </version>
	public class DefaultSystemContext : Apache.Avalon.Framework.DefaultContext, ISystemContext
	{
		//--------------------------------------------------------------
		// static
		//--------------------------------------------------------------
		
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		private System.IO.FileInfo m_base;
		
		private System.IO.FileInfo m_home;
		
		private System.IO.FileInfo m_temp;
		
		private IRepository m_repository;
		
		private ILoggingManager m_logging;
		
		private ILogger m_logger;
		
		private IModelFactory m_factory;
		
		private IRuntime m_runtime;
		
		private long m_timeout;
		
		private bool m_secure;
		
		//--------------------------------------------------------------
		// mutable state
		//--------------------------------------------------------------
		
		private bool m_trace;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of a new system context.
		/// 
		/// </summary>
		/// <param name="context">a repository initial context
		/// </param>
		/// <param name="artifact">an artifact identifying the default runtime
		/// </param>
		/// <param name="logging">the logging manager
		/// </param>
		/// <param name="base">the base directory from which relative references 
		/// within a classpath or library directive shall be resolved
		/// </param>
		/// <param name="home">the home directory
		/// </param>
		/// <param name="temp">the temp directory
		/// </param>
		/// <param name="repository">the application repository to be used when resolving 
		/// resource directives
		/// </param>
		/// <param name="category">the kernel logging category name
		/// </param>
		/// <param name="trace">flag indicating if internal logging is enabled
		/// </param>
		/// <param name="timeout">a system wide default deployment timeout
		/// </param>
		/*public DefaultSystemContext(Artifact artifact, ILoggingManager logging, 
			System.IO.FileInfo base_Renamed, System.IO.FileInfo home, 
			System.IO.FileInfo temp, IRepository repository, 
			String category, bool trace, long timeout, 
			bool secure) : 
			this(artifact, null, logging, base_Renamed, home, temp, 
			repository, category, trace, timeout, secure)
		{
		}*/
		
		/// <summary> Creation of a new system context.
		/// 
		/// </summary>
		/// <param name="runtime">the runtime instance
		/// </param>
		/// <param name="logging">the logging manager
		/// </param>
		/// <param name="base">the base directory from which relative references 
		/// within a classpath or library directive shall be resolved
		/// </param>
		/// <param name="home">the home directory
		/// </param>
		/// <param name="temp">the temp directory
		/// </param>
		/// <param name="repository">the application repository to be used when resolving 
		/// resource directives
		/// </param>
		/// <param name="category">the kernel logging category name
		/// </param>
		/// <param name="trace">flag indicating if internal logging is enabled
		/// </param>
		/// <param name="timeout">a system wide default deployment timeout
		/// </param>
		/*public DefaultSystemContext(IRuntime runtime, ILoggingManager logging, 
			System.IO.FileInfo base_Renamed, System.IO.FileInfo home, System.IO.FileInfo temp, 
			IRepository repository, String category, bool trace, long timeout, bool secure) : 
			this(runtime, logging, base_Renamed, home, temp, repository, category, trace, timeout, secure)
		{
		}*/
		
		/// <summary> Creation of a new system context.
		/// 
		/// </summary>
		/// <param name="context">the repository intial context
		/// </param>
		/// <param name="runtime">the runtime instance
		/// </param>
		/// <param name="runtime">the runtime class
		/// </param>
		/// <param name="logging">the logging manager
		/// </param>
		/// <param name="base">the base directory from which relative references 
		/// within a classpath or library directive shall be resolved
		/// </param>
		/// <param name="home">the home directory
		/// </param>
		/// <param name="temp">the temp directory
		/// </param>
		/// <param name="repository">the application repository to be used when resolving 
		/// resource directives
		/// </param>
		/// <param name="category">the kernel logging category name
		/// </param>
		/// <param name="trace">flag indicating if internal logging is enabled
		/// </param>
		/// <param name="timeout">a system wide default deployment timeout
		/// </param>
		public DefaultSystemContext(IRuntime runtime,  
			ILoggingManager logging, System.IO.FileInfo base_Renamed, System.IO.FileInfo home, 
			System.IO.FileInfo temp, IRepository repository, String category, bool trace, 
			long timeout, bool secure)
		{
			if (base_Renamed == null)
			{
				throw new System.ArgumentNullException("base");
			}
			/*
			if (repository == null)
			{
				throw new System.ArgumentNullException("repository");
			}*/
			if (logging == null)
			{
				throw new System.ArgumentNullException("logger");
			}
			if (!System.IO.Directory.Exists(base_Renamed.FullName))
			{
				String error = "system.error.base-not-a-directory"  + " " +  base_Renamed.FullName;
				throw new System.ArgumentException(error);
			}
			
			m_base = base_Renamed;
			m_home = home;
			m_temp = temp;
			m_trace = trace;
			m_repository = repository;
			m_logging = logging;
			m_timeout = timeout;
			m_secure = secure;
			m_runtime = runtime;
			
			m_logger = m_logging.GetLoggerForCategory(category);
			m_factory = new StandardModelFactory(this);
		}
		
		//--------------------------------------------------------------
		// ISystemContext
		//--------------------------------------------------------------
		
		/// <summary> Return the model factory.
		/// 
		/// </summary>
		/// <returns> the factory
		/// </returns>
		public virtual IModelFactory ModelFactory
		{
			get
			{
				return m_factory;
			}
		}
		
		/// <summary> Return the base directory from which relative classloader 
		/// references may be resolved.
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
		
		/// <summary> Return the working directory from which containers may 
		/// establish persistent content.
		/// 
		/// </summary>
		/// <returns> the working directory
		/// </returns>
		public virtual System.IO.FileInfo HomeDirectory
		{
			get
			{
				return m_home;
			}
		}
		
		/// <summary> Return the temporary directory from which a container 
		/// may use to establish a transient content directory. 
		/// 
		/// </summary>
		/// <returns> the temporary directory
		/// </returns>
		public virtual System.IO.FileInfo TempDirectory
		{
			get
			{
				return m_temp;
			}
		}
		
		/// <summary> Return the system wide repository from which resource 
		/// directives can be resolved.
		/// 
		/// </summary>
		/// <returns> the repository
		/// </returns>
		public virtual IRepository Repository
		{
			get
			{
				return m_repository;
			}
		}
		
		/// <summary> Return the system trace flag.
		/// 
		/// </summary>
		/// <returns> the trace flag
		/// </returns>
		public virtual bool IsTraceEnabled
		{
			get
			{
				return m_trace;
			}
		}
		
		/// <summary> Set the system trace flag.
		/// 
		/// </summary>
		/// <param name="trace">the trace flag
		/// </param>
		public virtual void SetTraceEnabled(bool trace)
		{
			m_trace = trace;
		}
		
		/// <summary> Return the logging manager.
		/// 
		/// </summary>
		/// <returns> the logging manager.
		/// </returns>
		public virtual ILoggingManager LoggingManager
		{
			get
			{
				return m_logging;
			}
		}
		
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
		
		/// <summary> Return the default deployment phase timeout value.</summary>
		/// <returns> the timeout value
		/// </returns>
		public virtual long DefaultDeploymentTimeout
		{
			get
			{
				return m_timeout;
			}
		}
		
		/// <summary> Return the enabled status of the code security policy.</summary>
		/// <returns> the code security enabled status
		/// </returns>
		/*public virtual bool isCodeSecurityEnabled()
		{
			return m_secure;
		}*/
		
		//------------------------------------------------------------------
		// runtime operations
		//------------------------------------------------------------------
		
		/// <summary> Request the commissioning of a runtime for a supplied deployment 
		/// model.
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		public virtual void Commission(IDeploymentModel model)
		{
			Runtime.Commission(model);
		}
		
		/// <summary> Request the decommissioning of a runtime for a supplied deployment 
		/// model.
		/// </summary>
		/// <param name="model">the deployment model 
		/// </param>
		/// <exception cref=""> Exception of a commissioning error occurs
		/// </exception>
		public virtual void Decommission(IDeploymentModel model)
		{
			Runtime.Decommission(model);
		}
		
		/// <summary> Request resolution of an object from the runtime.</summary>
		/// <param name="model">the deployment model
		/// </param>
		/// <exception cref=""> Exception if a deployment error occurs
		/// </exception>
		public virtual System.Object Resolve(IDeploymentModel model)
		{
			return Runtime.Resolve(model);
		}
		
		/// <summary> Request the release of an object from the runtime.</summary>
		/// <param name="model">the deployment model
		/// </param>
		/// <param name="instance">the object to release
		/// </param>
		/// <exception cref=""> Exception if a deployment error occurs
		/// </exception>
		public virtual void Release(IDeploymentModel model, System.Object instance)
		{
			Runtime.Release(model, instance);
		}

		/// <summary> Return the runtime factory.
		/// 
		/// </summary>
		/// <returns> the factory
		/// </returns>
		private IRuntime Runtime
		{
			get
			{
				return m_runtime;
			}
			
		}
		
		//------------------------------------------------------------------
		// runtime operations
		//------------------------------------------------------------------
		
		/// <summary> Get the runtime class referenced by the artifact.</summary>
		/// <param name="context">the repository initial context
		/// </param>
		/// <param name="artifact">the factory artifact
		/// </param>
		/// <returns> the Runtime class
		/// </returns>
		/*private System.Type GetRuntimeClass(InitialContext context, Artifact artifact)
		{
			if (null == artifact)
				return null;
			
			try
			{
				ClassLoader classloader = typeof(DefaultSystemContext).getClassLoader();
				Builder builder = context.newBuilder(classloader, artifact);
				return builder.getFactoryClass();
			}
			catch (System.Exception e)
			{
				String error = "todo: error message"; //"system.error.load", artifact.toString());
				throw new SystemException(error, e);
			}
		}*/
		
		/// <summary> Build a runtime using a supplied class.
		/// 
		/// </summary>
		/// <param name="type">the log target factory class
		/// </param>
		/// <returns> a instance of the class
		/// </returns>
		/// <exception cref=""> SystemException if the class does not expose a public 
		/// constructor, or the constructor requires arguments that the 
		/// builder cannot resolve, or if a unexpected instantiation error 
		/// ooccurs
		/// </exception>
		/*
		public virtual Runtime buildRuntimeInstance(InitialContext context, System.Type type)
		{
			if (null == type)
				return null;
			
			System.Reflection.ConstructorInfo[] constructors = type.GetConstructors();
			if (constructors.Length < 1)
			{
				String error = "todo: error message"; //"system.error.runtime.no-constructor", type.FullName);
				throw new SystemException(error);
			}
			
			//
			// log target factories only have one constructor
			//
			
			System.Reflection.ConstructorInfo constructor = constructors[0];
			System.Type[] classes = constructor.GetParameters();
			System.Object[] args = new System.Object[classes.Length];
			for (int i = 0; i < classes.Length; i++)
			{
				System.Type c = classes[i];
				if (typeof(ISystemContext).IsAssignableFrom(c))
				{
					args[i] = this;
				}
				else if (typeof(InitialContext).IsAssignableFrom(c))
				{
					args[i] = context;
				}
				else
				{
					String error = "todo: error message"; //"system.error.unrecognized-runtime-parameter", c.FullName, type.FullName);
					throw new SystemException(error);
				}
			}
			
			//
			// instantiate the factory
			//
			
			return instantiateRuntime(constructor, args);
		}*/
		
		/// <summary> Instantiation of a runtime using a supplied constructor 
		/// and arguments.
		/// 
		/// </summary>
		/// <param name="constructor">the runtime constructor
		/// </param>
		/// <param name="args">the constructor arguments
		/// </param>
		/// <returns> the runtime instance
		/// </returns>
		/// <exception cref=""> SystemException if an instantiation error occurs
		/// </exception>
		/*
		private Runtime instantiateRuntime(System.Reflection.ConstructorInfo constructor, System.Object[] args)
		{
			System.Type type = constructor.DeclaringType;
			try
			{
				return (Runtime) constructor.newInstance(args);
			}
			catch (System.Exception e)
			{
				String error = "todo: error message"; //"system.error.runtime-instantiation", type.FullName);
				throw new SystemException(error, e);
			}
		}*/
	}
}