/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.lifecycle;

/**
 * This represents a basic Object in the Container.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class ComponentEntry
{
    private final String m_name;
    private Object m_object;
    private State m_state;

    public ComponentEntry( final String name )
    {
        m_name = name;
        setState( State.VOID );
    }

    public String getName()
    {
        return m_name;
    }

    public final synchronized State getState()
    {
        return m_state;
    }

    public final synchronized void setState( final State state )
    {
        m_state = state;
    }

    public synchronized Object getObject()
    {
        return m_object;
    }

    public synchronized void setObject( final Object object )
    {
        m_object = object;
    }

    protected synchronized void invalidate()
    {
        m_state = State.VOID;
        m_object = null;
    }
}
