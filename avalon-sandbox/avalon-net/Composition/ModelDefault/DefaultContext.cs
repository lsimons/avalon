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
	
	/// <summary> <p>Default implementation of a context object.  The implementation
	/// maintains a mapping between context keys and context entry models.
	/// Requests for a context entry value are resolved through redirecting
	/// the request to an assigned model.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class DefaultContext : IContext
	{
		//==============================================================
		// static
		//==============================================================
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'REZ '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		//UPGRADE_NOTE: The initialization of  'REZ' was moved to static method 'Apache.Avalon.Composition.Model.Default.DefaultContext'. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1005"'
		// private static readonly Resources REZ;
		
		//==============================================================
		// immutable state
		//==============================================================
		
		//UPGRADE_NOTE: Final was removed from the declaration of 'm_context '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
		private IComponentContext m_context;
		
		//==============================================================
		// constructor
		//==============================================================
		
		/// <summary> <p>Creation of a new default context.</p>
		/// 
		/// </summary>
		/// <param name="context">the deployment context
		/// </param>
		public DefaultContext(IComponentContext context)
		{
			m_context = context;
		}
		
		//==============================================================
		// Context
		//==============================================================
		
		/// <summary> Return a context value relative to a key. If the context entry
		/// is unknown a {@link ContextException} containing the key as 
		/// as the exception message and a null cause will be thrown.  If 
		/// the contrext entry is recognized and a error occurs during 
		/// value resolvution a {@link ContextException} will be thrown 
		/// containing the causal exception.
		/// 
		/// </summary>
		/// <param name="key">the context entry key
		/// </param>
		/// <returns> the context entry value
		/// </returns>
		/// <exception cref=""> ContextException if the key is unknown or unresolvable
		/// </exception>
		public System.Object this [System.Object key]
		{
			get
			{
				try
				{
					return m_context.Resolve(key.ToString());
				}
				catch (ModelRuntimeException e)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					// String error = "todo: error message"; //"context.entry.model.error", key);
					String error = "Error obtaining entry";
					throw new ContextException(error, e);
				}
			}
		}
	}
}