/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.collections;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration wrapper for array.
 *
 * @deprecated use org.apache.commons.collections.ArrayEnumeration instead
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 */
public final class ArrayEnumeration
        implements Enumeration
{
    protected Object[] m_elements;
    protected int m_index;

    public ArrayEnumeration( final List elements )
    {
        m_elements = elements.toArray();
    }

    public ArrayEnumeration( final Object[] elements )
    {
        m_elements = elements;
    }

    public boolean hasMoreElements()
    {
        return (m_index < m_elements.length);
    }

    public Object nextElement()
    {
        if( !hasMoreElements() )
        {
            throw new NoSuchElementException( "No more elements exist" );
        }

        return m_elements[m_index++];
    }
}

