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

import java.util.Iterator;
import java.util.List;

import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object using a List.
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:46:57 $
 */
public final class AggregatedValidity
    extends AbstractAggregatedValidity
    implements SourceValidity
{
    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        for( final Iterator i = m_list.iterator(); i.hasNext(); )
        {
            final int v = ( (SourceValidity)i.next() ).isValid();
            if( v < 1 )
            {
                return v;
            }
        }
        return 1;
    }

    public int isValid( final SourceValidity validity )
    {
        if( validity instanceof AggregatedValidity )
        {
            final AggregatedValidity other = (AggregatedValidity)validity;
            final List otherList = other.m_list;
            if( m_list.size() != otherList.size() )
            {
                return -1;
            }

            for( final Iterator i = m_list.iterator(), j = otherList.iterator(); i.hasNext(); )
            {
                final SourceValidity srcA = (SourceValidity)i.next();
                final SourceValidity srcB = (SourceValidity)j.next();
                int result = srcA.isValid();
                if ( result == -1) 
                {
                    return -1;
                }
                if ( result == 0 )
                {
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

}
