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

		public TypeDescriptor CreateTypeDescriptor( Type targetType )
		{
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

			return CreateTypeDescriptor( targetType );
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
					new ReferenceDescriptor( attribute.ServiceType );

				ServiceDescriptor serviceDescriptor = 
					new ServiceDescriptor(referenceDescriptor);

				list.Add( serviceDescriptor );
			}

			return (ServiceDescriptor[]) list.ToArray( typeof(ServiceDescriptor) );
		}

		private DependencyDescriptor[] BuildDependencyDescriptor( TypeMetaInformation typeInfo )
		{
			ArrayList list = new ArrayList();

			foreach(AvalonDependencyAttribute attribute in typeInfo.DependenciesAttribute)
			{
				ReferenceDescriptor referenceDescriptor = 
					new ReferenceDescriptor( attribute.DependencyType );

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
			Type type = typeInfo.TargetType; // Verificar se o nome do assembly é incluido
			Lifestyle lifestyle = typeInfo.ComponentAttribute.Lifestyle;
			
			// TODO: What is a component schema anyway?
			String schema = ExtractSchemaName( typeInfo ); 
			
			// TODO: We should expose this using some attribute
			CollectionPolicy collectionPolicy = CollectionPolicy.Conservative; 

			// TODO: We should expose this somehow
			NameValueCollection attributes = new NameValueCollection();

			return new InfoDescriptor( name, type, lifestyle, 
				collectionPolicy, schema, attributes );
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
