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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $
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

