/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 2002,2003 The Apache Software Foundation. All rights reserved.
 
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
package org.apache.avalon.framework.configuration.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
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

    public void testGetValueAsBoolen()
        throws Exception
    {
        final boolean b = true;
        m_configuration.setValue("TrUe");
        assertEquals( b, m_configuration.getValueAsBoolean() );
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
    
    public void testCopying() throws Exception
    {
        DefaultConfiguration root = new DefaultConfiguration( "root", "0:0", "http://root", "root" );
        root.setAttribute( "attr1", "1" );
        root.setAttribute( "attr2", "2" );
                
        DefaultConfiguration child1 = new DefaultConfiguration( "child1", "0:1", "http://root/child1", "child1" );
        DefaultConfiguration child2 = new DefaultConfiguration( "child2", "0:2", "http://root/child2", "child2" );
        
        root.addChild( child1 );
        root.addChild( child2 );
        
        root.makeReadOnly();
        
        DefaultConfiguration modifiableRoot = new DefaultConfiguration( root );
        assertTrue( ConfigurationUtil.equals( root, modifiableRoot ) );
        
        modifiableRoot.setAttribute( "attr1", "0" );
        
        assertEquals( "0", modifiableRoot.getAttribute( "attr1" ) );
        
        DefaultConfiguration modifiableChild1 = new DefaultConfiguration( root.getChild("child1") );
        modifiableChild1.setValue( "1" );
        
        modifiableRoot.removeChild( modifiableRoot.getChild("child1") );
        modifiableRoot.addChild( modifiableChild1 );
        
        assertEquals( "1", modifiableRoot.getChild( "child1" ).getValue() );
    }
    
    public void testConvenienceSetters() throws Exception
    {
        DefaultConfiguration config = new DefaultConfiguration( "root", "0:0", "http://root", "root" );
        config.setAttribute( "integer", 12 );
        config.setAttribute( "long", 8000000000L );
        config.setAttribute( "float", 1.23f );
        config.setAttribute( "boolean", true );
        config.setAttribute( "string", "string" );
        
        assertEquals( "12", config.getAttribute("integer") );
        assertEquals( "8000000000", config.getAttribute("long") );
        assertEquals( 1.23, config.getAttributeAsFloat("float"), 0.01 );
        assertEquals( "string", config.getAttribute("string") );
        assertEquals( "true", config.getAttribute("boolean") );
        
        assertEquals( 12, config.getAttributeAsInteger("integer") );
        assertEquals( 8000000000L, config.getAttributeAsLong("long") );
        assertEquals( "string", config.getAttribute("string") );
        assertEquals( true, config.getAttributeAsBoolean("boolean") );
    }
}





