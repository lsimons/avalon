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
package org.apache.avalon.excalibur.naming.memory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingException;

import org.apache.avalon.excalibur.naming.AbstractNamingEnumeration;
import org.apache.avalon.excalibur.naming.Namespace;

/**
 * Class for building NamingEnumerations.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
final class MemoryNamingEnumeration
        extends AbstractNamingEnumeration
{
    protected Hashtable m_bindings;
    protected Iterator m_names;
    protected boolean m_returnBindings;

    public MemoryNamingEnumeration( final Context owner,
            final Namespace namespace,
            final Hashtable bindings,
            final boolean returnBindings )
    {
        super( owner, namespace );
        m_returnBindings = returnBindings;
        m_bindings = bindings;
        m_names = m_bindings.keySet().iterator();
    }

    public boolean hasMoreElements()
    {
        return m_names.hasNext();
    }

    public Object next()
            throws NamingException
    {
        if( !hasMore() ) throw new NoSuchElementException();

        final String name = (String)m_names.next();
        Object object = m_bindings.get( name );

        if( !m_returnBindings )
        {
            return new NameClassPair( name, object.getClass().getName() );
        }
        else
        {
            return new Binding( name, resolve( name, object ) );
        }
    }

    public void close()
    {
        super.close();
        m_bindings = null;
    }
}
