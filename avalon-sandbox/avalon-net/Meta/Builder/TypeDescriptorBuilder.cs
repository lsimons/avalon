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

		public TypeDescriptor CreateTypeDescriptor(String typename)
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

			TypeDescriptor descriptor = null;

			InfoDescriptor info = BuildInfoDescriptor( targetType );
			CategoryDescriptor[] loggers = BuildCategoryDescriptor( targetType );
			ContextDescriptor context = BuildContextDescriptor( targetType );
			ServiceDescriptor[] services = BuildServiceDescriptor( targetType );
			DependencyDescriptor[] dependencies = BuildDependencyDescriptor( targetType );
			StageDescriptor[] stages = BuildStageDescriptor( targetType );
			ExtensionDescriptor[] extensions = BuildExtensionDescriptor( targetType );

			descriptor = new TypeDescriptor(info, loggers, 
				context, services, 
				dependencies, stages, extensions);

			return descriptor;
		}

		private ContextDescriptor BuildContextDescriptor( Type targetType )
		{
			// TODO: Not supported yet
			return new ContextDescriptor( new EntryDescriptor[0] );
		}

		private ServiceDescriptor[] BuildServiceDescriptor( Type targetType )
		{
			if (! (targetType.IsDefined( typeof(AvalonServiceAttribute), true )) )
			{
				throw new BuilderException(
					String.Format("Type {0} is not exposing AvalonServiceAttribute", targetType.FullName));
			}

			ArrayList serviceList = new ArrayList();

			object[] attr = targetType.GetCustomAttributes( typeof(AvalonServiceAttribute), true );
			foreach(AvalonServiceAttribute serviceAttribute in attr)
			{
				ReferenceDescriptor referenceDescriptor = 
					new ReferenceDescriptor( serviceAttribute.ServiceType.FullName );

				ServiceDescriptor serviceDescriptor = 
					new ServiceDescriptor(referenceDescriptor);

				serviceList.Add( serviceDescriptor );
			}

			return (ServiceDescriptor[]) serviceList.ToArray( typeof(ServiceDescriptor) );
		}

		private DependencyDescriptor[] BuildDependencyDescriptor( Type targetType )
		{
			if (! (targetType.IsDefined( typeof(AvalonDependencyAttribute), true )) )
			{
				return null;
			}

			ArrayList dependencyList = new ArrayList();

			object[] attr = targetType.GetCustomAttributes( typeof(AvalonDependencyAttribute), true );
			foreach(AvalonDependencyAttribute dependencyAttribute in attr)
			{
				ReferenceDescriptor referenceDescriptor = 
					new ReferenceDescriptor( dependencyAttribute.DependencyType.FullName );

				DependencyDescriptor serviceDescriptor = 
					new DependencyDescriptor( dependencyAttribute.Key, 
						referenceDescriptor, 
						dependencyAttribute.IsOptional, null );

				dependencyList.Add( serviceDescriptor );
			}

			return (DependencyDescriptor[]) dependencyList.ToArray( typeof(DependencyDescriptor) );
		}

		private StageDescriptor[] BuildStageDescriptor( Type targetType )
		{
			// TODO: Not supported yet
			return new StageDescriptor[0];
		}

		private ExtensionDescriptor[] BuildExtensionDescriptor( Type targetType )
		{
			// TODO: Not supported yet
			return new ExtensionDescriptor[0];
		}

		private CategoryDescriptor[] BuildCategoryDescriptor( Type targetType )
		{
			// TODO: Currently very restrict usage:
			object[] attr = targetType.GetCustomAttributes( typeof(AvalonComponentAttribute), false );
			AvalonComponentAttribute componentAtt = attr[0] as AvalonComponentAttribute;

			CategoryDescriptor category = new CategoryDescriptor( componentAtt.LoggerName, null );

			return new CategoryDescriptor[] { category };
		}

		private InfoDescriptor BuildInfoDescriptor( Type targetType )
		{
			if (!(targetType.IsDefined( typeof(AvalonComponentAttribute), false )))
			{
				throw new BuilderException(
					String.Format("Type {0} is not an Avalon component", targetType.FullName));
			}

			object[] attr = targetType.GetCustomAttributes( typeof(AvalonComponentAttribute), false );
			AvalonComponentAttribute componentAtt = attr[0] as AvalonComponentAttribute;

			String name = componentAtt.Name;
			String typename = targetType.FullName; // Verificar se o nome do assembly é incluido
			Version version = targetType.Assembly.GetName().Version;
			Lifestyle lifestyle = componentAtt.Lifestyle;
			
			// TODO: What is a component schema anyway?
			String schema = String.Empty; 
			
			// TODO: We should expose this using some attribute
			CollectionPolicy collectionPolicy = CollectionPolicy.Conservative; 

			// TODO: We should expose this somehow
			NameValueCollection attributes = new NameValueCollection();

			return new InfoDescriptor( name, typename, version, 
				lifestyle, collectionPolicy, schema, attributes );
		}
	}
}
