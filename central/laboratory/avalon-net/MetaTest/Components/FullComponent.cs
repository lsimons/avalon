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

namespace Apache.Avalon.Meta.Test.Components
{
	using System;

	using Apache.Avalon.Framework;

	/// <summary>
	/// Summary description for FullComponent.
	/// </summary>
	[AvalonComponent("samplecomponent", Lifestyle.Singleton )]
	[AvalonService( typeof(IFirstService) )]
	[AvalonService( typeof(ISecondService) )]
	[AvalonLogger( "loggerName" )]
	public class FullComponent : IFirstService, ISecondService
	{
		[AvalonEntryAttribute("alias")]
		public FullComponent()
		{
		}

		[AvalonDependency( typeof(IThirdService), "role", Optional.True )]
		public IThirdService Service
		{
			get
			{
				return null;
			}
			set
			{
			}
		}
		}
}
