/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.configuration.merged;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Matches a configuration based off of the name
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class NamedConfigurationMatcher implements ConfigurationMatcher
{
    private final String m_name;

    public NamedConfigurationMatcher( String name )
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        m_name = name;
    }

    public boolean isMatch( Configuration c )
    {
        return m_name.equals( c.getName() );
    }
}
