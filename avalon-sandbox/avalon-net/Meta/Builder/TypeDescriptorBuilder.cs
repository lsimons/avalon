// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

namespace Apache.Avalon.Meta.Builder
{
	using System;
	using System.Collections;
	using System.Collections.Specialized;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for TypeDescriptorBuilder.
	/// </summary>
	public class TypeDescriptorBuilder
	{
		public TypeDescriptorBuilder()
		{
		}

		/// <summary>
		/// TODO
		/// </summary>
		/// <param name="typename"></param>
		/// <returns></returns>
		public TypeDescriptor CreateTypeDescriptor( String typename )
		{
			Type targetType = null;

			try
			{
				targetType = Type.GetType( typename, true, true );
			}
			catch(Exception e)
			{
				throw new BuilderException(
					String.Format("Exception obtaining type {0}", typename), e);
			}

			TypeMetaInformation typeInfo = null;
			
			try
			{
				typeInfo = TypeMetaInformation.Build( targetType );
			}
			catch(Exception e)
			{
				throw new BuilderException(
					String.Format("Exception gathering attributes from type {0}",
						targetType.FullName), e);
			}


			TypeDescriptor descriptor = null;

			InfoDescriptor info = BuildInfoDescriptor( typeInfo );
			CategoryDescriptor[] loggers = BuildCategoryDescriptor( typeInfo );
			ContextDescriptor context = BuildContextDescriptor( typeInfo );
			ServiceDescriptor[] services = BuildServiceDescriptor( typeInfo );
			DependencyDescriptor[] dependencies = BuildDependencyDescriptor( typeInfo );
			StageDescriptor[] stages = BuildStageDescriptor( typeInfo );
			ExtensionDescriptor[] extensions = BuildExtensionDescriptor( typeInfo );

			descriptor = new TypeDescriptor(info, loggers, 
				context, services, 
				dependencies, stages, extensions);

			return descriptor;
		}

		private ContextDescriptor BuildContextDescriptor( TypeMetaInformation typeInfo )
		{
			ArrayList list = new ArrayList();

			foreach(AvalonEntryAttribute attribute in typeInfo.EntriesAttribute)
			{
				EntryDescriptor entryDescriptor = 
					new EntryDescriptor( attribute.Key, attribute.EntryType, 
						attribute.Optional, attribute.Volatile, attribute.Alias, typeInfo[ attribute ] );

				list.Add( entryDescriptor );
			}

			EntryDescriptor[] entries = (EntryDescriptor[]) list.ToArray( typeof(EntryDescriptor) );

			ContextDescriptor desc = null;
			
			if (typeInfo.ContextAttribute != null)
			{
				desc = new ContextDescriptor( typeInfo.ContextAttribute.ContextType, entries );
			}
			else
			{
				desc = new ContextDescriptor( entries );
			}

			return desc;
		}

		private ServiceDescriptor[] BuildServiceDescriptor( TypeMetaInformation typeInfo )
		{
			if (typeInfo.ServicesAttribute.Length == 0)
			{
				throw new BuilderException(
					String.Format("Type {0} is not exposing AvalonServiceAttribute", typeInfo.TargetType.FullName));
			}

			ArrayList list = new ArrayList();

			foreach(AvalonServiceAttribute attribute in typeInfo.ServicesAttribute)
			{
				ReferenceDescriptor referenceDescriptor = 
					new ReferenceDescriptor( attribute.ServiceType.FullName );

				ServiceDescriptor serviceDescriptor = 
					new ServiceDescriptor(referenceDescriptor);

				list.Add( serviceDescriptor );
			}

			return (ServiceDescriptor[]) list.ToArray( typeof(ServiceDescriptor) );
		}

		private DependencyDescriptor[] BuildDependencyDescriptor( TypeMetaInformation typeInfo )
		{
			if (typeInfo.DependenciesAttribute.Length == 0)
			{
				return null;
			}

			ArrayList list = new ArrayList();

			foreach(AvalonDependencyAttribute attribute in typeInfo.DependenciesAttribute)
			{
				ReferenceDescriptor referenceDescriptor = 
					new ReferenceDescriptor( attribute.DependencyType.FullName );

				DependencyDescriptor serviceDescriptor = 
					new DependencyDescriptor( attribute.Key, 
						referenceDescriptor, 
						attribute.IsOptional, null, typeInfo[ attribute ] );

				list.Add( serviceDescriptor );
			}

			return (DependencyDescriptor[]) list.ToArray( typeof(DependencyDescriptor) );
		}

		private StageDescriptor[] BuildStageDescriptor( TypeMetaInformation typeInfo )
		{
			ArrayList list = new ArrayList();

			foreach( AvalonStageAttribute attribute in typeInfo.StagesAttribute )
			{
				list.Add ( new StageDescriptor(attribute.ExtensionID) );
			}

			return (StageDescriptor[]) list.ToArray( typeof(StageDescriptor) );
		}

		private ExtensionDescriptor[] BuildExtensionDescriptor( TypeMetaInformation typeInfo )
		{
			ArrayList list = new ArrayList();

			foreach( AvalonExtensionAttribute attribute in typeInfo.ExtensionsAttribute )
			{
				list.Add ( new ExtensionDescriptor(attribute.ID) );
			}

			return (ExtensionDescriptor[]) list.ToArray( typeof(ExtensionDescriptor) );
		}

		private CategoryDescriptor[] BuildCategoryDescriptor( TypeMetaInformation typeInfo )
		{
			String loggerName = typeInfo.ComponentAttribute.Name;

			if (typeInfo.LoggerAttribute != null)
			{
				loggerName = typeInfo.LoggerAttribute.Name;
			}

			CategoryDescriptor category = new CategoryDescriptor( loggerName, null );

			return new CategoryDescriptor[] { category };
		}

		private InfoDescriptor BuildInfoDescriptor( TypeMetaInformation typeInfo )
		{
			if ( typeInfo.ComponentAttribute == null )
			{
				throw new BuilderException(
					String.Format("Type {0} is not an Avalon component", typeInfo.TargetType.FullName));
			}

			String name = typeInfo.ComponentAttribute.Name;
			String typename = typeInfo.TargetType.FullName; // Verificar se o nome do assembly é incluido
			Version version = typeInfo.TargetType.Assembly.GetName().Version;
			Lifestyle lifestyle = typeInfo.ComponentAttribute.Lifestyle;
			
			// TODO: What is a component schema anyway?
			String schema = ExtractSchemaName( typeInfo ); 
			
			// TODO: We should expose this using some attribute
			CollectionPolicy collectionPolicy = CollectionPolicy.Conservative; 

			// TODO: We should expose this somehow
			NameValueCollection attributes = new NameValueCollection();

			return new InfoDescriptor( name, typename, version, 
				lifestyle, collectionPolicy, schema, attributes );
		}

		private String ExtractSchemaName( TypeMetaInformation typeInfo )
		{
			String schema = String.Empty;

			if (typeInfo.ConfigurationAttribute != null)
			{
				schema = typeInfo.ConfigurationAttribute.Schema;
			}

			return schema;
		}
	}
}
