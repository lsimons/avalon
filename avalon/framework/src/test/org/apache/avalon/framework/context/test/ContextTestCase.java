/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
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
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.context.test;

import java.util.Properties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.TestCase;
import junit.framework.AssertionFailedError;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.Resolvable;

/**
 * TestCase for Context.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public class ContextTestCase
    extends TestCase
{
    private static class ResolvableString implements Resolvable
    {
        private final String m_content;

        public ResolvableString( final String content )
        {
            this.m_content = content;
        }

        public ResolvableString()
        {
            this( "This is a ${test}." );
        }

        public final Object resolve( final Context context )
            throws ContextException
        {
            int index = this.m_content.indexOf( "${" );

            if ( index < 0 )
            {
                return this.m_content;
            }

            StringBuffer buf = new StringBuffer( this.m_content.substring( 0, index ) );

            while ( index >= 0 && index <= this.m_content.length() )
            {
                index += 2;
                int end = this.m_content.indexOf( "}", index);

                if ( end < 0 )
                {
                    end = this.m_content.length();
                }

                buf.append( context.get( this.m_content.substring( index, end ) ) );
                end++;

                index = this.m_content.indexOf( "${", end ) + 2;

                if ( index < 2 )
                {
                    index = -1;
                    buf.append( this.m_content.substring( end, this.m_content.length() ) );
                }

                if ( index >=0 && index <= this.m_content.length() )
                {
                    buf.append( this.m_content.substring( end, index ) );
                }
            }

            return buf.toString();
        }
    }

    public ContextTestCase( final String name )
    {
        super( name );
    }

    public void testAddContext()
        throws Exception
    {
        final DefaultContext context = new DefaultContext();
        context.put( "key1", "value1" );
        assertTrue( "value1".equals( context.get( "key1" ) ) );
        context.put( "key1", "" );
        assertTrue( "".equals( context.get( "key1" ) ) );

        context.put( "key1", "value1" );
        context.makeReadOnly();

        try
        {
            context.put( "key1", "" );
            throw new AssertionFailedError( "You are not allowed to change a value after it has been made read only" );
        }
        catch ( IllegalStateException ise )
        {
            assertTrue( "Value is null", "value1".equals( context.get( "key1" ) ) );
        }
    }

    public void testResolveableObject()
        throws ContextException
    {
        final DefaultContext context = new DefaultContext();
        context.put( "key1", new ResolvableString() );
        context.put( "test", "Cool Test" );
        context.makeReadOnly();

        final Context newContext = (Context) context;
        assertTrue( "Cool Test".equals( newContext.get( "test" ) ) );
        assertTrue( ! "This is a ${test}.".equals( newContext.get( "key1" ) ) );
        assertTrue( "This is a Cool Test.".equals( newContext.get( "key1" ) ) );
    }

    public void testCascadingContext()
         throws ContextException
    {
        final DefaultContext parent = new DefaultContext();
        parent.put( "test", "ok test" );
        parent.makeReadOnly();
        final DefaultContext child = new DefaultContext( parent );
        child.put( "check", new ResolvableString("This is an ${test}.") );
        child.makeReadOnly();
        final Context context = (Context) child;

        assertTrue ( "ok test".equals( context.get( "test" ) ) );
        assertTrue ( ! "This is an ${test}.".equals( context.get( "check" ) ) );
        assertTrue ( "This is an ok test.".equals( context.get( "check" ) ) );
    }
    
    public void testHiddenItems()
        throws ContextException
    {
        final DefaultContext parent = new DefaultContext();
        parent.put( "test", "test" );
        parent.makeReadOnly();
        final DefaultContext child = new DefaultContext( parent );
        child.put( "check", "check" );
        final Context context = (Context) child;
        
        assertTrue ( "check".equals( context.get( "check" ) ) );
        assertTrue ( "test".equals( context.get( "test" ) ) );
                
        child.hide( "test" );
        try 
        {
            context.get( "test" );
            fail( "The item \"test\" was hidden in the child context, but could still be retrieved via get()." );
        }
        catch (ContextException ce)
        {
            // Supposed to be thrown.
        }
        
        child.makeReadOnly();
        
        try 
        {
            child.hide( "test" );
            fail( "hide() did not throw an exception, even though the context is supposed to be read-only." );
        }
        catch (IllegalStateException ise)
        {
            // Supposed to be thrown.
        }
    }
}
