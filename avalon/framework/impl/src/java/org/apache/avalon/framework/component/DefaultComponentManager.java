/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.framework.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a static implementation of a ComponentManager. Allow ineritance
 * and extension so you can generate a tree of ComponentManager each defining
 * Component scope.
 *
 * <p>
 *  <span style="color: red">Deprecated: </span><i>
 *    Use {@link org.apache.avalon.framework.service.DefaultServiceManager} instead.
 *  </i>
 * </p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.24 $ $Date: 2003/02/11 15:58:38 $
 */
public class DefaultComponentManager
    implements ComponentManager
{
    private final HashMap m_components = new HashMap();
    private final ComponentManager m_parent;
    private boolean m_readOnly;

    /**
     * Construct ComponentManager with no parent.
     *
     */
    public DefaultComponentManager()
    {
        this( null );
    }

    /**
     * Construct ComponentManager with specified parent.
     *
     * @param parent the ComponentManagers parent
     */
    public DefaultComponentManager( final ComponentManager parent )
    {
        m_parent = parent;
    }

    /**
     * Retrieve Component by key from ComponentManager.
     *
     * @param key the key
     * @return the Component
     * @throws ComponentException if an error occurs
     */
    public Component lookup( final String key )
        throws ComponentException
    {
        final Component component = (Component)m_components.get( key );

        if( null != component )
        {
            return component;
        }
        else if( null != m_parent )
        {
            return m_parent.lookup( key );
        }
        else
        {
            throw new ComponentException( key, "Unable to provide implementation." );
        }
    }

    /**
     * Returns <code>true</code> if the component m_manager is managing a component
     * with the specified key, <code>false</code> otherwise.
     *
     * @param key key of the component you are lokking for
     * @return <code>true</code> if the component m_manager has a component with that key
     */
    public boolean hasComponent( final String key )
    {
        boolean componentExists = false;

        try
        {
            this.release( this.lookup( key ) );
            componentExists = true;
        }
        catch( Throwable t )
        {
            // Ignore all throwables--we want a yes or no answer.
        }

        return componentExists;
    }

    /**
     * Place Component into ComponentManager.
     *
     * @param key the components key
     * @param component the component
     */
    public void put( final String key, final Component component )
    {
        checkWriteable();
        m_components.put( key, component );
    }

    /**
     * Release component.
     *
     * @param component the component
     */
    public void release( final Component component )
    {
        // if the ComponentManager handled pooling, it would be
        // returned to the pool here.
    }

    /**
     * Build a human readable representation of ComponentManager.
     *
     * @return the description of ComponentManager
     */
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
        final Iterator components = m_components.keySet().iterator();
        buffer.append( "Components:" );

        while( components.hasNext() )
        {
            buffer.append( "[" );
            buffer.append( components.next() );
            buffer.append( "]" );
        }

        return buffer.toString();
    }

    /**
     * Helper method for subclasses to retrieve parent.
     *
     * @return the parent ComponentManager
     */
    protected final ComponentManager getParent()
    {
        return m_parent;
    }

    /**
     * Helper method for subclasses to retrieve component map.
     *
     * @return the component map
     */
    protected final Map getComponentMap()
    {
        return m_components;
    }

    /**
     * Make this component m_manager read only.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Check if this component m_manager is writeable.
     *
     * @throws IllegalStateException if this component m_manager is read-only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            final String message =
                "ComponentManager is read only and can not be modified";
            throw new IllegalStateException( message );
        }
    }
}
