/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.excalibur.configuration.test;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.excalibur.configuration.ConfigurationUtil;

import junit.framework.TestCase;

/**
 * Test the ConfigurationUtil class
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
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





