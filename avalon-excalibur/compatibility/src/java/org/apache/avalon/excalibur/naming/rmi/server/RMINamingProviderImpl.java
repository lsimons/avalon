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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $
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

        return (NameClassPair[])pairs.toArray( new NameClassPair[ 0 ] );
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

        return (Binding[])bindings.toArray( new Binding[ 0 ] );
    }

    public Object lookup( final Name name )
        throws NamingException
    {
        Object object = m_root.lookup( name );

        //check if it is an entry or a context
        if( object instanceof Binding )
        {
            object = ( (Binding)object ).getObject();
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
