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


using System;

namespace Apache.Avalon.Framework
{
	/// <summary>
	///	Utility class that makes it easier to transfer
	/// a component throught it's lifecycle stages.
	/// </summary>
	public class ContainerUtil
	{
		/// <summary>
		/// Runs specified object through shutdown lifecycle stages
		/// (Stop and Dispose).
		/// </summary>
		/// <param name="component">The Component to shutdown.</param>
		/// <exception cref="Exception">If there is a problem stoppping object.</exception>
		public static void Shutdown(object component)
		{
			Stop(component);
			Dispose(component);
		}

		/// <summary>
		/// Supplies specified object with Logger if it implements the
		/// <see cref="ILogEnabled"/> interface.
		/// </summary>
		/// <param name="component">The component instance</param>
		/// <param name="logger">The Logger to enable component with.</param>
		/// <exception cref="ArgumentException">
		/// If the component is <see cref="ILogEnabled"/> but <see cref="ILogger"/> is null.
		/// </exception>
		/// <remarks>
		/// The Logger may be null in which case
		/// the specified component must not implement <see cref="ILogEnabled"/>.
		/// </remarks>
		public static void EnableLogging(object component, ILogger logger)
		{
			if (component is ILogEnabled)
			{
				if (logger == null)
				{
					throw new ArgumentNullException("logger is null");
				}

				((ILogEnabled) component).EnableLogging(logger);
			}
		}

		/// <summary>
		/// Checks if the specified components supports IStartable
		/// or IDisposable interfaces - meaning that it cares about Stop
		/// or disposable phases.
		/// </summary>
		/// <param name="component">The component instance</param>
		/// <returns>true if the component wants the shutdown phase.</returns>
		public static bool ExpectsDispose(object component)
		{
			return (component is IStartable) || (component is IDisposable);
		}

		/// <summary>
		/// Supplies specified component with <see cref="ILookupManager"/>
		/// if it implements the <see cref="ILookupEnabled"/> interface.
		/// </summary>
		/// <param name="component">The Component to service.</param>
		/// <param name="lookupManager">
		/// The <see cref="ILookupManager"/> object to use for component.
		/// </param>
		/// <exception cref="ArgumentException">
		/// If the object is <see cref="ILookupEnabled"/> but
		/// <see cref="ILookupManager"/> is null.
		/// </exception>
		/// <exception cref="LookupException">
		/// If there is a problem servicing component.
		/// </exception>
		/// <remarks>
		/// The Service manager may be null in
		/// which case the specified component must not
		/// implement <see cref="ILookupEnabled"/>.
		/// </remarks>
		public static void Service(object component, ILookupManager lookupManager)
		{
			if (component is ILookupEnabled)
			{
				if (lookupManager == null)
				{
					throw new ArgumentNullException("LookupManager is null");
				}

				((ILookupEnabled) component).EnableLookups(lookupManager);
			}
		}


		/// <summary>
		/// Configures specified component if it implements the
		/// <see cref="IConfigurable"/> interface.
		/// </summary>
		/// <param name="component">The Component to configure.</param>
		/// <param name="configuration">
		/// The Configuration object to use during the configuration.
		/// </param>
		/// <exception cref="ArgumentException">
		/// If the component is <see cref="IConfigurable"/> but
		/// configuration is null.
		/// </exception>
		/// <exception cref="ConfigurationException">
		/// If there is a problem configuring component,
		/// or the component is <see cref="IConfigurable"/> but configuration is null.
		/// </exception>
		/// <remarks>
		/// The Configuration may be null in which case
		///  the specified component must not implement <see cref="IConfigurable"/>.
		/// </remarks>
		public static void Configure(object component, IConfiguration configuration)
		{
			if (component is IConfigurable)
			{
				if (configuration == null)
				{
					throw new ArgumentNullException("configuration is null");
				}

				((IConfigurable) component).Configure(configuration);
			}
		}

		/// <summary>
		/// Initializes specified component if it implements the
		/// <see cref="IInitializable"/> interface.
		/// </summary>
		/// <param name="component">
		/// The Component to initialize.
		/// </param>
		/// <exception cref="Exception">
		/// If there is a problem initializing component.
		/// </exception>
		public static void Initialize(object component)
		{
			if (component is IInitializable)
			{
				( (IInitializable) component).Initialize();
			}
		}

		/// <summary>
		/// Starts specified component if it implements the
		/// <see cref="IStartable"/> interface.
		/// </summary>
		/// <param name="component">The Component to start.</param>
		/// <exception cref="Exception">
		/// If there is a problem starting component.
		/// </exception>
		public static void Start(object component)
		{
			if (component is IStartable)
			{
				( (IStartable) component).Start();
			}
		}

		/// <summary>
		/// Stops specified components if it implements the
		/// <see cref="IStartable"/> interface.
		/// </summary>
		/// <param name="component">The Component to stop.</param>
		/// <exception cref="Exception">
		/// If there is a problem stoppping component.
		/// </exception>
		public static void Stop(object component)
		{
			if (component is IStartable)
			{
				( (IStartable) component).Stop();
			}
		}

		/// <summary>
		/// Disposes specified component if it implements the
		/// <see cref="IDisposable"/> interface.
		/// </summary>
		/// <param name="component">The Component to dispose.</param>
		/// <exception cref="Exception">
		/// If there is a problem disposing component.
		/// </exception>
		public static void Dispose(object component)
		{
			if (component is IDisposable)
			{
				((IDisposable) component).Dispose();
			}
		}
	}
}
