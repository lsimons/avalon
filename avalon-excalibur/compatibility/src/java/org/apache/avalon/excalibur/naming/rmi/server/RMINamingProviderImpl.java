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
package org.apache.avalon.excalibur.naming.rmi.server;

import java.io.Serializable;
import java.util.ArrayList;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.avalon.excalibur.naming.RemoteContext;
import org.apache.avalon.excalibur.naming.rmi.RMINamingProvider;

/**
 * The RMI implementation of provider.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.6 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class RMINamingProviderImpl
        implements Serializable, RMINamingProvider
{
    private Context m_root;

    public RMINamingProviderImpl( final Context root )
    {
        m_root = root;
    }

    public NameParser getNameParser()
            throws NamingException
    {
        return m_root.getNameParser( new CompositeName() );
    }

    public void bind( final Name name, final String className, final Object object )
            throws NamingException
    {
        final Binding binding = new Binding( name.toString(), className, object, true );
        m_root.bind( name, binding );
    }

    public void rebind( final Name name, final String className, final Object object )
            throws NamingException
    {
        final Binding binding = new Binding( name.toString(), className, object, true );
        m_root.rebind( name, binding );
    }

    public Context createSubcontext( final Name name )
            throws NamingException
    {
        m_root.createSubcontext( name );

        final RemoteContext context = new RemoteContext( null, name );
        return context;
    }

    public void destroySubcontext( final Name name )
            throws NamingException
    {
        m_root.destroySubcontext( name );
    }

    public NameClassPair[] list( final Name name )
            throws NamingException
    {
        //Remember that the bindings returned by this
        //actually have a nested Binding as an object
        final NamingEnumeration enum = m_root.listBindings( name );
        final ArrayList pairs = new ArrayList();

        while( enum.hasMore() )
        {
            final Binding binding = (Binding)enum.next();
            final Object object = binding.getObject();

            String className = null;

            //check if it is an entry or a context
            if( object instanceof Binding )
            {
                //must be an entry
                final Binding entry = (Binding)binding.getObject();
                className = entry.getObject().getClass().getName();
            }
            else if( object instanceof Context )
            {
                //must be a context
                className = RemoteContext.class.getName();
            }
            else
            {
                className = object.getClass().getName();
            }

            pairs.add( new NameClassPair( binding.getName(), className ) );
        }

        return (NameClassPair[])pairs.toArray( new NameClassPair[0] );
    }

    public Binding[] listBindings( final Name name )
            throws NamingException
    {
        //Remember that the bindings returned by this
        //actually have a nested Binding as an object
        final NamingEnumeration enum = m_root.listBindings( name );
        final ArrayList bindings = new ArrayList();

        while( enum.hasMore() )
        {
            final Binding binding = (Binding)enum.next();
            Object object = binding.getObject();
            String className = null;

            //check if it is an entry or a context
            if( object instanceof Binding )
            {
                //must be an entry
                final Binding entry = (Binding)binding.getObject();
                object = entry.getObject();
                className = object.getClass().getName();
            }
            else if( object instanceof Context )
            {
                //must be a context
                className = RemoteContext.class.getName();
                object = new RemoteContext( null, name );
            }
            else
            {
                className = object.getClass().getName();
            }

            final Binding result =
                    new Binding( binding.getName(), className, object );
            bindings.add( result );
        }

        return (Binding[])bindings.toArray( new Binding[0] );
    }

    public Object lookup( final Name name )
            throws NamingException
    {
        Object object = m_root.lookup( name );

        //check if it is an entry or a context
        if( object instanceof Binding )
        {
            object = ((Binding)object).getObject();
        }
        else if( object instanceof Context )
        {
            //must be a context
            object = new RemoteContext( null, name.getPrefix( name.size() - 1 ) );
        }

        return object;
    }

    public void unbind( final Name name )
            throws NamingException
    {
        m_root.unbind( name );
    }
}
