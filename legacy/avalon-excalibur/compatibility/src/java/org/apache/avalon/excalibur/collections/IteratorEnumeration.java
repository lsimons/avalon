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
import java.util.Iterator;

/**
 * Enumeration wrapper for iterator.
 *
 * @deprecated use org.apache.commons.collections.IteratorEnumeration instead
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 */
public final class IteratorEnumeration
        implements Enumeration
{
    protected Iterator m_iterator;

    public IteratorEnumeration( final Iterator iterator )
    {
        m_iterator = iterator;
    }

    public boolean hasMoreElements()
    {
        return m_iterator.hasNext();
    }

    public Object nextElement()
    {
        return m_iterator.next();
    }
}

