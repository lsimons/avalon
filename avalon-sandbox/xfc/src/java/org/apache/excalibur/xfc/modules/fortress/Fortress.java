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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.excalibur.xfc.modules.fortress;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.xfc.Module;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.modules.Constants;

/**
 * <code>Fortress</code> module implementation.
 *
 * <p>
 *  This implementation supports Fortress style role files. ie:
 *
 *  <pre>
 *   &lt;role-list&gt;
 *     &lt;role name=""&gt;
 *       &lt;component shorthand="..." class="..." handler="..."/&gt;
 *     &lt;/role&gt;
 *   &lt;/role-list&gt;
 *  </pre>
 *
 *  and Fortress style configuration files. ie:
 *
 *  <pre>
 *   &lt;shorthand id=".."&gt;
 *     &lt;config-data/&gt;
 *   &lt;/shorthand&gt;
 *  </pre>
 * </p>
 *
 * <p>
 *  The input context should be the name of the roles file, followed
 *  by the name of the configuration file, separated by a colon.
 *  eg: definitions.roles:config.xconf
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Fortress.java,v 1.3 2002/11/12 19:55:28 donaldp Exp $
 */
public class Fortress extends AbstractLogEnabled
    implements Module
{
    private final FortressGenerator m_generator = new FortressGenerator();
    private final FortressSerializer m_serializer = new FortressSerializer();

    /**
     * Generates a {@link Model} based on an a given ECM style
     * Context.
     *
     * <p>
     *  The specified Context string names the Fortress nrole and
     *  xconf files, separated by a ':' character. ie:
     *  <code>fortress.roles:fortress.xconf</code>
     * </p>
     *
     * @param context a <code>String</code> context value
     * @return a {@link Model} instance
     * @exception Exception if an error occurs
     */
    public Model generate( final String context )
        throws Exception
    {
        return m_generator.generate( validateContext( context ) );
    }

    /**
     * Serializes a {@link Model} definition, Fortress style, to an
     * output context.
     *
     * @param model a {@link Model} instance
     * @param context Fortress output Context
     * @exception Exception if an error occurs
     */
    public void serialize( final Model model, final String context )
        throws Exception
    {
        m_serializer.serialize( model, validateContext( context ) );
    }

    /**
     * Helper method to validate the input & output context's
     * given to this module.
     *
     * @param context a <code>String</code> context value
     * @return the validated context string
     * @exception Exception if an error occurs
     */
    private String validateContext( final String context )
        throws Exception
    {
        if( context.indexOf( Constants.CONTEXT_SEPARATOR ) == -1 )
            throw new IllegalArgumentException(
                "Fortress Module requires the role and xconf filename " +
                "separated by a '" + Constants.CONTEXT_SEPARATOR + "' character"
            );

        return context;
    }

    /**
     * Enables logging on this component, including internal
     * components constructed using composition.
     *
     * @param logger a logger instance
     */
    public void enableLogging( final Logger logger )
    {
        super.enableLogging( logger );
        m_generator.enableLogging( logger );
        m_serializer.enableLogging( logger );
    }
}
