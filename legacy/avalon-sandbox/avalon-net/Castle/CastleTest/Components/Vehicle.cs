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

namespace Apache.Avalon.Container.Test.Components
{
	using System;
	using NUnit.Framework;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Definitions for IVehicle service.
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
	[AvalonComponent( "Vehicle", Lifestyle.Singleton )]
	[AvalonDependency( typeof(IEngine), "Engine", Optional.False)]
	[AvalonDependency( typeof(IRadio), "Radio", Optional.False)]
	public class Vehicle : AbstractLogEnabled, IVehicle, IInitializable, ILookupEnabled, IStartable
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

		public void EnableLookups(ILookupManager manager)
		{
			Assertion.AssertNotNull(manager);

			Assertion.AssertNotNull(manager["Engine"]);
			Assertion.AssertNotNull(manager["Radio"]);

			Assertion.Equals( typeof(IEngine), manager["Engine"].GetType() );
			Assertion.Equals( typeof(IRadio), manager["Radio"].GetType() );
		}

		#region IStartable Members

		public void Start()
		{
			Logger.Info( "Vehicle -> Start" );
		}

		public void Stop()
		{
			Logger.Info( "Vehicle -> Stop" );
		}

		#endregion
	
		#region IInitializable Members

		public void Initialize()
		{
			Logger.Info( "Vehicle -> Initialize" );
		}

		#endregion
	}

	[AvalonService( typeof(IEngine) )]
	[AvalonComponent( "Engine", Lifestyle.Transient )]
	public class Engine : IEngine
	{
		public void TurnOn()
		{
		}

		public void TurnOff()
		{
		}
	}

	[AvalonService( typeof(IRadio) )]
	[AvalonComponent( "Radio", Lifestyle.Transient )]
	public class Radio : IRadio
	{
	}
}
