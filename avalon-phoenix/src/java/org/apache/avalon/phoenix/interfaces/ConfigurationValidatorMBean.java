/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Management interface to the Configuration Validator
 *
 * @phoenix:mx-topic name="ConfigurationValidator"
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see ConfigurationValidator
 */
public interface ConfigurationValidatorMBean
{
    /**
     * Get the schema type for the specified application and block. Returns
     * null if no schema
     *
     * @param application to get schema for
     * @param block to get schema for
     * @return schema type, or null if none exists
     * @phoenix:mx-operation
     */
    String getSchemaType( final String application, final String block );

    /**
     * Get the XML that represents the schema for the specified application and block.
     * Returns null if no schema.
     *
     * @param application to get schema for
     * @param block to get schema for
     * @return schema as string, or null if none exists
     * @phoenix:mx-operation
     */
    String getSchema( final String application, final String block );

    /**
     * Check to see if configuration is valid.
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration Configuration to check
     *
     * @return true if configuration is valid
     *
     * @throws ConfigurationException if no schema is found
     *
     * @see ConfigurationValidator#isValid
     *
     * @phoenix:mx-operation
     */
    boolean isValid( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
