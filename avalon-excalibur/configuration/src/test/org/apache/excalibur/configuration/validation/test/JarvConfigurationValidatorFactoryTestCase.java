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
package org.apache.excalibur.configuration.validation.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.excalibur.configuration.validation.ConfigurationValidator;
import org.apache.excalibur.configuration.validation.JarvConfigurationValidatorFactory;
import org.apache.excalibur.configuration.validation.ValidationResult;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
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
