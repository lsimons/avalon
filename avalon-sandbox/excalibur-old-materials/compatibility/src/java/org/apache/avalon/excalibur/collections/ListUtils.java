/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
 * @version CVS $Revision: 1.4 $ $Date: 2003/03/22 12:46:22 $
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
