// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Composition.Model.Default
{
	using System;
	using System.Reflection;
	using System.Collections;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Meta.Builder;
	using Apache.Avalon.Repository;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Composition.Model;
	
	/// <summary> A repository for services, types and profiles.
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/28 22:15:42 $
	/// </version>
	public class Scanner : AbstractLogEnabled
	{
		/// <summary> 
		/// The type builder.
		/// </summary>
		TypeDescriptorBuilder m_builder = new TypeDescriptorBuilder();

		/// <summary> 
		/// The service builder. NOTE: Not being used by now.
		/// </summary>
		ServiceBuilder m_serviceBuilder = new ServiceBuilder();

		/// <summary>
		/// Assemblies array to be scanned.
		/// </summary>
		Assembly[] m_assemblies;
		
		//===================================================================
		// constructor
		//===================================================================
		
		/// <summary> Creation of a new scanner.  
		/// The scanner is responsible for scanning suppied Assemblies for 
		/// service and types.
		/// </summary>
		/// <param name="logger">the logging channel
		/// </param>
		/// <param name="classloader">the classloader
		/// </param>
		public Scanner(ILogger logger, Assembly[] assemblies)
		{
			if (assemblies == null)
			{
				throw new ArgumentNullException("assemblies");
			}
			EnableLogging(logger);
			m_assemblies = assemblies;
		}
		
		/// <summary> Scan the supplied url for Service and Type defintions.</summary>
		/// <param name="urls">the URL array to scan
		/// </param>
		/// <param name="types">the map to populate with types as keys and 
		/// and packaged profiles as values
		/// </param>
		/// <param name="services">a list to be populated with service descriptors
		/// </param>
		public virtual void Scan(IList types, IList services)
		{
			foreach(Assembly item in m_assemblies)
			{
				ScanAssembly(item, types, services);
			}
		}
		
		/// <summary> Add a URL to the classpath.</summary>
		/// <param name="url">the URL to add to the repository
		/// </param>
		private void ScanAssembly(Assembly item, IList types, IList services)
		{
			if (Logger.IsDebugEnabled)
			{
				String message = "scanner.scanning";
				Logger.Debug(message);
			}
			
			foreach(Type type in item.GetExportedTypes())
			{
				ScanType( type, types, services );
			}
		}
		
		private void ScanType(Type targetType, IList types, IList services)
		{
			if (targetType.IsDefined( typeof(AvalonComponentAttribute), true ))
			{
				AddType(targetType, types);
			}
		}
		
		private void AddType(Type targetType, IList types)
		{
			TypeDescriptor descriptor = m_builder.CreateTypeDescriptor( targetType );

			try
			{
				VerifyType(descriptor, targetType);

				if (Logger.IsDebugEnabled)
				{
					String message = "type added " + descriptor.ToString(); 
					Logger.Debug(message);
				}

				types.Add(descriptor);
			}
			catch (System.ApplicationException)
			{
				if (Logger.IsWarnEnabled)
				{
					String warning = "Type verification failed for " + descriptor.ToString();
					Logger.Warn(warning);
				}
			}
			catch (System.Exception e)
			{
				if (Logger.IsWarnEnabled)
				{
					String warning = "Unexpected error in type verification: " + descriptor.ToString();
					Logger.Warn(warning, e);
				}
			}
		}
		
		/// <summary>
		/// Verify the intergrity of the supplied type.
		/// </summary>
		/// <param name="type">
		/// the type to verify
		/// </param>
		/// <param name="type">
		/// the implementation class
		/// </param>
		/// <exception cref=""> 
		/// Exception if an verification failure occurs
		/// </exception>
		private void VerifyType(TypeDescriptor typeDesc, System.Type type)
		{
			// TODO: VerifyType
		}
	}
}