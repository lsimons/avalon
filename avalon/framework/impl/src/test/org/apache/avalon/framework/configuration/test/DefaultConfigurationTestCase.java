/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.configuration.test;

import java.util.List;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Test the basic public methods of DefaultConfiguration.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultConfigurationTestCase extends TestCase
{
    private DefaultConfiguration m_configuration;

    public DefaultConfigurationTestCase()
    {
        this("DefaultConfiguration Test Case");
    }

    public DefaultConfigurationTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_configuration = new DefaultConfiguration( "a", "b" );
    }

    public void tearDowm()
    {
        m_configuration = null;
    }

    public void testGetValue()
        throws Exception
    {
        final String orgValue = "Original String";
        m_configuration.setValue( orgValue );
        assertEquals( orgValue, m_configuration.getValue() );
    }

    public void testGetValueAsInteger()
        throws Exception
    {
        final int orgValue = 55;
        final String strValue = Integer.toHexString( orgValue );
        m_configuration.setValue( "0x" + strValue );
        assertEquals( orgValue, m_configuration.getValueAsInteger() );
    }

    public void testGetAttribute()
        throws Exception
    {
        final String key = "key";
        final String value = "original value";
        final String defaultStr = "default";
        m_configuration.setAttribute( key, value );
        assertEquals( value, m_configuration.getAttribute( key, defaultStr ) );
        assertEquals(defaultStr , m_configuration.getAttribute( "newKey", defaultStr ) );
    }

    public void testMakeReadOnly()
    {
        final String key = "key";
        final String value = "original value";
        String exception = "exception not thrown";
        final String exceptionStr ="Configuration is read only";
        m_configuration.makeReadOnly();

        try
        {
            m_configuration.setAttribute( key, value );
        }
        catch( final IllegalStateException ise )
        {
            exception = exceptionStr;
        }

        assertEquals( exception, exceptionStr );
    }

    public void testAddRemoveChild()
    {
        final String childName = "child";
        final Configuration child = new DefaultConfiguration( childName, "child location" );

        m_configuration.addChild( child );
        assertEquals( child, m_configuration.getChild( childName ) );

        m_configuration.removeChild( child );
        assertEquals( null, m_configuration.getChild( childName, false ) );
    }
}





