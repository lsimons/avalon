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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Instance;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.RoleRef;

import org.apache.excalibur.xfc.modules.ecm.ECMGenerator;

/**
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: FortressGenerator.java,v 1.1 2002/10/16 16:20:38 crafterm Exp $
 */
public class FortressGenerator extends ECMGenerator
{
    // ROLE GENERATION METHODS

    /**
     * Method to determine whether the given role definition entails
     * a ComponentSelector style definition or not.
     *
     * @param role a <code>Configuration</code> value
     * @return a <code>boolean</code> value
     */
    protected boolean isComponentSelectorRole( final Configuration role )
    {
        // if we have more than 1 'component'we have a component selector
        return role.getChildren( COMPONENT ).length > 1;
    }

    /**
     * Method to create a {@link RoleRef} object for a Configuration 
     * definition that defines a single component based role.
     *
     * @param role a <code>Configuration</code> definition of a role
     * @return a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    protected RoleRef buildSingleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Configuration config = role.getChild( COMPONENT );
        Definition definition =
            new Definition(
                config.getAttribute( CLASS ), 
                config.getAttribute( SHORTHAND ),
                HandlerMapper.getHandler( config.getAttribute( HANDLER ) )
            );

        return new RoleRef(
            role.getAttribute( NAME ), config.getAttribute( SHORTHAND ), definition
        );
    }

    /**
     * Method to construct a {@link RoleRef} object from a Configuration
     * definition that defines a multiple component based role.
     *
     * @param role a <code>Configuration</code> definition of a role
     * @return a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    protected RoleRef buildMultipleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Configuration[] hints = role.getChildren( COMPONENT );
        Definition[] definitions = new Definition[ hints.length ];

        for ( int i = 0; i < hints.length; ++i )
        {
            definitions[i] =
                new Definition(
                    hints[i].getAttribute( CLASS ),
                    hints[i].getAttribute( SHORTHAND ),
                    HandlerMapper.getHandler( hints[i].getAttribute( HANDLER ) )
                );
        }

        return new RoleRef( role.getAttribute( NAME ), "UNKNOWN", definitions );
    }

    // INSTANCE GENERATION METHODS

    /**
     * Builds an Instance object from a Configuration snippet.
     *
     * @param i a <code>Configuration</code> value
     * @return an <code>Instance</code> value
     * @exception Exception if an error occurs
     */
    protected Instance buildInstance( final Configuration i )
        throws Exception
    {
        if ( i.getName().equals( COMPONENT ) )
        {
            // build non-role component
            return buildNonRoleComponentInstance( i );
        }

        // build role based component
        return buildRoleComponentInstance( i );
    }

    /**
     * Builds an Instance object from a Configuration snippet,
     * desribing a non-role manager component.
     *
     * @param i a <code>Configuration</code> value
     * @return an <code>Instance</code> value
     * @exception Exception if an error occurs
     */
    private Instance buildNonRoleComponentInstance( final Configuration i )
        throws Exception
    {
        return new Instance(
            i.getChildren(),
            i.getAttribute( CLASS ),
            i.getAttribute( ROLE ),
            null
        );
    }

    /**
     * Builds an Instance object from a Configuration snippet,
     * describing a role manager based component.
     *
     * @param i a <code>Configuration</code> value
     * @return an <code>Instance</code> value
     * @exception Exception if an error occurs
     */
    private Instance buildRoleComponentInstance( final Configuration i )
        throws Exception
    {
        return new Instance(
            i.getName(),
            i.getChildren(),
            null,
            null
        );
    }
}
