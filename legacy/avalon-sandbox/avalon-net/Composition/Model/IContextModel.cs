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

namespace Apache.Avalon.Composition.Model
{
	using System;

	using Apache.Avalon.Framework;
	
	/// <summary> <p>Specification of a context model from which a 
	/// a fully qualifed context can be established.</p>
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:43 $
	/// </version>
	public struct IContextModel_Fields
	{
		/// <summary> The default context strategy interface class.</summary>
		public readonly static System.String DEFAULT_STRATEGY_CLASSNAME = "Apache.Avalon.Framework.IContextualizable, Apache.Avalon.Framework";
	}

	public interface IContextModel : IDependent
	{
		/// <summary> Return the context object for the component.
		/// 
		/// </summary>
		/// <returns> the context object
		/// </returns>
		IContext Context
		{
			get;
			
		}
		
		
		/// <summary> Return the class representing the contextualization 
		/// stage interface.
		/// 
		/// </summary>
		/// <returns> the contextualization interface class
		/// </returns>
		System.Type StrategyClass
		{
			get;
		}
	}
}