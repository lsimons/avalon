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
	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for ICalculator.
	/// </summary>
	public interface ICalculator
	{
		int Add(int x, int y);
	}

	public interface ICalculatorSingleton : ICalculator
	{
		int LastResult
		{
			get;
		}
	}

	/// <summary>
	/// Calculator Service
	/// </summary>
	[AvalonService( typeof(ICalculator) )]
	[AvalonComponent( @"Apache.Avalon.Container.Test\Calculator", Lifestyle.Transient )]
	public class Calculator : ICalculator
	{
		#region ICalculator Members

		public virtual int Add(int x, int y)
		{
			return x + y;
		}

		#endregion
	}


	/// <summary>
	/// Calculator Singleton Service
	/// </summary>
	[AvalonService( typeof(ICalculatorSingleton) )]
	[AvalonComponent( @"Apache.Avalon.Container.Test\CalculatorSingleton", Lifestyle.Singleton )]
	public class CalculatorSingleton : Calculator, ICalculatorSingleton
	{
		protected int m_lastResult;

		#region ICalculatorSingleton Members
		public int LastResult
		{
			get
			{
				return m_lastResult;
			}
		}
		#endregion

		#region ICalculator Members
		public override int Add(int x, int y)
		{
			m_lastResult = base.Add(x, y);
			return m_lastResult;
		}
		#endregion
	}
}
