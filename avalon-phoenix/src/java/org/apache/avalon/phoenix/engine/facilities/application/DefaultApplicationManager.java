/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities.application;

import org.apache.avalon.framework.atlantis.ManagerException;
import org.apache.avalon.framework.atlantis.SystemManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.engine.facilities.ApplicationManager;

/**
 * This is default implementation of ApplicationManager.
 * It uses kernel Manager to export management.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultApplicationManager
    implements ApplicationManager, Contextualizable, Composable
{
    private SystemManager    m_systemManager;
    private String           m_name;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_name = (String)context.get( "name" );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_systemManager = (SystemManager)componentManager.
            lookup( "org.apache.avalon.framework.atlantis.SystemManager" );
    }

   /**
     * Register a block for management.
     * The block is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register block under
     * @param block the block
     * @param interfaces the interfaces to register the component under
     * @exception ManagerException if an error occurs. An error could occur if the block doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @exception IllegalArgumentException if block or interfaces is null
     */
    public void register( final String name, final Block block, final Class[] interfaces )
        throws ManagerException, IllegalArgumentException
    {
        m_systemManager.register( getFQNFor( name ), block, interfaces );
    }

    /**
     * Unregister named block.
     *
     * @param name the name of block to unregister
     * @exception ManagerException if an error occurs such as when no such block registered.
     */
    public void unregister( final String name )
        throws ManagerException
    {
        m_systemManager.unregister( getFQNFor( name ) );
    }

    protected String getFQNFor( final String name )
    {
        return m_name + "/" + name;
    }
}
