/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Resolvable;

/**
 * This provides utility methods for properties.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/02/22 05:34:44 $
 * @since 4.0
 */
final class PropertyUtil
{
    private PropertyUtil()
    {
    }

    /**
     * Resolve a string property. This evaluates all property
     * substitutions based on specified context.
     *
     * @param property the property to resolve
     * @param context the context in which to resolve property
     * @param ignoreUndefined if false will throw an PropertyException if property is not found
     * @return the reolved property
     * @exception Exception if an error occurs
     */
    public static Object resolveProperty( final String property,
                                          final Context context,
                                          final boolean ignoreUndefined )
        throws Exception
    {
        int start = findBeginning( property, 0 );
        if( -1 == start )
        {
            return property;
        }

        int end = findEnding( property, start );

        final int length = property.length();

        if( 0 == start && end == ( length - 1 ) )
        {
            return resolveValue( property.substring( start + 2, end ),
                                 context,
                                 ignoreUndefined );
        }

        final StringBuffer sb = new StringBuffer( length * 2 );
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

            start = findBeginning( property, lastPlace );
            if( -1 == start )
            {
                break;
            }

            end = findEnding( property, start );
        }

        sb.append( property.substring( lastPlace, length ) );

        return sb.toString();
    }

    private static int findBeginning( final String property, final int currentPosition )
    {
        //TODO: Check if it is commented out
        return property.indexOf( "${", currentPosition );
    }

    private static int findEnding( final String property, final int currentPosition )
        throws Exception
    {
        //TODO: Check if it is commented out
        final int index = property.indexOf( '}', currentPosition );
        if( -1 == index )
        {
            throw new Exception( "Malformed property with mismatched }'s" );
        }

        return index;
    }

    /**
     * Retrieve a value from the specified context using the specified key.
     * If there is no such value and ignoreUndefined is not false then a
     * Exception is generated.
     *
     * @param key the key of value in context
     * @param context the Context
     * @param ignoreUndefined true if undefined variables are ignored
     * @return the object retrieved from context
     * @exception Exception if an error occurs
     */
    private static Object resolveValue( final String key,
                                        final Context context,
                                        final boolean ignoreUndefined )
        throws Exception
    {
        Object value = null;

        try
        {
            value = context.get( key );
        }
        catch( final ContextException ce )
        {
            //ignore
        }

        try
        {
            while( null != value && value instanceof Resolvable )
            {
                value = ( (Resolvable)value ).resolve( context );
            }
        }
        catch( final ContextException ce )
        {
            throw new Exception( "Unable to resolve value for key " + key );
        }

        if( null == value )
        {
            if( ignoreUndefined )
            {
                return "";
            }
            else
            {
                throw new Exception( "Unable to find " + key + " to expand during "
                                     + "property resolution." );
            }
        }

        return value;
    }
}
