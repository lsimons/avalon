/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.impl;

import org.apache.avalon.fortress.impl.handler.ComponentHandler;

/**
 * This is the impl of runtime information about a
 * ComponentHandler.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/02/07 16:08:11 $
 */
class ComponentHandlerEntry
{
    private final ComponentHandler m_handler;
    private final ComponentHandlerMetaData m_metaData;

    /**
     * Create an entry for a particular handler.
     *
     * @param handler the handler
     * @param metaData the metadata for handler
     */
    public ComponentHandlerEntry( final ComponentHandler handler,
                                  final ComponentHandlerMetaData metaData )
    {
        if( null == handler )
        {
            throw new NullPointerException( "handler" );
        }
        if( null == metaData )
        {
            throw new NullPointerException( "metaData" );
        }


        m_handler = handler;
        m_metaData= metaData;
    }

    /**
     * Return the handler that entry manages.
     *
     * @return the handler that entry manages.
     */
    public ComponentHandler getHandler()
    {
        return m_handler;
    }

    /**
     * Return the meta data for handler.
     *
     * @return the meta data for handler.
     */
    public ComponentHandlerMetaData getMetaData()
    {
        return m_metaData;
    }
}
