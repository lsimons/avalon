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

namespace Apache.Avalon.Composition.Data.Builder
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Composition.Data;
	using Apache.Avalon.Meta;

	/// <summary>
	/// Summary description for TargetsCreator.
	/// </summary>
	public class TargetsCreator : ComponentProfileCreator
	{
		public TargetsCreator()
		{
		}

		/// <summary>
		/// Create a set of target directives from the confiugration.
		/// </summary>
		/// <param name="config">the targets configuration</param>
		/// <returns></returns>
		public Targets CreateTargets( IConfiguration config )
		{
			ConfigurationCollection children = config.GetChildren( "target" );
			TargetDirective[] targets = new TargetDirective[ children.Count ];
			int i=0;
			foreach( IConfiguration child in children )
			{
				targets[i++] = CreateTargetDirective( child );
			}
			return new Targets( targets );
		}

		/// <summary>
		/// Create a TargetDirective from a configuration
		/// </summary>
		/// <param name="config">the configuration</param>
		/// <returns>the target directive</returns>
		private TargetDirective CreateTargetDirective( IConfiguration config )
		{
			String name = (String) config.GetAttribute( "name", null ); // legacy
			if( name == null )
			{
				name = (String) config.GetAttribute( "path", null );
			}
			IConfiguration conf = config.GetChild( "configuration", false );
			CategoriesDirective categories = 
				GetCategoriesDirective( config.GetChild( "categories", false ), name );
			return new TargetDirective( name, conf, categories );
		}
	}
}
