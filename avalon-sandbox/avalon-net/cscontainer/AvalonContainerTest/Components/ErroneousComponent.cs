// Copyright 2004 Apache Software Foundation
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
