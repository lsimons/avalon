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
package org.apache.avalon.fortress.impl;

import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * This is the default implementation of {@link org.apache.avalon.fortress.Container},
 * adding configuration markup semantics to the {@link AbstractContainer}.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/03/19 12:55:46 $
 */
public class DefaultContainer
    extends AbstractContainer
    implements Configurable
{
    /**
     * <p>Process the configuration and set up the components and their
     * mappings. At this point, all components are prepared and all mappings
     * are made. However, nothing is initialized.</p>
     *
     * <p>The native Configuration format follows a specific convention.  If
     * you use a RoleManager to map roles and implementations to more helpful
     * names, we will internally rewrite the configuration to match this
     * format.  Please note: If a configuration element does
     * <strong>not</strong> have a unique id, it will not be treated as a
     * Component.  That ID is used as the hint when there is more than one
     * implementation of a role.</p>
     *
     * <pre>
     *   &lt;component role="org.apache.avalon.excalibur.datasource.DataSourceComponent"
     *                 id="default-connection"
     *                 class="org.apache.avalon.excalibur.datasource.JdbcDataSourceComponent"
     *                 handler="org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler"&gt;
     *
     *    &lt;!-- Component specific configuration --&gt;
     *
     *  &lt;/component&gt;
     * </pre>
     *
     * @param config  The configuration element to translate into the
     *                       list of components this impl managers.
     *
     * @throws ConfigurationException if the configuration is not valid
     */
    public void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration[] elements = config.getChildren();
        for( int i = 0; i < elements.length; i++ )
        {
            final Configuration element = elements[ i ];
            final String hint = element.getAttribute( "id", null );
            if( null == hint )
            {
                // Only components with an id attribute are treated as components.
                getLogger().debug( "Ignoring configuration for component, " + element.getName()
                                   + ", because the id attribute is missing." );
            }
            else
            {
                final String classname = getClassname( element );
                final boolean isLazy = isLazyComponentHandler( element );
                final ComponentHandlerMetaData metaData =
                    new ComponentHandlerMetaData( hint, classname, element, isLazy );

                try
                {
                    addComponent( metaData );
                }
                catch( Exception e )
                {
                    throw new ConfigurationException( "Could not add component", e );
                }
            }
        }
    }

    /**
     * Retrieve the classname for component configuration.
     *
     * @param config the component configuration
     * @return the class name
     */
    private String getClassname( final Configuration config )
        throws ConfigurationException
    {
        if( "component".equals( config.getName() ) )
        {
            return config.getAttribute( "class" );
        }
        else
        {
            final RoleEntry roleEntry = m_roleManager.getRoleForShortName( config.getName() );
            if( null == roleEntry )
            {
                final String message = "No class found matching configuration name " +
                    "[name: " + config.getName() + ", location: " + config.getLocation() + "]";
                throw new ConfigurationException( message );
            }
            return roleEntry.getComponentClass().getName();
        }
    }

    /**
     * Helper method to determine whether a given component handler
     * configuration requests a lazy or startup based initialization policy.
     *
     * @param component <code>Configuration</code>
     *
     * @return true if the given handler configuration specifies a lazy init
     *         policy, false otherwise
     *
     * @throws java.lang.IllegalArgumentException if the handler specifies an unknown init
     *         policy
     */
    private boolean isLazyComponentHandler( Configuration component )
    {
        String policy = component.getAttribute( "activation", "startup" );

        final boolean isLazy = "request".equalsIgnoreCase( policy );
        final boolean isNonLazy = "startup".equalsIgnoreCase( policy );

        if( isNonLazy )
        {
            return false;
        }
        else if( isLazy )
        {
            return true;
        }
        else
        {
            // policy was not null, but didn't match anything above
            final String classname = component.getAttribute( "class", null );

            final String message =
                "Unknown activation policy for class " + classname + ": " + policy;
            throw new IllegalArgumentException( message );
        }
    }

    /**
     * Return the ServiceManager that exposes all the services in impl.
     *
     * @return the ServiceManager that exposes all the services in impl.
     */
    public ServiceManager getServiceManager()
    {
        return super.getServiceManager();
    }
}
