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
 * Handles parsing of configuration schema and validation against schema
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/03/22 12:07:14 $
 */
public interface ConfigurationValidator
{
    String ROLE = ConfigurationValidator.class.getName();

    /**
     * Add configuration schema to validator
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param url url that the schema may be located at
     *
     * @throws ConfigurationException if schema is invalid
     */
    void addSchema( String application, String block, String schemaType, String url )
        throws ConfigurationException;

    /**
     * Add configuration schema to validator
     *
     * @param application Application name
     * @param block Block name to store configuration for
     */
    void removeSchema( String application, String block );

    /**
     * Check to see if configuration is feasibly valid. That is, does this configuration match
     * the schema in its current state, but not neccessarily fullfill the requirements of the
     * schema.
     *
     * Implementations are not required to support checking feasibility. If feasibility cannot
     * be checked, the implementation should always return true
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration Configuration to check
     *
     * @return true if configuration is feasibly valid
     *
     * @throws ConfigurationException if no schema is found
     */
    boolean isFeasiblyValid( String application, String block, Configuration configuration )
        throws ConfigurationException;

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
     */
    boolean isValid( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
