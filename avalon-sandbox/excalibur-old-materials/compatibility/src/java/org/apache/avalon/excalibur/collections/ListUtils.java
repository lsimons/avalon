/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Miscelaneous utilities to manipulate Lists.
 *
 * @deprecated use org.apache.commons.collections.ListUtils instead
 *
 * @author  <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author  <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/11 13:29:16 $
 * @since 4.0
 */
public class ListUtils
{
    public static List intersection( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList();
        final Iterator iterator = list2.iterator();

        while( iterator.hasNext() )
        {
            final Object o = iterator.next();

            if( list1.contains( o ) )
            {
                result.add( o );
            }
        }

        return result;
    }

    public static List subtract( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList( list1 );
        final Iterator iterator = list2.iterator();

        while( iterator.hasNext() )
        {
            result.remove( iterator.next() );
        }

        return result;
    }

    public static List sum( final List list1, final List list2 )
    {
        return subtract( union( list1, list2 ),
                         intersection( list1, list2 ) );
    }

    public static List union( final List list1, final List list2 )
    {
        final ArrayList result = new ArrayList( list1 );

        final Iterator iterator = list2.iterator();
        while( iterator.hasNext() )
        {
            final Object o = iterator.next();
            if( !result.contains( o ) )
            {
                result.add( o );
            }
        }
        return result;
    }
}
