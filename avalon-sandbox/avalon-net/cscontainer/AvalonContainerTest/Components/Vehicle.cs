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

namespace Apache.Avalon.Container.Test.Components
{
	using System;
	using NUnit.Framework;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for Vehicle.
	/// </summary>
	public interface IVehicle
	{
		IEngine Engine
		{
			get;
			set;
		}

		IRadio Radio
		{
			get;
			set;
		}
	}

	public interface IEngine
	{
		void TurnOn();

		void TurnOff();
	}

	public interface IRadio
	{
	}

	[AvalonService( typeof(IVehicle) )]
	[AvalonComponent( "Vehicle", Lifestyle.Transient )]
	[AvalonDependency( typeof(IEngine), "Engine", Optional.False)]
	[AvalonDependency( typeof(IRadio), "Radio", Optional.False)]
	public class Vehicle : IVehicle, ILookupEnabled
	{
		private IEngine m_engine;
		private IRadio  m_radio;

		public IEngine Engine
		{
			get
			{
				return m_engine;
			}
			set
			{
				m_engine = value;
			}
		}

		public IRadio Radio
		{
			get
			{
				return m_radio;
			}
			set
			{
				m_radio = value;
			}
		}

		#region ILookupEnabled Members

		public void EnableLookups(ILookupManager manager)
		{
			Assertion.AssertNotNull(manager);

			Assertion.AssertNotNull(manager["Engine"]);
			Assertion.AssertNotNull(manager["Radio"]);

			Assertion.Equals( typeof(IEngine), manager["Engine"].GetType() );
			Assertion.Equals( typeof(IRadio), manager["Radio"].GetType() );
		}

		#endregion
	}

	[AvalonService( typeof(IEngine) )]
	[AvalonComponent( "Engine", Lifestyle.Transient )]
	public class Engine : IEngine
	{
		#region IEngine Members

		public void TurnOn()
		{
		}

		public void TurnOff()
		{
		}

		#endregion
	}

	[AvalonService( typeof(IRadio) )]
	[AvalonComponent( "Radio", Lifestyle.Transient )]
	public class Radio : IRadio
	{
	}
}