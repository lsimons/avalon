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

namespace Apache.Avalon.Framework.Test
{
	using System;
	using System.Collections; 
	using NUnit.Framework;
	using Apache.Avalon.Framework;

	[TestFixture]
	public class ConfigurationCollectionTest
	{
		private IConfiguration[] arrayRange = new IConfiguration[] {
			new DefaultConfiguration("array", "ConfigurationCollectionTest" ),
			new DefaultConfiguration("array", "ConfigurationCollectionTest" ),
			new DefaultConfiguration("array", "ConfigurationCollectionTest" )
		};

		private ConfigurationCollection collRange;

		public ConfigurationCollectionTest()
		{
			collRange = new ConfigurationCollection();
			collRange.Add( new DefaultConfiguration("collection", "ConfigurationCollectionTest") );
			collRange.Add( new DefaultConfiguration("collection", "ConfigurationCollectionTest") );
			collRange.Add( new DefaultConfiguration("collection", "ConfigurationCollectionTest") );
		}

		[Test] public void Constructors()
		{
			ConfigurationCollection collection = new ConfigurationCollection();
			Assertion.AssertEquals( 0, collection.Count );

			collection = new ConfigurationCollection( collRange );
			Assertion.AssertEquals( 3, collection.Count );
			foreach( IConfiguration config in collection )
			{
				Assertion.AssertEquals( "collection", config.Name );
				Assertion.AssertEquals( "ConfigurationCollectionTest", config.Location );
			}

			collection = new ConfigurationCollection( arrayRange );
			Assertion.AssertEquals( 3, collection.Count );
			foreach( IConfiguration config in collection )
			{
				Assertion.AssertEquals( "array", config.Name );
				Assertion.AssertEquals( "ConfigurationCollectionTest", config.Location );
			}
		}

		[Test] public void Index()
		{
			ConfigurationCollection collection = new ConfigurationCollection( arrayRange );
			DefaultConfiguration testconfig = new DefaultConfiguration( "test", "ConfigurationCollectionTest" );
			testconfig.Value = "1";
			collection.Add( testconfig );

			Assertion.AssertEquals( 4, collection.Count );
			IConfiguration config = collection[3]; // 0 based indexes

			Assertion.AssertEquals( "test", config.Name );
			Assertion.AssertEquals( "ConfigurationCollectionTest" , config.Location );

			Assertion.Assert( ! ("1" == collection[0].Value) );
			Assertion.AssertEquals( "1", collection[3].Value );
		}

		[Test] public void Add()
		{
			ConfigurationCollection collection = new ConfigurationCollection();
			collection.Add( new DefaultConfiguration( "test", "ConfigurationCollectionTest" ) );
			Assertion.AssertEquals( 1, collection.Count );
			Assertion.AssertEquals( "test", collection[0].Name );
			Assertion.AssertEquals( "ConfigurationCollectionTest" , collection[0].Location );

			collection.AddRange( arrayRange );
			Assertion.AssertEquals( 4, collection.Count );

			collection.AddRange( collRange );
			Assertion.AssertEquals( 7, collection.Count );

			int place = 0;
			foreach( IConfiguration config in collection )
			{
				Assertion.AssertEquals( "ConfigurationCollectionTest", config.Location );
				switch (place)
				{
					case 0:
						Assertion.AssertEquals( "test", config.Name );
						break;

					case 1:
					case 2:
					case 3:
						Assertion.AssertEquals( "array", config.Name );
						break;

					case 4:
					case 5:
					case 6:
						Assertion.AssertEquals( "collection", config.Name );
						break;
				}
				place++;
			}
		}

		[Test] public void CopyTo()
		{
			ConfigurationCollection collection = new ConfigurationCollection( collRange );
			
			IConfiguration[] array = new IConfiguration[4];
			array[0] = new DefaultConfiguration( "test", "ConfigurationCollectionTest" );

			collection.CopyTo( array, 1 );

			bool isFirst = true;
			foreach ( IConfiguration config in array )
			{
				if (isFirst)
				{
					Assertion.AssertEquals("test", config.Name);
					isFirst = false;
				}
				else
				{
					Assertion.AssertEquals("collection", config.Name);
				}

				Assertion.AssertEquals("ConfigurationCollectionTest", config.Location);
			}
		}

		[Test] public void Contains()
		{
			ConfigurationCollection collection = new ConfigurationCollection( arrayRange );

			foreach ( IConfiguration config in arrayRange )
			{
				Assertion.AssertEquals( true, collection.Contains( config ) );
			}

			foreach ( IConfiguration config in collRange )
			{
				Assertion.AssertEquals( false, collection.Contains( config ) );
			}
		}

		[Test] public void IndexOf()
		{
			ConfigurationCollection collection = new ConfigurationCollection( arrayRange );
			Assertion.AssertEquals( 0, collection.IndexOf( arrayRange[0] ) );
			Assertion.AssertEquals( 2, collection.IndexOf( arrayRange[2] ) );
		}

		[Test] public void InsertRemove()
		{
			ConfigurationCollection collection = new ConfigurationCollection( arrayRange );
			IConfiguration config = new DefaultConfiguration( "test", "ConfigurationCollectionTest" );

			collection.Insert( 1, config );
			Assertion.Assert( collection.Contains( config ) );
			Assertion.AssertEquals( config, collection[1] );
			Assertion.AssertEquals( 4, collection.Count );

			collection.Remove( config );
			Assertion.AssertEquals( 3, collection.Count );
			Assertion.AssertEquals( false, collection.Contains( config ) );
		}
	}
}
