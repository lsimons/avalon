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
	using Apache.Avalon.Framework;
	using NUnit.Framework;

	public abstract class AbstractConfigurationTest
	{
		protected abstract AbstractConfiguration GetConfiguration();

		[Test]
		public void IsReadOnly()
		{
			AbstractConfiguration config = GetConfiguration();
			Assertion.AssertEquals( false, config.IsReadOnly );

			config.MakeReadOnly();

			Assertion.AssertEquals( true, config.IsReadOnly );
		}

		[Test]
		public void Name()
		{
			AbstractConfiguration config = GetConfiguration();

			config.Name = "Name";

			Assertion.AssertEquals( "Name", config.Name );
		}

		[Test]
		public void Location()
		{
			AbstractConfiguration config = GetConfiguration();

			config.Location = "Location";

			Assertion.AssertEquals( "Location", config.Location );
		}

		[Test]
		public void Value()
		{
			AbstractConfiguration config = GetConfiguration();

			config.Value = "Value";

			Assertion.AssertEquals( "Value", config.Value );

			config.Value = "true";

			Assertion.AssertEquals( true, (bool)config.GetValue(typeof( bool ) ) );

			int intValue = (int) config.GetValue(typeof(int), -1);
			Assertion.AssertEquals( -1, intValue );
			
			config.Value = "3";
			intValue = (int) config.GetValue( typeof( int ), -1 );
			Assertion.AssertEquals( 3, intValue );
		}

		[Test]
		public void Namespace()
		{
			AbstractConfiguration config = GetConfiguration();

			config.Namespace = "Namespace";

			Assertion.AssertEquals( "Namespace", config.Namespace );
		}

		[Test]
		public void Prefix()
		{
			AbstractConfiguration config = GetConfiguration();

			config.Prefix = "Prefix";

			Assertion.AssertEquals( "Prefix", config.Prefix );
		}

		[Test]
		public void Children()
		{
			AbstractConfiguration config = GetConfiguration();

			IConfiguration fooBar = config.GetChild("FooBar", false );
			Assertion.AssertNull( fooBar );
			fooBar = config.GetChild("FooBar", true);
			Assertion.AssertNotNull( fooBar );

			Assertion.AssertNotNull( config.Children );

			ConfigurationCollection collection = config.Children;

			for (int i = 0; i < 10; i++)
			{
				AbstractConfiguration child = GetConfiguration();
				child.Name="Child" + i;
				collection.Add( child );
			}

			config.Children = collection;

			Assertion.AssertEquals( 11, config.Children.Count );

			config.Children.Remove( fooBar );
			
			Assertion.AssertEquals( 10, config.Children.Count );

			int x = 0;
			foreach ( AbstractConfiguration child in config.Children )
			{
				Assertion.AssertEquals( "Child" + x, child.Name );
				x++;
			}
		}

		[Test]
		public void Attributes()
		{
			AbstractConfiguration config = GetConfiguration();

			Assertion.AssertEquals( 0, config.Attributes.Count );

			config.Attributes.Add( "Attr1", "Val1" );

			Assertion.AssertEquals( 1, config.Attributes.Count );

			Assertion.AssertEquals( "Val1", config.Attributes[ "Attr1" ] );

			config.Attributes.Add( "ValTest", "true" );
			Assertion.AssertEquals( 2, config.Attributes.Count );

			bool valTest = (bool) config.GetAttribute( "ValTest", typeof ( bool ) );
			Assertion.AssertEquals( true, valTest );

			config.Attributes["ValTest"] = "3";
			Assertion.AssertEquals( 2, config.Attributes.Count );

			int intValTest = (int) config.GetAttribute( "ValTest", typeof( int ), -1 );
			Assertion.AssertEquals( 3, intValTest );

			intValTest = (int) config.GetAttribute( "Attr1", typeof( int ), -1 );
			Assertion.AssertEquals( -1, intValTest );
			
		}

	}
}
