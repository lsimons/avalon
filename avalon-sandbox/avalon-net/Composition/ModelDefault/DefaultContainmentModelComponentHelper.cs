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
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Logging;
	using Apache.Avalon.Meta;
	
	/// <summary> A utility class that handles creation of a component model context.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultContainmentModelComponentHelper
	{
		//-------------------------------------------------------------------
		// static
		//-------------------------------------------------------------------
		
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContainmentContext m_context;
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_model '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IContainmentModel m_model;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		/// <summary> Creation of a component context creation helper.</summary>
		/// <param name="context">the containment model context
		/// </param>
		/// <param name="model">the containment model
		/// </param>
		public DefaultContainmentModelComponentHelper(IContainmentContext context, IContainmentModel model)
		{
			if (context == null)
			{
				throw new System.ArgumentNullException("context");
			}
			if (model == null)
			{
				throw new System.ArgumentNullException("model");
			}
			m_context = context;
			m_model = model;
		}
		
		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------
		
		/// <summary> Creation of a new component model relative to a supplied profile.
		/// 
		/// </summary>
		/// <param name="profile">the component profile
		/// </param>
		/// <returns> the component model context
		/// </returns>
		public virtual IComponentContext CreateComponentContext(ComponentProfile profile)
		{
			if (null == profile)
			{
				throw new System.ArgumentNullException("profile");
			}
			
			ISystemContext system = m_context.SystemContext;
			//UPGRADE_NOTE: Final was removed from the declaration of 'name '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
			String name = profile.Name;
			//UPGRADE_NOTE: Final was removed from the declaration of 'partition '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
			String partition = m_model.Partition;
			ILoggingManager logging = system.LoggingManager;
			CategoriesDirective categories = profile.Categories;
			if (null != categories)
			{
				logging.AddCategories(partition, categories);
			}
			
			ILogger logger = logging.GetLoggerForCategory(partition + name);
			DependencyGraph graph = m_context.DependencyGraph;
			System.IO.FileInfo home = new System.IO.FileInfo(m_context.HomeDirectory.FullName + "\\" + name);
			System.IO.FileInfo temp = new System.IO.FileInfo(m_context.TempDirectory.FullName + "\\" + name);
			
			try
			{
				//UPGRADE_ISSUE: Method 'java.lang.ClassLoader.loadClass' was not converted. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1000_javalangClassLoader"'
				System.Type base_Renamed = Type.GetType( profile.Classname ); // classloader.loadClass(profile.Classname);
				TypeDescriptor type = m_model.TypeLoaderModel.TypeRepository.GetType(base_Renamed);
				
				return new DefaultComponentContext(logger, name, system, 
					graph, m_model, profile, type, base_Renamed, home, temp, partition);
			}
			//UPGRADE_NOTE: Exception 'java.lang.Throwable' was converted to 'System.Exception' which has different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1100"'
			catch (System.Exception e)
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String error = "todo: error message"; //"containment.deployment.create.error", m_model.Path, name);
				throw new ModelException(error, e);
			}
		}
	}
}