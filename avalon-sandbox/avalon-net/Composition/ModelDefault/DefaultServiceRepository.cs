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
	using Apache.Avalon.Composition.Model;
	
	/// <summary> A service repository provides support for the retrival
	/// of service defintions.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultServiceRepository : IServiceRepository
	{

		//==============================================================
		// immutable state
		//==============================================================
		
		/// <summary> The logging channel.</summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_logger '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ILogger m_logger;
		
		/// <summary> The parent service manager (may be null)</summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_parent '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IServiceRepository m_parent;
		
		/// <summary> List of service entries.</summary>
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_services '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		private System.Collections.IList m_services;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new root service manager.</summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="services">the list of available services
		/// </param>
		/// <exception cref=""> NullPointerException if the services list is null
		/// </exception>
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		internal DefaultServiceRepository(ILogger logger, System.Collections.IList services):this(logger, null, services)
		{
		}
		
		/// <summary> Creation of a new service manager.</summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="parent">the parent type manager
		/// </param>
		/// <param name="services">the list of available services
		/// </param>
		/// <exception cref=""> NullPointerException if the services list is null
		/// </exception>
		//UPGRADE_ISSUE: Class hierarchy differences between ''java.util.List'' and ''System.Collections.IList'' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
		internal DefaultServiceRepository(ILogger logger, IServiceRepository parent, System.Collections.IList services)
		{
			if (services == null)
			{
				throw new System.ArgumentNullException("services");
			}
			m_parent = parent;
			m_services = services;
			m_logger = logger;
		}
		
		//==============================================================
		// implemetation
		//==============================================================
		
		/// <summary> Locate a {@link Service} instances associated with the
		/// supplied typename and version. If a service defintion is not
		/// found locally, the implementation redirects the request to
		/// the parent service manager.
		/// 
		/// </summary>
		/// <param name="typename">the service class name
		/// </param>
		/// <param name="version">the service version
		/// </param>
		/// <returns> the service matching the supplied typename and version.
		/// </returns>
		/// <exception cref=""> UnknownServiceException if a matching service cannot be found
		/// </exception>
		public virtual Service GetService(Type type)
		{
			return GetService(new ReferenceDescriptor(type));
		}
		
		/// <summary> Locate a {@link Service} instances associated with the
		/// supplied referecne descriptor. If a service defintion is not
		/// found locally, the implementation redirects the request to
		/// the parent service manager.
		/// 
		/// </summary>
		/// <param name="reference">the reference descriptor
		/// </param>
		/// <returns> the service matching the supplied descriptor.
		/// </returns>
		/// <exception cref=""> UnknownServiceException if a matching service cannot be found
		/// </exception>
		public virtual Service GetService(ReferenceDescriptor reference)
		{
			Service service = GetLocalService(reference);
			if (service == null)
			{
				if (m_parent != null)
				{
					return m_parent.GetService(reference);
				}
				else
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "Unknown service defintion: " + reference;
					throw new ServiceUnknownException(error);
				}
			}
			return service;
		}
		
		protected virtual internal ILogger Logger
		{
			get
			{
				return m_logger;
			}
			
		}

		private Service GetLocalService(ReferenceDescriptor reference)
		{
			System.Collections.IEnumerator iterator = m_services.GetEnumerator();
			//UPGRADE_TODO: Method 'java.util.Iterator.hasNext' was converted to 'System.Collections.IEnumerator.MoveNext' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratorhasNext"'
			while (iterator.MoveNext())
			{
				//UPGRADE_TODO: Method 'java.util.Iterator.next' was converted to 'System.Collections.IEnumerator.Current' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilIteratornext"'
				/* Service service = (Service) iterator.Current;
				if (service.Equals(reference))
				{
					return service;
				}*/

				Service service = (Service) iterator.Current;
				if (service.Reference.Equals( reference ))
				{
					return service;
				}
			}
			return null;
		}
	}
}