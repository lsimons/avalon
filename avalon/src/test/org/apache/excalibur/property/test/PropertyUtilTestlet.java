/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */ 
package org.apache.excalibur.property.test;

import org.apache.avalon.context.Context; 
import org.apache.avalon.context.DefaultContext; 
import org.apache.avalon.context.Resolvable; 
import org.apache.excalibur.property.PropertyException; 
import org.apache.excalibur.property.PropertyUtil; 
import org.apache.testlet.AbstractTestlet; 
 
/** 
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class PropertyUtilTestlet
    extends AbstractTestlet
{
    protected static final class ResolveTest
        implements Resolvable
    {
        protected int           m_count;
        protected int           m_current;

        public ResolveTest( final int count )
        {
            m_count = count;
        }

        public Object resolve( final Context context )
        {
            m_current++;

            if( m_current >= m_count ) return new Integer( m_count );
            else return this;
        }
    }

    protected final static Object     OBJ1         = new Object();
    protected final static Object     OBJ2         = new Object();

    protected DefaultContext          m_context;


    public void initialize()
    {
        m_context = new DefaultContext();
        m_context.put( "obj1", OBJ1 );
        m_context.put( "obj2", OBJ2 );
        m_context.put( "res1", new ResolveTest( 1 ) );
        m_context.put( "res2", new ResolveTest( 2 ) );
        m_context.put( "res3", new ResolveTest( 3 ) );
        m_context.put( "res4", new ResolveTest( 4 ) );
    }

    public void testNoResolve()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "blah", m_context, false );

        assertEquality( result, "blah" );
    }

    public void testObjResolve()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "${obj1}", m_context, false );

        assertEquality( result, OBJ1 );
    }

    public void testObjResolveToText()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "${obj1} ", m_context, false );

        assertEquality( result, OBJ1 + " " );
    }

    public void testDualObjResolve()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( " ${obj1} ${obj2} ", m_context, false );

        assertEquality( result, " " + OBJ1 + " " + OBJ2 + " " );
    }

    public void testRecurseObjResolve()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "${res1}", m_context, false );

        assertEquality( result, new Integer( 1 ) );
    }

    public void testRecurseObjResolve2()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "${res2}", m_context, false );

        assertEquality( result, new Integer( 2 ) );
    }

    public void testNullObjResolve()
        throws PropertyException
    {
        final Object result =
            PropertyUtil.resolveProperty( "${blahaaa}", m_context, true );

        assertEquality( result, "" );
    }

    public void testNullObjResolveForException()
        throws PropertyException
    {
        try
        {
            final Object result =
                PropertyUtil.resolveProperty( "${blahaaa}", m_context, false );
        }
        catch( final PropertyException pe )
        {
            return;
        }
        fail( "NUll resolve occured without exception" );
    }
}
