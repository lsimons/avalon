/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.source.impl.validity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object using a List.
 * This validity object does the same as the {@link AggregatedValidity}
 * object, but the contained validity objects are only fetched when
 * required.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/01/13 13:14:12 $
 */
public final class DeferredAggregatedValidity
        extends AbstractAggregatedValidity
    implements SourceValidity
{

    public void add( final DeferredValidity validity )
    {
        m_list.add( validity );
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
        for( final ListIterator i = m_list.listIterator(); i.hasNext(); )
        {
            final Object o = i.next();
            final SourceValidity validity;
            if (o instanceof SourceValidity) {
                validity = (SourceValidity)o;
            } else {
                validity = ((DeferredValidity)o).getValidity();
                i.set(validity);
            }
            final int v = validity.isValid();
            if( v < 1 )
            {
                return v;
            }
        }
        return 1;
    }

    public int isValid( final SourceValidity validity )
    {
        AbstractAggregatedValidity aggregatedValidity = null;
        
        if (validity instanceof AbstractAggregatedValidity) 
        {
            aggregatedValidity = (AbstractAggregatedValidity)validity;
        }
        
        if ( null != aggregatedValidity) 
        {
            ArrayList otherList = aggregatedValidity.m_list;
            if( m_list.size() != otherList.size() )
            {
                return -1;
            }

            for(int i=0; i < m_list.size(); i++) {
                final SourceValidity srcA = this.getValidity(i);
                int result = srcA.isValid();
                if ( result == -1) 
                {
                    return -1;
                }
                if ( result == 0 )
                {
                    final SourceValidity srcB = aggregatedValidity.getValidity(i);
                    result = srcA.isValid( srcB );
                    if ( result < 1)
                    {
                        return result;
                    }
                }
            }
            return 1;
        }
        return -1;
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer( "SourceValidity " );
        for( final Iterator i = m_list.iterator(); i.hasNext(); )
        {
            sb.append( i.next() );
            if( i.hasNext() ) sb.append( ':' );
        }
        return sb.toString();
    }
    
    SourceValidity getValidity(final int index) 
    {
        final Object o = m_list.get(index);
        final SourceValidity validity;
        if (o instanceof SourceValidity) {
            validity = (SourceValidity)o;
        } else {
            validity = ((DeferredValidity)o).getValidity();
            m_list.set(index, validity);
        }
        return validity;
    }
    
    private void writeObject(java.io.ObjectOutputStream out)
         throws IOException
    {
        // resolve all deferred source validities first
        for(int i=0; i<m_list.size();i++) {
            this.getValidity(i);
        }
        out.defaultWriteObject();
    }

}

