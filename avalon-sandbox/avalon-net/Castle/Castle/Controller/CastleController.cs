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

namespace Apache.Avalon.Castle.Controller
{
	using System;
	using System.Configuration;

	using Apache.Avalon.Castle.Controller.Config;
	using Apache.Avalon.Castle.ManagementExtensions;

	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for CastleController.
	/// </summary>
	[ManagedComponent]
	public class CastleController : MRegistrationListener
	{
		/// <summary>
		/// 
		/// </summary>
		protected MServer server;
		
		/// <summary>
		/// 
		/// </summary>
		private CastleConfig config;

		protected ILogger logger = Logger.LoggerFactory.GetLogger("CastleController");

		public CastleController()
		{
			logger.Debug("Constructor");
		}

		[ManagedOperation]
		public void Create()
		{
			logger.Debug("Create");

			InstantiateAndRegisterComponents();
		}

		[ManagedOperation]
		public void Start()
		{
			logger.Debug("Start");
		}

		[ManagedOperation]
		public void Stop()
		{
			logger.Debug("Stop");

			// Uninstall();
		}

		private void InstantiateAndRegisterComponents()
		{
			foreach(ComponentDescriptor component in config.Components)
			{
				RecursiveInstantiate(component);
			}
		}

		private void RecursiveInstantiate(ComponentDescriptor component)
		{
			foreach(ComponentDescriptor child in component.Dependencies.Components)
			{
				RecursiveInstantiate(child);
			}

			Instantiate(component);
		}

		/// <summary>
		/// Instantiates the component using the current MServer
		/// </summary>
		/// <param name="component"></param>
		private void Instantiate(ComponentDescriptor component)
		{
			logger.Debug("About to instantiate {0} type {1}", component.Name, component.Typename);

			ManagedObjectName name = new ManagedObjectName( component.Name );
			String typename = component.Typename;

			Object instance = server.Instantiate( typename );

			if ( !typeof(MService).IsAssignableFrom( instance.GetType() ) )
			{
				logger.Error("Type {0} does not implement interface MService", typename);

				throw new CastleContainerException( String.Format("Type {0} must implement MService", typename) );
			}

			logger.Debug("About to register {0}", name);
			server.RegisterManagedObject( instance, name );

			logger.Debug("About to setup {0}", name);
			Setup( name, component );
		}

		/// <summary>
		/// Handle lifecycle methods and setup component attributes and dependencies;
		/// </summary>
		/// <param name="name"></param>
		/// <param name="component"></param>
		private void Setup(ManagedObjectName name, ComponentDescriptor component)
		{
			SetupAttributes(name, component);
			SetupDependencies(name, component);

			logger.Debug("Invoking Create on {0}", name);
			server.Invoke(name, "Create", null, null);

			logger.Debug("Invoking Start on {0}", name);
			server.Invoke(name, "Start", null, null);
		}

		private void SetupAttributes(ManagedObjectName name, ComponentDescriptor component)
		{
			if (component.Attributes.Count == 0)
			{
				return;
			}

			ManagementInfo info = server.GetManagementInfo(name);

			foreach(AttributeDescriptor attribute in component.Attributes)
			{
				if (!info.Attributes.Contains(attribute.Name))
				{
					throw new InvalidConfigurationEntryException(String.Format("Entry {0} doesn't support attribute named {1}", name, attribute.Name));
				}

				ManagementAttribute mAtt = (ManagementAttribute) info.Attributes[attribute.Name];

				Object value = PerformConversion(mAtt.AttributeType, attribute.Value);

				logger.Debug("Setting Attribute '{0}' value '{1}'", attribute.Name, value);
				server.SetAttribute(name, attribute.Name, value);
			}
		}

		private void SetupDependencies(ManagedObjectName name, ComponentDescriptor component)
		{
			if (component.Dependencies.Count == 0)
			{
				return;
			}

			// TODO: Add dependency handling
		}

		private Object PerformConversion(Type targetType, Object currentValue)
		{
			// TODO: There is a lot to refactor here.

			if (targetType == typeof(ManagedObjectName))
			{
				return new ManagedObjectName( Convert.ToString(currentValue) );
			}

			throw new CastleContainerException("PerformConversion: Type not supported");
		}

		#region MRegistrationListener Members

		public void BeforeRegister(MServer server, ManagedObjectName name)
		{
			logger.Debug("BeforeRegister {0}", name);

			this.server = server;

			logger.Debug("Obtaining configuration from {0}", AppDomain.CurrentDomain.SetupInformation.ConfigurationFile);
			
			config = (CastleConfig) 
				ConfigurationSettings.GetConfig("Castle/Services");

			if (config == null)
			{
				logger.Error("Null configuration returned");

				throw new InitializationException("Could not find configuration.");
			}
		}

		public void AfterDeregister()
		{
		}

		public void AfterRegister()
		{
		}

		public void BeforeDeregister()
		{
		}

		#endregion
	}
}
