/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.camelot;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLoggable;

/**
 * This contains it during execution and may provide certain
 * facilities (like a thread per EJB etc).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractContainer
    extends AbstractLoggable
    implements Container
{
    protected final HashMap          m_entries      = new HashMap();
    protected Class                  m_entryClass;

    /**
     * Add a component instance to container.
     *
     * @param entry the component entry
     */
    public void add( final String name, final Entry entry )
        throws ContainerException
    {
        checkEntry( name, entry );
        preAdd( name, entry );
        m_entries.put( name, entry );
        postAdd( name, entry );
    }

    /**
     * Remove a component instance from container.
     *
     * @param name the name of component
     */
    public void remove( final String name )
        throws ContainerException
    {
        final Entry entry = (Entry)m_entries.get( name );

        if( null == entry )
        {
            throw new ContainerException( "Component named " + name + " not contained" );
        }

        preRemove( name, entry );
        m_entries.remove( name );
        postRemove( name, entry );
    }

    /**
     * Retrieve Entry from container
     *
     * @param name the name of entry
     * @return the entry
     */
    public Entry getEntry( final String name )
        throws ContainerException
    {
        final Entry entry = (Entry)m_entries.get( name );

        if( null == entry )
        {
            throw new ContainerException( "Name " + name + " not contained" );
        }
        else
        {
            return entry;
        }
    }

    /**
     * List all names of entries in container.
     *
     * @return the list of all entries
     */
    public Iterator list()
    {
        return m_entries.keySet().iterator();
    }

    /**
     * This method is called before entry is added to give chance for
     * sub-class to veto removal.
     *
     * @param name the name of entry
     * @param entry the entry
     * @exception ContainerException to stop removal of entry
     */
    protected void preAdd( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * This method is called after entry is added to give chance for
     * sub-class to do some cleanup.
     *
     * @param name the name of entry
     * @param entry the entry
     */
    protected void postAdd( final String name, final Entry entry )
    {
    }

    /**
     * This method is called before entry is removed to give chance for
     * sub-class to veto removal.
     *
     * @param name the name of entry
     * @param entry the entry
     * @exception ContainerException to stop removal of entry
     */
    protected void preRemove( final String name, final Entry entry )
        throws ContainerException
    {
    }

    /**
     * This method is called after entry is removed to give chance for
     * sub-class to do some cleanup.
     *
     * @param name the name of entry
     * @param entry the entry
     */
    protected void postRemove( final String name, final Entry entry )
    {
    }

    /**
     * List all entries in container.
     *
     * @return the list of all entries
     */
    protected Iterator listEntries()
    {
        return m_entries.values().iterator();
    }

    protected void checkEntry( final String name, final Entry entry )
        throws ContainerException
    {
        if( null != m_entries.get( name ) )
        {
            throw new ContainerException( "Can not add component to container because " +
                                          "entry already exists with name " + name );
        }

        if( !isValidName( name ) )
        {
            throw new ContainerException( "Can not add component to container because " +
                                          "invalid name " + name );
        }

        if( !isValidEntry( entry ) )
        {
            throw new ContainerException( "Can not add component to container because " +
                                          "invalid entry for " + name );
        }

        if( !m_entryClass.isAssignableFrom( entry.getClass() ) )
        {
            throw new ContainerException( "Only Entries of type " + m_entryClass.getName() +
                                          " may be placed in container." );
        }
    }

    protected boolean isValidName( final String name )
        throws ContainerException
    {
        return true;
    }

    protected boolean isValidEntry( final Entry entry )
        throws ContainerException
    {
        return true;
    }
}
