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

package org.apache.avalon.phoenix.framework.info;

import java.io.Serializable;

/**
 * This class contains the meta information about a particular
 * component type. It describes;
 *
 * <ul>
 *   <li>Human presentable meta data such as name, version, description etc
 *   useful when assembling the system.</li>
 *   <li>the context object capabilities that this component requires</li>
 *   <li>the services that this component type is capable of providing</li>
 *   <li>the services that this component type requires to operate (and the
 *   names via which services are accessed)</li>
 * </ul>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:07:13 $
 */
public class ComponentInfo
    implements Serializable
{
    /**
     * descriptor for the component.
     */
    private final ComponentDescriptor m_descriptor;

    /**
     * Descriptors for the services exported by component.
     */
    private final ServiceDescriptor[] m_services;

    /**
     * Descriptors for the loggers used by component.
     */
    private final LoggerDescriptor[] m_loggers;

    /**
     * Descriptor for the context (and entrys) used by component.
     */
    private final ContextDescriptor m_context;

    /**
     * Descriptor for the schema of components parameters.
     */
    private final SchemaDescriptor m_configurationSchema;

    /**
     * Descriptor for the schema of components parameters.
     */
    private final SchemaDescriptor m_parametersSchema;

    /**
     * Descriptor for the service dependencies of component.
     */
    private final DependencyDescriptor[] m_dependencies;

    /**
     * Basic constructor that takes as parameters all parts.
     */
    public ComponentInfo( final ComponentDescriptor descriptor,
                          final ServiceDescriptor[] services,
                          final LoggerDescriptor[] loggers,
                          final ContextDescriptor context,
                          final DependencyDescriptor[] dependencies,
                          final SchemaDescriptor configurationSchema,
                          final SchemaDescriptor parametersSchema )
    {
        if( null == descriptor )
        {
            throw new NullPointerException( "descriptor" );
        }
        if( null == services )
        {
            throw new NullPointerException( "services" );
        }
        if( null == loggers )
        {
            throw new NullPointerException( "loggers" );
        }
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }
        if( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        m_descriptor = descriptor;
        m_services = services;
        m_loggers = loggers;
        m_context = context;
        m_dependencies = dependencies;
        m_configurationSchema = configurationSchema;
        m_parametersSchema = parametersSchema;
    }

    /**
     * Return the Component descriptor.
     *
     * @return the Component descriptor.
     */
    public ComponentDescriptor getDescriptor()
    {
        return m_descriptor;
    }

    /**
     * Return the set of Services that this Component is capable of providing.
     *
     * @return the set of Services that this Component is capable of providing.
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * Return the set of Logger that this Component will use.
     *
     * @return the set of Logger that this Component will use.
     */
    public LoggerDescriptor[] getLoggers()
    {
        return m_loggers;
    }

    /**
     * Return the ContextDescriptor for Component, may be null.
     * If null then this component does not implement Contextualizable.
     *
     * @return the ContextDescriptor for Component, may be null.
     */
    public ContextDescriptor getContext()
    {
        return m_context;
    }

    /**
     * Return the schema for the configuration.
     *
     * @return the schema for the configuration.
     */
    public SchemaDescriptor getConfigurationSchema()
    {
        return m_configurationSchema;
    }

    /**
     * Return the schema for the parameters.
     *
     * @return the schema for the parameters.
     */
    public SchemaDescriptor getParametersSchema()
    {
        return m_parametersSchema;
    }

    /**
     * Return the set of Dependencies that this Component requires to operate.
     *
     * @return the set of Dependencies that this Component requires to operate.
     */
    public DependencyDescriptor[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param role the role
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String role )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getKey().equals( role ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }

    /**
     * Retrieve a service matching the supplied classname.
     *
     * @param classname the service classname
     * @return the service descriptor or null if it does not exist
     */
    public ServiceDescriptor getService( final String classname )
    {
        for( int i = 0; i < m_services.length; i++ )
        {
            final String otherClassname = m_services[ i ].getType();
            if( otherClassname.equals( classname ) )
            {
                return m_services[ i ];
            }
        }
        return null;
    }
}
