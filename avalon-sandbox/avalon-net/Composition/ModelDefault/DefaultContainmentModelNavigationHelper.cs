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
	
	/// <summary> A utility class that assists in the location of a model relative
	/// a supplied path.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultContainmentModelNavigationHelper
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
		
		public DefaultContainmentModelNavigationHelper(IContainmentContext context, IContainmentModel model)
		{
			m_context = context;
			m_model = model;
		}
		
		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------
		
		public virtual IDeploymentModel GetModel(String path)
		{
			IContainmentModel parent = m_context.ParentContainmentModel;
			
			if (path.Equals(""))
			{
				return m_model;
			}
			else if (path.StartsWith("/"))
			{
				//
				// its a absolute reference that need to be handled by the 
				// root container
				//
				
				if (null != parent)
				{
					return parent.GetModel(path);
				}
				else
				{
					//
					// this is the root container thereforw the 
					// path can be transfored to a relative reference
					//
					
					return m_model.GetModel(path.Substring(1));
				}
			}
			else
			{
				//
				// its a relative reference in the form xxx/yyy/zzz
				// so if the path contains "/", then locate the token 
				// proceeding the "/" (i.e. xxx) and apply the remainder 
				// (i.e. yyy/zzz) as the path argument , otherwise, its 
				// a local reference that we can pull from the model 
				// repository
				//
				
				//UPGRADE_NOTE: Final was removed from the declaration of 'root '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				String root = getRootName(path);
				
				if (root.Equals(".."))
				{
					//
					// its a relative reference in the form "../xxx/yyy" 
					// in which case we simply redirect "xxx/yyy" to the 
					// parent container
					//
					
					if (null != parent)
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'remainder '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String remainder = getRemainder(root, path);
						return parent.GetModel(remainder);
					}
					else
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						String error = "Supplied path [" + path + "] references a container above the root container.";
						throw new System.ArgumentException(error);
					}
				}
				else if (root.Equals("."))
				{
					//
					// its a path with a redundant "./xxx/yyy" which is 
					// equivalent to "xxx/yyy"
					//
					
					//UPGRADE_NOTE: Final was removed from the declaration of 'remainder '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					String remainder = getRemainder(root, path);
					return m_model.GetModel(remainder);
				}
				else if (path.IndexOf("/") < 0)
				{
					// 
					// its a path in the form "xxx" so we can use this
					// to lookup and return a local child
					//
					
					return m_context.ModelRepository.GetModel(path);
				}
				else
				{
					//
					// locate the relative root container, and apply 
					// getModel to the container
					//
					
					IDeploymentModel model = m_context.ModelRepository.GetModel(root);
					if (model != null)
					{
						//
						// we have the sub-container so we can apply 
						// the relative path after subtracting the name of 
						// this container and the path seperator character
						//
						
						if (model is IContainmentModel)
						{
							IContainmentModel container = (IContainmentModel) model;
							//UPGRADE_NOTE: Final was removed from the declaration of 'remainder '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
							String remainder = getRemainder(root, path);
							return container.GetModel(remainder);
						}
						else
						{
							//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
							//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
							String error = "The path element [" + root + "] does not reference a containment model within [" + m_model + "].";
							throw new System.ArgumentException(error);
						}
					}
					else
					{
						//
						// path contains a token that does not map to 
						// known container
						//
						
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						String error = "Unable to locate a container with name [" + root + "] within the container [" + m_model + "].";
						throw new System.ArgumentException(error);
					}
				}
			}
		}
		
		private String getRootName(String path)
		{
			int n = path.IndexOf("/");
			if (n < 0)
			{
				return path;
			}
			else
			{
				return path.Substring(0, (n) - (0));
			}
		}
		
		private String getRemainder(String name, String path)
		{
			return path.Substring(name.Length + 1);
		}
	}
}