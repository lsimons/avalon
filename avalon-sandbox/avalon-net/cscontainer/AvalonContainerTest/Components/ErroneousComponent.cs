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
	/// Definition for IAirplane service.
	/// </summary>
	public interface IAirplane 
	{
		bool hasRadio
		{
			get;
		}

		void LandOff();
	}

	/// <summary>
	/// Definition for IAirBus service.
	/// </summary>
	public interface IAirBus 
	{
		void ReachTheMoon();
	}

	[AvalonService( typeof(IAirplane) )]
	[AvalonComponent( "Airplane", Lifestyle.Transient )]
	public class Airplane : IAirplane, ILookupEnabled
	{
		private bool _hasRadio = true;

		public Airplane()
		{
		}

		public bool hasRadio
		{
			get
			{
				return _hasRadio;
			}
		}

		public void LandOff()
		{
		}

		#region ILookupEnabled Members

		public void EnableLookups(ILookupManager manager)
		{
			// This component doesn't have
			// dependencies and can't lookup anything
			
			try
			{
				IRadio radio = (IRadio) manager[ typeof(IRadio).FullName ];

				_hasRadio = true;
			}
			catch(LookupException)
			{
				_hasRadio = false;
			}
		}

		#endregion
	}


	[AvalonService( typeof(IAirBus) )]
	[AvalonComponent( "AirBus", Lifestyle.Transient )]
	public class AirBus : IAirBus
	{
		public AirBus(int passengersCount)
		{
		}

		#region IAirBus Members

		public void ReachTheMoon()
		{
			// TODO:  Add AirBus.ReachTheMoon implementation
		}

		#endregion

	}

	
}
