/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
}
