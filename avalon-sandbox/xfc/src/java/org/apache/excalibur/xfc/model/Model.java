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

import org.apache.excalibur.xfc.model.role.RoleRef;
import org.apache.excalibur.xfc.model.instance.Instance;

/**
 * Class for maintaining the Roles and their providing Components
 * a particular Container configuration.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Model.java,v 1.4 2002/10/17 14:38:17 crafterm Exp $
 */
public final class Model
{
    private final List m_definitions = new ArrayList();
    private final List m_instances = new ArrayList();

    /**
     * Adds a new {@link RoleRef} object to the system. A
     * {@link RoleRef} object contains the definition of a particular
     * role and all {@link Definition}'s that provide it.
     *
     * @param role a {@link RoleRef} instance
     */
    public void addRoleRef( final RoleRef role )
    {
        m_definitions.add( role );
    }

    /**
     * Adds a new {@link Instance} object to the system. An
     * {@link Instance} object contains the definition of a
     * particular instance of a role.
     *
     * @param instance an {@link Instance} instance
     */
    public void addInstance( final Instance instance )
    {
        m_instances.add( instance );
    }

    /**
     * Obtain all definitions this Model contains, as an
     * array of {@link RoleRef} objects.
     *
     * @return a {@link RoleRef}[] array
     */
    public RoleRef[] getDefinitions()
    {
        return (RoleRef[]) m_definitions.toArray(
            new RoleRef[ m_definitions.size() ]
        );
    }

    /**
     * Method to locate a {@link RoleRef} object by shorthand name.
     *
     * @param shorthand shorthand name
     * @return a {@link RoleRef} object or null if none could be found
     */
    public RoleRef findByShorthand( final String shorthand )
    {
        RoleRef[] refs = getDefinitions();

        for ( int i = 0; i < refs.length; ++i )
        {
            if ( refs[i].getShorthand().equals( shorthand ) )
                return refs[i];
        }

        return null;
    }

    /**
     * Obtain all instances this Model contains, as an
     * array of {@link Instance} objects.
     *
     * @return an @link Instance}[] array
     */
    public Instance[] getInstances()
    {
        return (Instance[]) m_instances.toArray(
            new Instance[ m_instances.size() ]
        );
    }
}
