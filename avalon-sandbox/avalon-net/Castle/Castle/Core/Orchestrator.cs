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

namespace Apache.Avalon.Castle.Core
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using ILogger = Apache.Avalon.Framework.ILogger;

	/// <summary>
	/// Summary description for Orchestrator.
	/// </summary>
	[ManagedComponent]
	public class Orchestrator : ManagedService
	{
		private static readonly int LOGGER_MANAGER = 0;
		private static readonly int CONFIG_MANAGER = 1;
		private static readonly int LOOKUP_MANAGER = 2;

		protected ManagedObjectName[] childServices = new ManagedObjectName[3];

		protected ILogger logger = Logger.LoggerFactory.GetLogger("Orchestrator");

		private OrchestratorNotificationSystem notificationSystem;

		/// <summary>
		/// 
		/// </summary>
		public Orchestrator()
		{
			logger.Debug("Constructor");
		}

		[ManagedAttribute]
		public ManagedObjectName LoggerManager
		{
			get
			{
				return childServices[LOGGER_MANAGER];
			}
			set
			{
				childServices[LOGGER_MANAGER] = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName ConfigurationManager
		{
			get
			{
				return childServices[CONFIG_MANAGER];
			}
			set
			{
				childServices[CONFIG_MANAGER] = value;
			}
		}

		[ManagedAttribute]
		public ManagedObjectName LookupManager
		{
			get
			{
				return childServices[LOOKUP_MANAGER];
			}
			set
			{
				childServices[LOOKUP_MANAGER] = value;
			}
		}
	
		public override void Create()
		{
			logger.Debug("Create");

			base.Create();

			// Start up notification system

			notificationSystem = new OrchestratorNotificationSystem();

			foreach(ManagedObjectName child in childServices)
			{
				if (child == null)
				{
					continue;
				}

				logger.Debug("Invoking RegisterForPhases on {0}", child);

				server.Invoke( 
					child, 
					"RegisterForPhases", 
					new object[] { notificationSystem }, 
					new Type[] { typeof(OrchestratorNotificationSystem) } );

				logger.Debug("Done");
			}
		}
	}
}
