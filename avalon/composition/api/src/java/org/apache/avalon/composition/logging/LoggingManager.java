/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
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
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.composition.logging;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.CategoriesDirective;

/**
 * A <code>LoggerManager</code> that supports the management of a logging hierarchy.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public interface LoggingManager
{
    /**
     * Standard context key for the logging manager.
     */
     String KEY = "urn:assembly:logging";

    /**
     * The default logging priority value.
     */
     String DEFAULT_PRIORITY = "INFO";

    /**
     * The default logging target name.
     */
     String DEFAULT_TARGET = "default";

    /**
     * The default logging format.
     */
     String DEFAULT_FORMAT =
            "[%7.7{priority}] (%{category}): %{message}\\n%{throwable}";

    /**
     * Add a set of category entries using the supplied categories descriptor.
     * @param descriptor a set of category descriptors to be added under the path
     */
    public void addCategories( CategoriesDirective descriptor );

    /**
     * Add a set of category entries relative to the supplied base category
     * path, using the supplied descriptor as the definition of subcategories.
     * @param path the category base path
     * @param descriptor a set of category descriptors to be added under
     *   the base path
     */
    public void addCategories( String path, CategoriesDirective descriptor );

    /**
     * Create a logging channel configured with the supplied category path,
     * priority and target.
     *
     * @param name logging category path
     * @param target the logging target to assign the channel to
     * @param priority the priority level to assign to the channel
     * @return the logging channel
     * @throws Exception if an error occurs
     */
    public Logger getLoggerForCategory(
            final String name, String target, String priority )
            throws Exception;

    /**
     * Configure Logging channel based on the description supplied in a
     * category descriptor.
     *
     * @param category defintion of the channel category, priority and target
     * @return the logging channel
     * @throws Exception if an error occurs
     */
    public Logger getLoggerForCategory( final CategoryDirective category )
            throws Exception;

    /**
     * Return the Logger for the specified category.
     * @param category the category path
     * @return the logging channel
     */
    public Logger getLoggerForCategory( final String category );

}
