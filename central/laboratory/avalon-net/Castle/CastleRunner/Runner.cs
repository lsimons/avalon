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

namespace Apache.Avalon.Castle.Runner
{
	using System;
	using System.Threading;

	using Apache.Avalon.Castle;

	/// <summary>
	/// Summary description for Runner.
	/// </summary>
	public sealed class Runner
	{
		private Runner()
		{
		}

		public static void Main(String[] args)
		{
			CastleOptions options = new CastleOptions();

			if(ParseCommandLine(options, args))
			{
				Castle instance = new Castle(options);

				Thread runner = new Thread(new ThreadStart(instance.Start));

				runner.Start();
			}
		}

		private static bool ParseCommandLine(CastleOptions options, String[] args)
		{
			// TODO: override configuration based on command line arguments.

			return true;
		}
	}
}
