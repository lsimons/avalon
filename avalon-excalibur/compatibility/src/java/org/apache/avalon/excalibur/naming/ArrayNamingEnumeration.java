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
package org.apache.avalon.excalibur.naming;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Class for building NamingEnumerations.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
final class ArrayNamingEnumeration
        extends AbstractNamingEnumeration
{
    protected Object[] m_items;
    protected int m_index;

    public ArrayNamingEnumeration( final Context owner,
            final Namespace namespace,
            final Object[] items )
    {
        super( owner, namespace );
        m_items = items;
        //m_index = 0;
    }

    public boolean hasMoreElements()
    {
        return m_index < m_items.length;
    }

    public Object next()
            throws NamingException
    {
        if( !hasMore() ) throw new NoSuchElementException();

        final Object object = m_items[m_index++];

        if( object instanceof Binding )
        {
            final Binding binding = (Binding)object;
            final Object resolvedObject = resolve( binding.getName(), binding.getObject() );
            binding.setObject( resolvedObject );
        }

        return object;
    }

    public void close()
    {
        super.close();
        m_items = null;
    }
}
