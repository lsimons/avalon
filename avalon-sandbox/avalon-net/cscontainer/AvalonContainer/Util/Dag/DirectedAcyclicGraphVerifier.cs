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

namespace Apache.Avalon.Container.Util.Dag
{
	using System;
	using System.Collections;

	/// <summary>
	/// DirectedAcyclicGraphVerifier provides methods to verify that any set of
	/// vertices has no cycles.  A Directed Acyclic Graph is a "graph" or set of
	/// vertices where all connections between each vertex goes in a particular
	/// direction and there are no cycles or loops.  It is used to track dependencies
	/// and ansure that dependencies can be loaded and unloaded in the proper order.
	/// </summary>
	/// <remarks>
	/// Based on Java Fortress implementation class "DirectedAcyclicGraphVerifier.java"
	/// by Berin Loritsch and Leif Mortenson
	/// </remarks>
	internal class DirectedAcyclicGraphVerifier
	{
		private DirectedAcyclicGraphVerifier()
		{
		}

		public static void TopologicalSort( Vertex[] vertices )
		{
			Verify( vertices );

			Array.Sort( vertices );
		}

		private static void Verify( Vertex[] vertices )
	    {
			ResetVertices( vertices );
	        
			foreach(Vertex vertex in vertices)
			{
				vertex.ResolveOrder();
			}
		}

		private static void ResetVertices( Vertex[] vertices )
		{
			foreach(Vertex vertex in vertices)
			{
				vertex.Reset();
			}
		}
	}
}
