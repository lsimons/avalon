/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl.validity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object using a List.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/06/04 08:42:13 $
 */
public final class AggregatedValidity
    implements SourceValidity
{

    private List a;

    public AggregatedValidity()
    {
        this.a = new ArrayList();
    }

    public void add( SourceValidity validity )
    {
        this.a.add( validity );
    }

    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        for( Iterator i = a.iterator(); i.hasNext(); )
        {
            final int v = ((SourceValidity)i.next() ).isValid();
            if (v < 1) return v;
        }
        return 1;
    }

    public boolean isValid( SourceValidity validity )
    {
        if( validity instanceof AggregatedValidity )
        {
            List b = ( (AggregatedValidity)validity ).a;
            if( a.size() != b.size() )
                return false;
            for( Iterator i = a.iterator(), j = b.iterator(); i.hasNext(); )
            {
                final SourceValidity srcA = (SourceValidity)i.next();
                final SourceValidity srcB = (SourceValidity)j.next();
                if( srcA.isValid() < 1 && !srcA.isValid( srcB ) )
                    return false;
            }
            return true;
        }
        return false;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer( "SourceValidity " );
        for( Iterator i = a.iterator(); i.hasNext(); )
        {
            b.append( i.next() );
            if( i.hasNext() ) b.append( ':' );
        }
        return b.toString();
    }
}

