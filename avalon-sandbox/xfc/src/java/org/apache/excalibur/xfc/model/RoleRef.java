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

/**
 * Class for maintaining a 1-N list of references between Roles & Components.
 *
 * <p>
 *  Normally there is a 1 Component per role definition, but in the of a
 *  ComponentSelector there can be more.
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: RoleRef.java,v 1.4 2002/10/14 16:17:50 crafterm Exp $
 */
public final class RoleRef
{
    // internals
    private final List m_definitions = new ArrayList();
    private final String m_role;
    private final String m_shorthand;

    /**
     * Creates a new <code>RoleRef</code> instance.
     *
     * @param role role name
     * @param shorthand shorthand name
     * @param definition a {@link Definition} instance
     */
    public RoleRef( final String role, final String shorthand, final Definition definition )
    {
        m_role = role;
        m_shorthand = shorthand;
        m_definitions.add( definition );
    }

    /**
     * Creates a new <code>RoleRef</code> instance.
     *
     * @param role role name
     * @param shorthand shorthand name
     * @param definitions a {@link Definition} array
     */
    public RoleRef( final String role, final String shorthand, final Definition[] definitions )
    {
        m_role = role;
        m_shorthand = shorthand;

        for ( int i = 0; i < definitions.length; ++i )
        {
            m_definitions.add( definitions[i] );
        }
    }

    /**
     * Obtain the role this ref object manages
     *
     * @return a <code>String</code> value
     */
    public String getRole()
    {
        return m_role;
    }

    /**
     * Obtain the shorthand name of this role
     *
     * @return a <code>String</code> value
     */
    public String getShorthand()
    {
        return m_shorthand;
    }

    /**
     * Obtain a list of all Definition objects that provide the role
     * this roleref manages.
     *
     * @return a {@link Definition}[] array
     */
    public Definition[] getProviders()
    {
        return (Definition[]) m_definitions.toArray(
            new Definition[ m_definitions.size() ]
        );
    }
}
