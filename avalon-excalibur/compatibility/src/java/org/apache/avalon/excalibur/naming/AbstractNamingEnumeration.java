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
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Class for building NamingEnumerations.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public abstract class AbstractNamingEnumeration
        implements NamingEnumeration
{
    protected Context m_owner;
    protected Namespace m_namespace;

    public AbstractNamingEnumeration( final Context owner, final Namespace namespace )
    {
        m_owner = owner;
        m_namespace = namespace;
    }

    public boolean hasMore()
            throws NamingException
    {
        return hasMoreElements();
    }

    public Object nextElement()
    {
        try
        {
            return next();
        }
        catch( final NamingException ne )
        {
            throw new NoSuchElementException( ne.toString() );
        }
    }

    protected Object resolve( final String name, final Object object )
            throws NamingException
    {
        // Call getObjectInstance for using any object factories
        try
        {
            final Name atom = m_owner.getNameParser( name ).parse( name );
            return m_namespace.
                    getObjectInstance( object, atom, m_owner, m_owner.getEnvironment() );
        }
        catch( final Exception e )
        {
            final NamingException ne = new NamingException( "getObjectInstance failed" );
            ne.setRootCause( e );
            throw ne;
        }
    }

    public void close()
    {
        m_namespace = null;
        m_owner = null;
    }
}
