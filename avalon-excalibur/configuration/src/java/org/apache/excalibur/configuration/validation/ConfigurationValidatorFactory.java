package org.apache.excalibur.configuration.validation;

import java.io.InputStream;

import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 *
 * @author <a href="proyal@pace2020.com">peter royal</a>
 */
public interface ConfigurationValidatorFactory
{
    String ROLE = ConfigurationValidatorFactory.class.getName();

    /**
     * Add configuration schema to validator
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param url url that the schema may be located at
     *
     * @throws ConfigurationException if schema is invalid
     */
    ConfigurationValidator createValidator( String schemaType, InputStream schema )
        throws ConfigurationException;
}
