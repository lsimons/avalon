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
package org.apache.avalon.framework.parameters.test;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * TestCase for Parameter.
 * FIXME: Write messages for each assertion.
 * Writing message in English is very difficult for me :-(.
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class ParameterTestCase
    extends TestCase
{
    private static final String EOL = "\n";

    public ParameterTestCase( final String name )
    {
        super( name );
    }

    public void testRemoveParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );
        assertEquals( 1, parameters.getNames().length );
        parameters.setParameter( "key1", null );
        assertTrue( ! parameters.isParameter( "key1" ) );
        assertEquals( 0, parameters.getNames().length );
    }

    public void testIsParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );
        assertTrue( parameters.isParameter( "key1" ) );
        assertTrue( ! parameters.isParameter( "key2" ) );
    }

    public void testGetParameter()
    {
        final Parameters parameters = new Parameters();
        parameters.setParameter( "key1", "value1" );

        try
        {
            assertEquals( "value1", parameters.getParameter( "key1" ) );
        }
        catch ( final ParameterException pe )
        {
            fail( pe.getMessage() );
        }

        try
        {
            parameters.getParameter( "key2" );
            fail( "Not inserted parameter 'key2' exists" );
        }
        catch( final ParameterException pe )
        {
            //OK
        }

        assertEquals( "value1", parameters.getParameter( "key1", "value1-1" ) );

        assertEquals( "value2", parameters.getParameter( "key2", "value2" ) );
    }

    public void testFromConfiguration()
    {
        final ByteArrayInputStream confInput = new ByteArrayInputStream( (
            "<?xml version=\"1.0\"?>" + EOL +
            "<test>" + EOL +
            "<parameter name=\"key1\" value=\"value1\"/>" + EOL +
            "<parameter name=\"key2\" value=\"value2\"/>" + EOL +
            "<parameter name=\"key3\" value=\"value3\"/>" + EOL +
            "</test>" ).getBytes() );

        try
        {
            final DefaultConfigurationBuilder builder =
                new DefaultConfigurationBuilder();
            final Configuration configuration = builder.build( confInput );

            final Parameters parameters =
                Parameters.fromConfiguration( configuration );

            assertEquals( "value1", parameters.getParameter( "key1" ) );
            assertEquals( "value2", parameters.getParameter( "key2" ) );
            assertEquals( "value3", parameters.getParameter( "key3" ) );
        }
        catch ( final ConfigurationException ce )
        {
            fail( "Converting failed: " + ce.getMessage() );
        }
        catch ( final Exception e )
        {
            fail( e.getMessage() );
        }
    }

    public void testFromProperties()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "value1" );
        properties.put( "key2", "value2" );
        properties.put( "key3", "value3" );

        final Parameters parameters = Parameters.fromProperties( properties );

        try
        {
            assertEquals( "value1", parameters.getParameter( "key1" ) );
            assertEquals( "value2", parameters.getParameter( "key2" ) );
            assertEquals( "value3", parameters.getParameter( "key3" ) );
        }
        catch ( final ParameterException pe )
        {
            fail( pe.getMessage() );
        }
    }
}
