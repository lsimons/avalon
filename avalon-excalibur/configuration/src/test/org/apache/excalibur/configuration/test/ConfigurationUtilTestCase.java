/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.configuration.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.configuration.ConfigurationUtil;

/**
 * Test the ConfigurationUtil class
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public final class ConfigurationUtilTestCase extends TestCase
{
    private DefaultConfiguration m_configuration;

    public ConfigurationUtilTestCase()
    {
        this( "ConfigurationUtil Test Case" );
    }

    public ConfigurationUtilTestCase( String name )
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

    /** this method is gone? public void testBranch()
        throws Exception
    {
        m_configuration.setAttribute( "test", "test" );
        m_configuration.setValue( "test" );
        m_configuration.addChild( new DefaultConfiguration( "test", "test" ) );

        final Configuration c =
                ConfigurationUtil.branch( m_configuration, "branched" );

        assertEquals( "branched", c.getName() );
        assertEquals( "test", c.getAttribute( "test" ) );
        assertEquals( "test", c.getValue() );
        assertTrue( c.getChild( "test", false ) != null );
    }*/

    public void testIdentityEquals()
    {
        assertTrue( ConfigurationUtil.equals( m_configuration, m_configuration ) );
    }

    public void testAttributeEquals()
    {
        DefaultConfiguration c1 = new DefaultConfiguration("a", "here");
        DefaultConfiguration c2 = new DefaultConfiguration("a", "there");

        c1.setAttribute("test", "test");
        c2.setAttribute("test", "test");

        assertTrue( ConfigurationUtil.equals( c1, c2 ) );
    }

    public void testValueEquals()
    {
        DefaultConfiguration c1 = new DefaultConfiguration("a", "here");
        DefaultConfiguration c2 = new DefaultConfiguration("a", "there");

        c1.setValue("test");
        c2.setValue("test");

        assertTrue( ConfigurationUtil.equals( c1, c2 ) );
    }

    public void testChildrenEquals()
    {
        DefaultConfiguration c1 = new DefaultConfiguration("a", "here");
        DefaultConfiguration k1 = new DefaultConfiguration("b", "wow");
        DefaultConfiguration c2 = new DefaultConfiguration("a", "there");
        DefaultConfiguration k2 = new DefaultConfiguration("c", "wow");
        DefaultConfiguration k3 = new DefaultConfiguration("c", "wow");

        k3.setValue( "bigger stronger faster" );

        k1.setAttribute("test", "test");
        k2.setAttribute("test", "test");

        c1.addChild( k1 );
        c2.addChild( k2 );

        assertTrue( !ConfigurationUtil.equals( c1, c2 ) );

        c1.addChild( k2 );
        c2.addChild( k1 );

        assertTrue( ConfigurationUtil.equals( c1, c2 ) );

        c1.addChild( k2 );
        c1.addChild( k1 );
        c2.addChild( k1 );
        c2.addChild( k2 );
        c1.addChild( k3 );
        c2.addChild( k3 );

        assertTrue( ConfigurationUtil.equals( c1, c2 ) );
    }
}





