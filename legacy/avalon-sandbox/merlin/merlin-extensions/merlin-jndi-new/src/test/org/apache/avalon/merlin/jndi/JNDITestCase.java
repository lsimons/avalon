/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.merlin.jndi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;

import junit.framework.TestCase;

public class JNDITestCase extends TestCase
{
    public JNDITestCase( )
    {
        this( "jndi" );
    }

    public JNDITestCase( String name )
    {
        super( name );
    }

    protected void setUp() throws Exception
    {
        Hashtable environment = new Hashtable();
        environment.put( 
          Context.INITIAL_CONTEXT_FACTORY, 
          "org.apache.avalon.merlin.jndi.StandardInitialContextFactory" );
        environment.put( 
          Context.URL_PKG_PREFIXES, 
          "block://localhost/" );
    }

    public void testInitialContext() throws Exception
    {
        try
        {
            Context root = (Context) new InitialContext().lookup( "block:/" );
            print( root );
            assertTrue( true );
        }
        catch( Throwable e )
        {
            System.out.println( "WOOPS: " + e );
            e.printStackTrace();
            assertTrue( false );
        }
    }

    private static void print( Context context ) throws NamingException 
    {
        print( "  ", context );
    }

    private static void print( String header, Object object ) throws NamingException
    {
        if( object instanceof Context )
        {
            printContext( header, (Context) object );
        }
        else
        {
            System.out.println( header + "class: " + object.getClass().getName() );
        }
    }

    private static void print( String header, Context context, Object object ) throws NamingException
    {
        if( object instanceof Context )
        {
            printContext( header, (Context) object );
        }
        else if( object instanceof NameClassPair )
        {
            Object o = context.lookup( ((NameClassPair)object).getName() );
            print( header, o );
        }
        else
        {
            System.out.println( header + "class: " + object.getClass().getName() );
        }
    }

    private static void printContext( String header, Context context ) throws NamingException
    {
        System.out.println( header + "name: " + context );
        System.out.println( header + "environment: " + context.getEnvironment() );
        System.out.println( header + "path: " + context.getNameInNamespace() );
        Name name = new CompositeName();
        NamingEnumeration enum = context.list( name );
        final String s = header + "  ";
        while( enum.hasMore() )
        {
             Object next = enum.next();
             print( s, context, next );
        }
        enum.close();
    }
}
