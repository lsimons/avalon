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

namespace Apache.Avalon.Container.Test.Context
{
	using System;
	using System.Text;
	using NUnit.Framework;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Container.Context;

	/// <summary>
	/// Summary description for ContextTestCase.
	/// </summary>
	[TestFixture]
	public class ContextTestCase
	{
		private class ResolvableString : IResolvable
		{
			private string m_content;

			public ResolvableString( string content )
			{
				this.m_content = content;
			}

			public ResolvableString() : this( "This is a ${test}." )
			{
			}
		
			#region IResolvable Members

			public object Resolve(IContext context)
			{
				int index = this.m_content.IndexOf( "${" );
				if ( index < 0 )
				{
					return this.m_content;
				}

				StringBuilder buf = new StringBuilder( this.m_content.Substring( 0, index ) );

				while ( index >= 0 && index <= this.m_content.Length )
				{
					index += 2;
					int end = this.m_content.IndexOf( "}", index);

					if ( end < 0 )
					{
						end = this.m_content.Length;
					}

					buf.Append( context[ this.m_content.Substring( index, end - index ) ] );
					end++;

					index = this.m_content.IndexOf( "${", end ) + 2;

					if ( index < 2 )
					{
						index = -1;
						buf.Append( this.m_content.Substring( end, this.m_content.Length - end ) );
					}

					if ( index >=0 && index <= this.m_content.Length )
					{
						buf.Append( this.m_content.Substring( end, index - end ) );
					}
				}

				return buf.ToString();
			}

			#endregion
		}

		[Test]
		public void AddContext()
		{
			DefaultContext context = new DefaultContext();
			context.Put( "key1", "value1" );
			Assertion.Assert( "value1".Equals( context["key1"] ) );
			context.Put( "key1", String.Empty );
			Assertion.Assert( String.Empty.Equals( context["key1"] ) );

			context.Put( "key1", "value1" );
			context.MakeReadOnly();

			try
			{
				context.Put( "key1", String.Empty );
				Assertion.Fail( "You are not allowed to change a value after it has been made read only" );
			}
			catch ( ContextException )
			{
				Assertion.Assert( "Value is null", "value1".Equals( context["key1"] ) );
			}
		}

		[Test]
		public void ResolveableObject()
		{
			DefaultContext context = new DefaultContext();
			context.Put( "key1", new ResolvableString() );
			context.Put( "test", "Cool Test" );
			context.MakeReadOnly();

			IContext newContext = (IContext) context;
			Assertion.Assert( "Cool Test".Equals( newContext["test"] ) );
			Assertion.Assert( ! "This is a ${test}.".Equals( newContext["key1"] ) );
			Assertion.Assert( "This is a Cool Test.".Equals( newContext["key1"] ) );
		}

		[Test]
		public void CascadingContext()
		{
			DefaultContext parent = new DefaultContext();
			parent.Put( "test", "ok test" );
			parent.MakeReadOnly();
			DefaultContext child = new DefaultContext( parent );
			child.Put( "check", new ResolvableString("This is an ${test}.") );
			child.MakeReadOnly();
			IContext context = (IContext) child;

			Assertion.Assert ( "ok test".Equals( context["test"] ) );
			Assertion.Assert ( ! "This is an ${test}.".Equals( context["check"] ) );
			Assertion.Assert ( "This is an ok test.".Equals( context["check"] ) );
		}

		[Test]
		public void HiddenItems()
		{
			DefaultContext parent = new DefaultContext();
			parent.Put( "test", "test" );
			parent.MakeReadOnly();
			DefaultContext child = new DefaultContext( parent );
			child.Put( "check", "check" );
			IContext context = (IContext) child;
	        
			Assertion.Assert ( "check".Equals( context["check"] ) );
			Assertion.Assert ( "test".Equals( context["test"] ) );
	                
			child.Hide( "test" );
			try 
			{
				object o = context["test"];
				Assertion.Fail( "The item \"test\" was hidden in the child context, but could still be retrieved via Get()." );
			}
			catch (ContextException)
			{
				// Supposed to be thrown.
			}
	        
			child.MakeReadOnly();
	        
			try 
			{
				child.Hide( "test" );
				Assertion.Fail( "Hide() did not throw an exception, even though the context is supposed to be read-only." );
			}
			catch (ContextException)
			{
				// Supposed to be thrown.
			}
		}

	}
}
