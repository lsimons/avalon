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
package org.apache.excalibur.xfc.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Class which represents an instance of a particular component in an xconf file.
 *
 * <p>
 *  This class can contain a:
 *
 *  <ul>
 *   <li>Single role based instance definition.
 *   <li>Single non-role based instance definition (ie. complete role & instance 
 *  definition).
 *   <li>Multi role based instance definition (ie. role based component selector
 *  definition).
 *   <li>Multi non-role based instance definition (ie. complete role & sub instance
 *  definition of a component selector).
 *  </ul>
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Instance.java,v 1.1 2002/10/14 16:17:50 crafterm Exp $
 */
public final class Instance
{
    // instance configuration
    private final Configuration[] m_configuration;
    private final String m_shorthand;

    // instance roles/override class/and subinstance definitions, if any
    private final String m_class;
    private final String m_role;
    private final Instance[] m_subinstances;
    private final String m_handler;

    /**
     * Creates a new {@link Instance} instance. This constructor creates
     * an instance definition of a particular role, indexed by shorthand name.
     *
     * @param shorthand shorthand name
     * @param config instance <code>Configuration</code> as an array, if any
     * @param clazz override class, if any
     */
    public Instance(
        final String shorthand,
        final Configuration[] config,
        final String clazz,
        final String handler
    )
    {
        m_configuration = config;
        m_shorthand = shorthand;
        m_class = clazz;
        m_role = null;
        m_subinstances = null;
        m_handler = handler;
    }

    /**
     * Creates a new {@link Instance} object for role
     * based ComponentSelector.
     *
     * @param shorthand a <code>String</code> value
     * @param subinstances an <code>Instance[]</code> value
     */
    public Instance(
        final String shorthand,
        final Instance[] subinstances
    )
    {
        m_configuration = null;
        m_shorthand = shorthand;
        m_subinstances = subinstances;
        m_class = null;
        m_role = null;
        m_handler = null;
    }

    /**
     * Creates a new {@link Instance} instance. This constructor creates
     * an instance definition of a given role.
     *
     * @param config instance <code>Configuration</code> as an array, if any
     * @param clazz component implementation class name
     * @param role role name
     */
    public Instance(
        final Configuration[] config,
        final String clazz,
        final String role,
        final String handler
    )
    {
        m_configuration = config;
        m_class = clazz;
        m_role = role;
        m_shorthand = null;
        m_subinstances = null;
        m_handler = handler;
    }

    /**
     * Creates a new {@link Instance} instance. This constructor creates
     * an instance definition of a given role that contains sub instances
     * (ie. non role manager component selector definitions).
     *
     * @param clazz implementing class name
     * @param role role name
     * @param subinstances an <code>Instance[]</code> array
     */
    public Instance(
        final String clazz,
        final String role,
        final Instance[] subinstances,
        final String handler
    )
    {
        m_class = clazz;
        m_role = role;
        m_subinstances = subinstances;
        m_configuration = null;
        m_shorthand = null;
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
     * @return a <code>String</code> value
     */
    public String getClassImpl()
    {
        return m_class;
    }

    /**
     * Obtain this Instance's role name
     *
     * @return a <code>String</code> value
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Obtain this Instance's shorthand name
     *
     * @return a <code>String</code> value
     */
    public String getShorthand()
    {
        return m_shorthand;
    }

    /**
     * Obtain this Instance's list of sub instances.
     *
     * @return an <code>Instance[]</code> value
     */
    public Instance[] getSubInstances()
    {
        return m_subinstances;
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
}
