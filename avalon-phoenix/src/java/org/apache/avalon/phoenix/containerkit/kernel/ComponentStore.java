/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.containerkit.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;

/**
 *
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2003/03/22 12:07:11 $
 */
public class ComponentStore
{
    /**
     * Parent {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}. Components in parent
     * {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore} are potential Providers for services
     * if no component in current {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore} satisfies
     * dependency.
     */
    private final ComponentStore m_parent;
    /**
     * The child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore} objects.
     * Possible consumers of services in this assembly.
     */
    private final ArrayList m_children = new ArrayList();
    /**
     * The set of components in assembly.
     * Used when searching for providers/consumers.
     */
    private final Map m_components = new HashMap();

    /**
     * Create a root ComponentStore without any parent
     * ComponentStore.
     */
    public ComponentStore()
    {
        this( null );
    }

    /**
     * Return the parent ComponentStore (may be null).
     */
    public ComponentStore getParent()
    {
        return m_parent;
    }

    /**
     * Create a root ComponentStore with specified parent
     * ComponentStore.
     */
    public ComponentStore( final ComponentStore parent )
    {
        m_parent = parent;
    }

    /**
     * Add child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}.
     *
     * @param child the child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}.
     */
    public void addChildStore( final ComponentStore child )
    {
        m_children.add( child );
    }

    /**
     * Return the list of child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}s.
     *
     * @return the list of child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}s.
     */
    public List getChildStores()
    {
        return m_children;
    }

    /**
     * Remove child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}.
     *
     * @param child the child {@link org.apache.avalon.phoenix.containerkit.kernel.ComponentStore}.
     */
    public void removeChildStore( final ComponentStore child )
    {
        m_children.remove( child );
    }

    /**
     * Add a component to store.
     *
     * @param component the component
     */
    public void addComponent( final ComponentProfile component )
    {
        final String name =
            component.getMetaData().getName();
        m_components.put( name, component );
    }

    /**
     * Remove a component from the store.
     *
     * @param component the component
     */
    public void removeComponent( final ComponentProfile component )
    {
        final String name =
            component.getMetaData().getName();
        m_components.remove( name );
    }

    /**
     * Return a component with specified name.
     *
     * @return a component with specified name
     */
    public ComponentProfile getComponent( final String name )
    {
        return (ComponentProfile)m_components.get( name );
    }

    /**
     * Return a collection containing all the
     * names of components in store. No ordering of
     * components is guarenteed or mandated.
     *
     * @return the collection containing all component names
     */
    public Collection getComponentNames()
    {
        final Collection collection = m_components.keySet();
        final ArrayList components = new ArrayList();
        components.addAll( collection );
        return components;
    }

    /**
     * Return a collection containing all the
     * components in store. Noordering of
     * components is guarenteed or mandated.
     *
     * @return the collection containing all components
     */
    public Collection getComponents()
    {
        final Collection collection = m_components.values();
        final ArrayList components = new ArrayList();
        components.addAll( collection );
        return components;
    }
}
