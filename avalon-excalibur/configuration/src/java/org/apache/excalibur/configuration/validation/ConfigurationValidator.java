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
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public interface ConfigurationValidator
{
    /**
     * Check to see if configuration is feasibly valid. That is, does this configuration match
     * the schema in its current state, but not neccessarily fullfill the requirements of the
     * schema.
     *
     * Implementations are not required to support checking feasibility. If feasibility cannot
     * be checked, the implementation should always return true
     *
     * @param configuration Configuration to check
     *
     * @return ValidationResult containing results of validation
     *
     * @throws ConfigurationException if no schema is found
     */
    ValidationResult isFeasiblyValid( Configuration configuration )
        throws ConfigurationException;

    /**
     * Check to see if configuration is valid.
     *
     * @param configuration Configuration to check
     *
     * @return ValidationResult containing results of validation
     *
     * @throws ConfigurationException if no schema is found
     */
    ValidationResult isValid( Configuration configuration )
        throws ConfigurationException;
}
