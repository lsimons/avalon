/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;

/**
 *
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2003/01/18 16:43:43 $
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
