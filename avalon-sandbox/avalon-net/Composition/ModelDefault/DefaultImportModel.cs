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
	
	/// <summary> Default implementation of a the context entry import model.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultImportModel : DefaultEntryModel
	{
		//==============================================================
		// static
		//==============================================================
		
		//==============================================================
		// immutable state
		//==============================================================
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_directive '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private ImportDirective m_directive;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_descriptor '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private EntryDescriptor m_descriptor;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IComponentContext m_context;
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_map '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private System.Collections.IDictionary m_map;
		
		//==============================================================
		// mutable state
		//==============================================================
		
		private System.Object m_value;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> Creation of a new context entry import model.
		/// 
		/// </summary>
		/// <param name="descriptor">the context entry descriptor
		/// </param>
		/// <param name="directive">the context entry directive
		/// </param>
		/// <param name="context">the containment context
		/// </param>
		public DefaultImportModel(EntryDescriptor descriptor, ImportDirective directive, IComponentContext context, System.Collections.IDictionary map):base(descriptor)
		{
			if (directive == null)
			{
				throw new System.ArgumentNullException("directive");
			}
			if (context == null)
			{
				throw new System.ArgumentNullException("context");
			}
			m_descriptor = descriptor;
			m_directive = directive;
			m_context = context;
			m_map = map;
		}
		
		//==============================================================
		// IContainmentContext
		//==============================================================
		
		/// <summary> Return the context entry value.
		/// 
		/// </summary>
		/// <returns> the context entry value
		/// </returns>
		public override System.Object Value
		{
			get
			{
				if (m_value != null)
				{
					return m_value;
				}
			
				String target = m_descriptor.Key;
				String key = m_directive.ImportKey;
			
				System.Object object_Renamed = null;
				try
				{
					object_Renamed = m_context.Resolve(key);
				}
				catch (ContextException )
				{
					//UPGRADE_TODO: Method 'java.util.Map.get' was converted to 'System.Collections.IDictionary.Item' which has a different behavior. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1073_javautilMapget_javalangObject"'
					object_Renamed = m_map[key];
					if (object_Renamed == null)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String error = "todo: error message"; //"import.missing-entry.error", key, target);
						throw new ModelException(error);
					}
				}
			
				//
				// validate the value before returning it
				// (should move this code up to the context model)
				//
			
				System.Type type = m_descriptor.Type;
			
				if (!(type.IsAssignableFrom(object_Renamed.GetType())))
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String error = "todo: error message"; //"import.type-conflict.error", key, typename, target);
					throw new ModelException(error);
				}
			
				if (!m_descriptor.Volatile)
				{
					m_value = object_Renamed;
				}
			
				return object_Renamed;
			}
		}
	}
}