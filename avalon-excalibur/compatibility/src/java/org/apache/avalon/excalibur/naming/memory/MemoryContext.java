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
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.avalon.excalibur.naming.AbstractLocalContext;
import org.apache.avalon.excalibur.naming.Namespace;

/**
 * An in memory context implementation.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class MemoryContext
        extends AbstractLocalContext
{
    private Hashtable m_bindings;

    protected MemoryContext( final Namespace namespace,
            final Hashtable environment,
            final Context parent,
            final Hashtable bindings )
    {
        super( namespace, environment, parent );
        m_bindings = bindings;
    }

    public MemoryContext( final Namespace namespace,
            final Hashtable environment,
            final Context parent )
    {
        this( namespace, environment, parent, new Hashtable( 11 ) );
    }

    protected Context newContext()
            throws NamingException
    {
        return new MemoryContext( getNamespace(), getRawEnvironment(), getParent() );
    }

    protected Context cloneContext()
            throws NamingException
    {
        return new MemoryContext( getNamespace(), getRawEnvironment(), getParent(), m_bindings );
    }

    protected void doLocalBind( final Name name, final Object object )
            throws NamingException
    {
        m_bindings.put( name.get( 0 ), object );
    }

    protected NamingEnumeration doLocalList()
            throws NamingException
    {
        return new MemoryNamingEnumeration( this, getNamespace(), m_bindings, false );
    }

    protected NamingEnumeration doLocalListBindings()
            throws NamingException
    {
        return new MemoryNamingEnumeration( this, getNamespace(), m_bindings, true );
    }

    /**
     * Actually lookup raw entry in local context.
     * When overidding this it is not neccesary to resolve references etc.
     *
     * @param name the name in local context (size() == 1)
     * @return the bound object
     * @exception NamingException if an error occurs
     */
    protected Object doLocalLookup( final Name name )
            throws NamingException
    {
        final Object object = m_bindings.get( name.get( 0 ) );
        if( null == object ) throw new NameNotFoundException( name.get( 0 ) );
        return object;
    }

    /**
     * Actually unbind raw entry in local context.
     *
     * @param name the name in local context (size() == 1)
     * @exception NamingException if an error occurs
     */
    protected void doLocalUnbind( final Name name )
            throws NamingException
    {
        m_bindings.remove( name.get( 0 ) );
    }
}

