/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.connection;

/**
 * Default Hnalder factory that creates instances via reflection.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultHandlerFactory
    extends AbstractHandlerFactory
{
    protected Class             m_handlerClass;

    public DefaultHandlerFactory( final Class handlerClass )
    {
        m_handlerClass = handlerClass;
    }

    /**
     * Overide this method to create actual instance of connection handler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    protected ConnectionHandler newHandler()
        throws Exception
    {
        return (ConnectionHandler)m_handlerClass.newInstance();
    }
}
