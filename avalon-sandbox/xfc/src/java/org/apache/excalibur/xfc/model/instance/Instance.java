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
package org.apache.excalibur.xfc.model.instance;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Abstract base class for representing an instance of a particular component
 * in a configuraiton file.
 *
 * <p>
 *  Subclasses specify concrete types of instance definitions, eg:
 *  those based on role manager, component selector, etc.
 * </p>
 *
 * <p>
 *  {@link InstanceVisitor} defines an interface for traversing groups of
 *  Instance classes.
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Instance.java,v 1.2 2002/11/12 19:55:27 donaldp Exp $
 */
public abstract class Instance
{
    // common instance configuration
    private final Configuration[] m_configuration;
    private final String m_class;
    private final String m_handler;

    /**
     * Creates a new {@link Instance} instance. This constructor creates
     * an instance definition of a particular role, indexed by shorthand name.
     *
     * @param clazz override class, if any
     * @param config instance <code>Configuration</code> as an array, if any
     * @param handler a <code>String</code> value
     */
    public Instance(
        final String clazz,
        final Configuration[] config,
        final String handler
        )
    {
        m_class = clazz;
        m_configuration = config;
        m_handler = handler;
    }

    /**
     * Obtain this Instance's configuration
     *
     * @return a <code>Configuration[]</code> value
     */
    public Configuration[] getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Obtain this Instance's implementing class, or override class name
     *
     * @return implementing class
     */
    public String getClassImpl()
    {
        return m_class;
    }

    /**
     * Obtain this Instance's normalized handler
     *
     * @return normalized handler name
     */
    public String getHandler()
    {
        return m_handler;
    }

    /**
     * Method for accepting an {@link InstanceVisitor} class.
     *
     * @param visitor an {@link InstanceVisitor} value
     * @exception Exception if an error occurs
     */
    public void accept( final InstanceVisitor visitor )
        throws Exception
    {
        throw new UnsupportedOperationException( "This method shouldn't be invoked" );
    }
}
