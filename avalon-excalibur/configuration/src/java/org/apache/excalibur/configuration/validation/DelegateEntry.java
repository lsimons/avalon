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
package org.apache.excalibur.configuration.validation;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Configuration Validator entry for the DelegatingConfigurationValidator.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
class DelegateEntry
{
    private final String m_schemaType;
    private final String m_className;
    private final Configuration m_configuration;
    private ConfigurationValidatorFactory m_validatorFactory;

    public DelegateEntry( String schemaType, String className, Configuration configuration )
    {
        m_className = className;
        m_configuration = configuration;
        m_schemaType = schemaType;
    }

    public String getSchemaType()
    {
        return m_schemaType;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    public String getClassName()
    {
        return m_className;
    }

    public ConfigurationValidatorFactory getValidatorFactory()
    {
        return m_validatorFactory;
    }

    public void setValidatorFactory( ConfigurationValidatorFactory validatorFactory )
    {
        m_validatorFactory = validatorFactory;
    }
}
