/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.configuration.validation.test;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.excalibur.configuration.validation.ConfigurationValidator;
import org.apache.excalibur.configuration.validation.JarvConfigurationValidatorFactory;
import org.apache.excalibur.configuration.validation.ValidationResult;

import junit.framework.TestCase;

/**
 *
 * @author <a href="proyal@apache.org">peter royal</a>
 */
public class JarvConfigurationValidatorFactoryTestCase extends TestCase
{
    private JarvConfigurationValidatorFactory m_factory;
    private DefaultConfiguration m_configuration;

    public JarvConfigurationValidatorFactoryTestCase()
    {
        this( "JarvConfigurationValidatorFactoryTestCase" );
    }

    public JarvConfigurationValidatorFactoryTestCase( String s )
    {
        super( s );
    }

    public void setUp() throws Exception
    {
        m_configuration = new DefaultConfiguration( "a", "b" );
        m_configuration.setAttribute( "test", "test" );
        m_configuration.setValue( "test" );

        m_factory = new JarvConfigurationValidatorFactory();
        m_factory.enableLogging( new ConsoleLogger() );
        m_factory.configure( createConfiguration() );
        m_factory.initialize();
    }

    private Configuration createConfiguration() throws Exception
    {
        final DefaultConfiguration c = new DefaultConfiguration( "validator", "0" );
        final DefaultConfiguration child = new DefaultConfiguration( "schema-language", "1" );

        c.setAttribute( "schema-type", "relax-ng" );
        child.setValue( "http://relaxng.org/ns/structure/1.0" );

        c.addChild( child );

        c.makeReadOnly();

        return c;
    }

    public void tearDowm()
    {
        m_configuration = null;
    }

    public void testValidConfiguration()
        throws Exception
    {
        final ConfigurationValidator validator =
            m_factory.createValidator(
                "relax-ng",
                this.getClass().getResourceAsStream( "valid.rng" ) );

        final ValidationResult result = validator.isValid( m_configuration );

        System.out.println( "(bad) testValidConfiguration.warning: " + result.getWarnings() );
        System.out.println( "(bad) testValidConfiguration.errors: " + result.getErrors() );

        assertEquals( "failure!!", true, result.isValid() );
    }

    public void testInvalidConfiguration()
        throws Exception
    {
        final ConfigurationValidator validator =
            m_factory.createValidator(
                "relax-ng",
                this.getClass().getResourceAsStream( "invalid.rng" ) );

        final ValidationResult result = validator.isValid( m_configuration );

        System.out.println( "(expected) testInvalidConfiguration.warning: " + result.getWarnings() );
        System.out.println( "(expected) testInvalidConfiguration.errors: " + result.getErrors() );

        assertEquals( false, result.isValid() );
    }
}
