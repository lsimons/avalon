/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.embeddor;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * @author <a href="bauer@denic.de">Joerg Bauer</a>
 */
public class EmbeddorEntry
{
    private String m_role;
    private Configuration m_configuration;
    private String m_classname;
    private String m_loggerName;
    private Object m_object;

    public String getRole()
    {
        return m_role;
    }

    public void setRole( String role )
    {
        m_role = role;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public void setConfiguration( Configuration conf )
    {
        this.m_configuration = conf;
    }

    public String getClassName()
    {
        return m_classname;
    }

    public void setClassName( String name )
    {
        m_classname = name;
    }

    public Object getObject()
    {
        return m_object;
    }

    public void setObject( Object object )
    {
        m_object = object;
    }

    public String getLoggerName()
    {
        return m_loggerName;
    }

    public void setLoggerName( String logger )
    {
        m_loggerName = logger;
    }
}