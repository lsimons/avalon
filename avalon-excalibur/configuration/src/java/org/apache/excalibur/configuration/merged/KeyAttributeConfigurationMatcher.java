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
 * Matches a configuration based off of name and a specific attribute value
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class KeyAttributeConfigurationMatcher extends NamedConfigurationMatcher
{
    private final String m_attribute;
    private final String m_value;

    public KeyAttributeConfigurationMatcher( String name, String attribute, String value )
    {
        super( name );

        if( null == attribute )
        {
            throw new NullPointerException( "attribute" );
        }
        else if( null == value )
        {
            throw new NullPointerException( "value" );
        }

        m_attribute = attribute;
        m_value = value;
    }

    public boolean isMatch( Configuration c )
    {
        if( super.isMatch( c ) )
        {
            return m_value.equals( c.getAttribute( m_attribute, null ) );
        }
        else
        {
            return false;
        }
    }
}
