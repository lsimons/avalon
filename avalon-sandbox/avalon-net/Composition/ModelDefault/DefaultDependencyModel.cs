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
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Default implementation of the deplendency model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.3 $ $Date: 2004/03/07 22:06:40 $
	/// </version>
	public class DefaultDependencyModel : DefaultDependent, IDependencyModel
	{
		//--------------------------------------------------------------
		// static
		//--------------------------------------------------------------
		
		//--------------------------------------------------------------
		// immutable state
		//--------------------------------------------------------------
		
		private DependencyDescriptor m_descriptor;
		
		private DependencyDirective m_directive;
		
		private String m_partition;
		
		private String m_name;
		
		private String m_source;
		
		//--------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------
		
		/// <summary> Creation of a new dependency model.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="partition">the partition
		/// </param>
		/// <param name="name">the name
		/// </param>
		/// <param name="descriptor">the dependency descriptor
		/// </param>
		/// <param name="directive">the dependency directive (possibly null)
		/// </param>
		public DefaultDependencyModel(ILogger logger, String partition, String name, DependencyDescriptor descriptor, DependencyDirective directive):base(logger)
		{
			
			if (descriptor == null)
				throw new System.ArgumentNullException("descriptor");
			
			m_descriptor = descriptor;
			m_directive = directive;
			m_partition = partition;
			m_name = name;
			
			//
			// a dependency directive is either declaring an explicitly
			// identified provider, or, it is delcaring 0 or more selection 
			// constraints - if its a an absolute source declaration then 
			// add it to the table to paths keyed by depedency key names
			//
			
			if (directive != null)
			{
				if ((System.Object) directive.Source != null)
				{
					m_source = resolvePath(partition, directive.Source);
					String message = "dependency.path.debug" + " " + m_source + " " + directive.Key;
					Logger.Debug(message);
				}
				else
				{
					m_source = null;
				}
			}
			else
			{
				m_source = null;
			}
		}
		
		//--------------------------------------------------------------
		// IDependencyModel
		//--------------------------------------------------------------
		
		/// <summary> Return the dependency descriptor.
		/// 
		/// </summary>
		/// <returns> the descriptor
		/// </returns>
		public virtual DependencyDescriptor Dependency
		{
			get
			{
				return m_descriptor;
			}
		}
		
		/// <summary> Return an explicit path to a supplier component.  
		/// If a dependency directive has been declared
		/// and the directive contains a source declaration, the value 
		/// returned is the result of parsing the source value relative 
		/// to the absolute address of the implementing component.
		/// </summary>
		/// <returns> the explicit path
		/// </returns>
		public virtual String Path
		{
			get
			{
				return m_source;
			}
		}
		
		/// <summary> Filter a set of candidate service descriptors and return the 
		/// set of acceptable service as a ordered sequence.
		/// 
		/// </summary>
		/// <param name="candidates">the set of candidate services for the dependency
		/// matching the supplied key
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		/// <exception cref=""> IllegalArgumentException if the key is unknown
		/// </exception>
		public virtual ServiceDescriptor[] Filter(ServiceDescriptor[] candidates)
		{
			if (m_directive != null)
			{
				if ((System.Object) m_directive.Source == null)
				{
					return Filter(m_directive, candidates);
				}
			}
			return candidates;
		}
		
		/// <summary> Filter a set of candidate service descriptors and return the 
		/// set of acceptable service as a ordered sequence.
		/// 
		/// </summary>
		/// <param name="directive">the dependency directive
		/// </param>
		/// <param name="services">the set of candidate services for the dependency
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		private ServiceDescriptor[] Filter(DependencyDirective directive, ServiceDescriptor[] services)
		{
			SelectionDirective[] filters = GetFilters(directive);
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			
			for (int i = 0; i < services.Length; i++)
			{
				ServiceDescriptor service = services[i];
				if (IsaCandidate(service, filters))
				{
					list.Add(service);
				}
			}
			
			ServiceDescriptor[] candidates = (ServiceDescriptor[]) list.ToArray( typeof(ServiceDescriptor) );
			
			//
			// TODO: include ranking of candidates
			//
			
			return candidates;
		}
		
		private bool IsaCandidate(ServiceDescriptor service, SelectionDirective[] filters)
		{
			for (int i = 0; i < filters.Length; i++)
			{
				SelectionDirective filter = filters[i];
				if (!IsaCandidate(service, filter))
				{
					return false;
				}
			}
			return true;
		}
		
		private bool IsaCandidate(ServiceDescriptor service, SelectionDirective filter)
		{
			String feature = filter.Feature;
			String value_Renamed = filter.Value;
			String criteria = filter.Criteria;
			
			if (criteria.Equals(SelectionDirective.EQUALS))
			{
				return value_Renamed.Equals(service.GetAttribute(feature));
			}
			else if (criteria.Equals(SelectionDirective.EXISTS))
			{
				return service.GetAttribute(feature) != null;
			}
			else if (criteria.Equals(SelectionDirective.INCLUDES))
			{
				String v = service.GetAttribute(feature);
				if ((System.Object) v != null)
				{
					return v.IndexOf(value_Renamed) > - 1;
				}
				else
				{
					return false;
				}
			}
			else
			{
				String error = "dependency.invalid-criteria.error" + " " + criteria + " " + feature;
				throw new System.ArgumentException(error);
			}
		}
		
		private String resolvePath(String partition, String path)
		{
			if (path.StartsWith("/"))
			{
				return path;
			}
			else if (path.StartsWith("../"))
			{
				String parent = GetParenPath(partition);
				return resolvePath(parent, path.Substring(3));
			}
			else if (path.StartsWith("./"))
			{
				return resolvePath(partition, path.Substring(2));
			}
			else
			{
				return partition + path;
			}
		}
		
		private String GetParenPath(String partition)
		{
			int n = partition.LastIndexOf("/");
			if (n > 0)
			{
				int index = partition.Substring(0, (n - 1) - (0)).LastIndexOf("/");
				if (index == 0)
				{
					return "/";
				}
				else
				{
					return partition.Substring(0, (index) - (0)) + "/";
				}
			}
			else
			{
				String error = "Illegal attempt to reference a containment context above the root context.";
				throw new System.ArgumentException(error);
			}
		}
		
		/// <summary> Return the required selection constraints.</summary>
		/// <param name="directive">the dependency directive
		/// </param>
		/// <returns> the set of required selection directives
		/// </returns>
		private SelectionDirective[] GetFilters(DependencyDirective directive)
		{
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			SelectionDirective[] selections = directive.SelectionDirectives;
			for (int i = 0; i < selections.Length; i++)
			{
				SelectionDirective selection = selections[i];
				if (selection.Required)
				{
					list.Add(selection);
				}
			}
			return (SelectionDirective[]) list.ToArray( typeof(SelectionDirective) );
		}
	}
}