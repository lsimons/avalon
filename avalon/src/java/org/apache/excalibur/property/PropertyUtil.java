/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.property;

import org.apache.avalon.context.Context;
import org.apache.avalon.context.ContextException;
import org.apache.avalon.context.Resolvable;

/**
 * This provides utility methods for properties.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class PropertyUtil
{
    private PropertyUtil()
    {
    }

    /**
     * Resolve property.
     * This evaluates all property substitutions based on current context.
     *
     * @param property the property to resolve
     * @param context the context in which to resolve property
     * @param ignoreUndefined if false will throw an PropertyException if property is not found
     * @return the reolved property
     * @exception PropertyException if an error occurs
     */
    public static Object resolveProperty( final String property,
                                          final Context context,
                                          final boolean ignoreUndefined )
        throws PropertyException
    {
        int start = property.indexOf( "${" );
        if( -1 == start ) return property;

        int end = property.indexOf( '}', start );
        if( -1 == end ) return property;

        final int length = property.length();

        if( 0 == start && end == (length - 1) )
        {
            return resolveValue( property.substring( start + 2, end ),
                                 context,
                                 ignoreUndefined );
        }

        final StringBuffer sb = new StringBuffer();
        int lastPlace = 0;

        while( true )
        {
            final Object value =
                resolveValue( property.substring( start + 2, end ),
                              context,
                              ignoreUndefined );

            sb.append( property.substring( lastPlace, start ) );
            sb.append( value );

            lastPlace = end + 1;

            start = property.indexOf( "${", end );
            if( -1 == start ) break;

            end = property.indexOf( '}', start );
            if( -1 == end ) break;
        }

        sb.append( property.substring( lastPlace, length ) );

        return sb.toString();
    }

    private static Object resolveValue( final String key,
                                        final Context context,
                                        final boolean ignoreUndefined )
        throws PropertyException
    {

        Object value = null;

        try
        {
            value = context.get( key );

            while( null != value && value instanceof Resolvable )
            {
                value = ((Resolvable)value).resolve( context );
            }
        }
        catch( final ContextException ce )
        {
        }

        if( null == value )
        {
            if( ignoreUndefined )
            {
                return "";
            }
            else
            {
                throw new PropertyException( "Unable to find " + key + " to expand during " +
                                             "property resolution." );
            }
        }

        return value;
    }
}
