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

	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Data;
	
	/// <summary> A utility class that assists in the location of a model relative
	/// a supplied path.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	class DefaultContainmentModelExportHelper
	{
		//-------------------------------------------------------------------
		// static
		//-------------------------------------------------------------------
		
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------
		
		private IContainmentContext m_context;
		private IContainmentModel m_model;
		
		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------
		
		public DefaultContainmentModelExportHelper(IContainmentContext context, IContainmentModel model)
		{
			m_context = context;
			m_model = model;
		}
		
		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------
		
		public virtual DefaultServiceModel[] createServiceExport()
		{
			
			ServiceDirective[] export = m_context.ContainmentProfile.ExportDirectives;
			DefaultServiceModel[] services = new DefaultServiceModel[export.Length];
			for (int i = 0; i < export.Length; i++)
			{
				ServiceDirective service = export[i];
				System.Type type = getServiceExportClass(service);
				IDeploymentModel provider = locateImplementionProvider(service);
				services[i] = new DefaultServiceModel(service, type, provider);
			}
			return services;
		}
		
		/// <summary> Return the class declared by a container service export declaration.</summary>
		/// <returns> the exported service interface class
		/// </returns>
		/// <exception cref=""> ModelException if the class cannot be resolved
		/// </exception>
		private System.Type getServiceExportClass(ServiceDirective service)
		{
			return service.Reference.Type;
		}
		
		/// <summary> Given a service directive declared by a container, locate a model 
		/// with this containment model to map as the provider.  If not model
		/// is explicity declared, the implementation will attempt to construct
		/// a new model based on packaged profiles and add the created model to
		/// the set of models within this container.
		/// 
		/// </summary>
		/// <param name="service">the service directive
		/// </param>
		/// <returns> the implementing deployment model
		/// </returns>
		/// <exception cref=""> ModelException if an implementation is not resolvable 
		/// </exception>
		private IDeploymentModel locateImplementionProvider(ServiceDirective service)
		{
			String path = service.Path;
			if (null != path)
			{
				IDeploymentModel provider = m_model.GetModel(path);
				
				if (null == provider)
				{
					//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
					//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
					String error = "Implemention provider path [" + path + "] for the exported service [" + service.Reference + "] in the containment model " + m_model + " does not reference a known model.";
					throw new ModelException(error);
				}
				else
				{
					return provider;
				}
			}
			else
			{
				//UPGRADE_NOTE: Final was removed from the declaration of 'dependency '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				DependencyDescriptor dependency = new DependencyDescriptor("export", service.Reference, null);
				
				//UPGRADE_NOTE: Final was removed from the declaration of 'repository '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				IModelRepository repository = m_context.ModelRepository;
				//UPGRADE_NOTE: Final was removed from the declaration of 'candidates '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				IDeploymentModel[] candidates = repository.Models;
				//UPGRADE_NOTE: Final was removed from the declaration of 'selector '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
				IModelSelector selector = new DefaultModelSelector();
				IDeploymentModel provider = selector.Select(candidates, dependency);
				if (null != provider)
				{
					return provider;
				}
				else
				{
					ITypeRepository repo = m_context.TypeLoaderModel.TypeRepository;
					DeploymentProfile[] profiles = repo.GetProfiles(dependency, false);

					if (profiles.Length != 0)
					{
						return m_model.AddModel(profiles[0]);
					}
					else
					{
						//UPGRADE_NOTE: Final was removed from the declaration of 'error '. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1003"'
						//UPGRADE_TODO: The equivalent in .NET for method 'java.lang.Object.toString' may return a different value. 'ms-help://MS.VSCC.2003/commoner/redir/redirect.htm?keyword="jlca1043"'
						String error = "Could not locate a provider for the exported service [" + dependency.Service + "] in the containment model " + m_model;
						throw new ModelException(error);
					}
				}
			}
		}
	}
}