/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration;

final class ConfigurationKey
{
    private final String m_application;

    private final String m_block;

    public ConfigurationKey( String application, String block )
    {
        this.m_application = application;
        this.m_block = block;
    }

    public int hashCode()
    {
        return this.getApplication().hashCode() + this.getBlock().hashCode();
    }

    public boolean equals( Object obj )
    {
        if( obj instanceof ConfigurationKey )
        {
            final ConfigurationKey key = (ConfigurationKey)obj;

            return this.getApplication().equals( key.getApplication() )
                && this.getBlock().equals( key.getBlock() );
        }
        else
        {
            return false;
        }
    }

    public String getApplication()
    {
        return m_application;
    }

    public String getBlock()
    {
        return m_block;
    }
}
