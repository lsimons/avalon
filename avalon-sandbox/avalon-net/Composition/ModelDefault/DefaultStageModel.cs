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
	using Apache.Avalon.Composition.Data;
	
	/// <summary> Default implementation of the stage model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultStageModel : DefaultDependent, IStageModel
	{
		//==============================================================
		// static
		//==============================================================
		
		//==============================================================
		// immutable state
		//==============================================================
		
		private StageDescriptor m_descriptor;
		
		private StageDirective m_directive;
		
		private String m_source;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new stage model.
		/// 
		/// </summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="partition">the partition name
		/// </param>
		/// <param name="descriptor">the stage descriptors
		/// </param>
		/// <param name="directive">the stage directive (possibly null)
		/// </param>
		public DefaultStageModel(ILogger logger, String partition, StageDescriptor descriptor, StageDirective directive):base(logger)
		{
			if (descriptor == null)
				throw new System.ArgumentNullException("descriptor");
			
			m_descriptor = descriptor;
			m_directive = directive;
			
			//
			// a stage directive is either declaring with an explicitly
			// identified provider, or, it is delcaring 0 or more selection 
			// constraints - if its an absolute source declaration then 
			// resolve it now
			//
			
			if (directive != null)
			{
				if ((System.Object) directive.Source != null)
				{
					m_source = ResolvePath(partition, directive.Source);
					String message = "todo: error message"; //"dependency.path.debug", m_source, directive.Key);
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
		
		//==============================================================
		// IStageModel
		//==============================================================
		
		/// <summary> Return the stage descriptor.
		/// 
		/// </summary>
		/// <returns> the descriptor
		/// </returns>
		public virtual StageDescriptor Stage
		{
			get
			{
				return m_descriptor;
			}
		}
		
		/// <summary> Return an explicit path to a supplier component.  
		/// If a stage directive has been declared
		/// and the directive contains a source declaration, the value 
		/// returned is the result of parsing the source value relative 
		/// to the absolute address of the dependent component.
		/// 
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
		
		/// <summary> Filter a set of candidate extension descriptors and return the 
		/// set of acceptable extensions as a ordered sequence.
		/// 
		/// </summary>
		/// <param name="candidates">the set of candidate stage providers
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		public virtual ExtensionDescriptor[] Filter(ExtensionDescriptor[] candidates)
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
		/// set of acceptable stage providers as a ordered sequence.
		/// 
		/// </summary>
		/// <param name="directive">the stage directive
		/// </param>
		/// <param name="providers">the set of candidate extension descriptors
		/// </param>
		/// <returns> the accepted candidates in ranked order
		/// </returns>
		private ExtensionDescriptor[] Filter(StageDirective directive, ExtensionDescriptor[] providers)
		{
			SelectionDirective[] filters = GetFilters(directive);
			//UPGRADE_ISSUE: Class hierarchy differences between 'java.util.ArrayList' and 'System.Collections.ArrayList' may cause compilation errors. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1186"'
			System.Collections.ArrayList list = new System.Collections.ArrayList();
			
			for (int i = 0; i < providers.Length; i++)
			{
				ExtensionDescriptor provider = providers[i];
				if (IsaCandidate(provider, filters))
				{
					list.Add(provider);
				}
			}
			
			ExtensionDescriptor[] candidates = (ExtensionDescriptor[]) list.ToArray( typeof(ExtensionDescriptor) );
			
			//
			// TODO: include ranking of candidates
			//
			
			return candidates;
		}
		
		private bool IsaCandidate(ExtensionDescriptor provider, SelectionDirective[] filters)
		{
			for (int i = 0; i < filters.Length; i++)
			{
				SelectionDirective filter = filters[i];
				if (!IsaCandidate(provider, filter))
				{
					return false;
				}
			}
			return true;
		}
		
		private bool IsaCandidate(ExtensionDescriptor provider, SelectionDirective filter)
		{
			String feature = filter.Feature;
			String value_Renamed = filter.Value;
			String criteria = filter.Criteria;
			
			if (criteria.Equals(SelectionDirective.EQUALS))
			{
				return value_Renamed.Equals(provider.GetAttribute(feature));
			}
			else if (criteria.Equals(SelectionDirective.EXISTS))
			{
				return provider.GetAttribute(feature) != null;
			}
			else if (criteria.Equals(SelectionDirective.INCLUDES))
			{
				String v = provider.GetAttribute(feature);
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
				String error = "todo: error message"; //"stage.invalid-criteria.error", criteria, feature);
				throw new System.ArgumentException(error);
			}
		}
		
		private String ResolvePath(String partition, String path)
		{
			if (path.StartsWith("/"))
			{
				return path;
			}
			else if (path.StartsWith("../"))
			{
				String parent = GetParenPath(partition);
				return ResolvePath(parent, path.Substring(3));
			}
			else if (path.StartsWith("./"))
			{
				return ResolvePath(partition, path.Substring(2));
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
					return partition.Substring(0, (index) - (0));
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
		private SelectionDirective[] GetFilters(StageDirective directive)
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