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

package org.apache.avalon.meta.info;

import java.io.Serializable;

import org.apache.avalon.framework.configuration.Configuration;

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
 *   <li>extended lifecycle stages that this component uses</li>
 * </ul>
 *
 * <p><b>UML</b></p>
 * <p><image src="doc-files/Type.gif" border="0"/></p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 08:15:11 $
 */
public class Type implements Serializable
{
    private final InfoDescriptor m_descriptor;
    private final ContextDescriptor m_context;
    private final Configuration m_configuration;
    private final ServiceDescriptor[] m_services;
    private final DependencyDescriptor[] m_dependencies;
    private final CategoryDescriptor[] m_loggers;
    private final StageDescriptor[] m_stages;
    private final ExtensionDescriptor[] m_extensions;

    /**
     * Creation of a new Type instance using a supplied component descriptor,
     * logging, cotext, services, depedencies, stages and extension descriptors.
     * @param descriptor a component descriprot that contains information about
     *   the component type
     * @param loggers a set of logger descriptors the declare the logging channels
     *   required by the type
     * @param context a component context descriptor that declares the context type
     *   and context entry key and value classnames
     * @param services a set of service descriptors that detail the service that
     *   this component type is capable of supplying
     * @param dependencies a set of depedency descriptors that detail the service
     *   that this component type is depedent on
     * @param stages a set of stage descriprors that detail the extension stage
     *   interfaces that this component requires a handler for
     * @param extensions a set of lifecycle extension capabilities that this
     *   componet can provide to its container during the process of stage
     *   suppier resolution
     * @exception NullPointerException if the descriptor, loggers, context, services,
     *   dependencies, stages, or extensions argument is null
     */
    public Type( final InfoDescriptor descriptor,
                 final CategoryDescriptor[] loggers,
                 final ContextDescriptor context,
                 final ServiceDescriptor[] services,
                 final DependencyDescriptor[] dependencies,
                 final StageDescriptor[] stages,
                 final ExtensionDescriptor[] extensions )
            throws NullPointerException
    {
        this( descriptor, loggers, context, services, dependencies, stages, extensions, null );
    }

    /**
     * Creation of a new Type instance using a supplied component descriptor,
     * logging, cotext, services, depedencies, stages and extension descriptors.
     * @param descriptor a component descriprot that contains information about
     *   the component type
     * @param loggers a set of logger descriptors the declare the logging channels
     *   required by the type
     * @param context a component context descriptor that declares the context type
     *   and context entry key and value classnames
     * @param services a set of service descriprors that detail the service that
     *   this component type is capable of supplying
     * @param dependencies a set of depedency descriprors that detail the service
     *   that this component type is depedent on
     * @param stages a set of stage descriprors that detail the extensiuon stage
     *   interfaces that this component requires a handler for
     * @param extensions a set of lifecycle extension capabilities that this
     *   componet can provide to its container during the process of stage
     *   suppier resolution
     * @exception NullPointerException if the descriptor, loggers, context, services,
     *   dependencies, stages, or extensions argument is null
     */
    public Type( final InfoDescriptor descriptor,
                 final CategoryDescriptor[] loggers,
                 final ContextDescriptor context,
                 final ServiceDescriptor[] services,
                 final DependencyDescriptor[] dependencies,
                 final StageDescriptor[] stages,
                 final ExtensionDescriptor[] extensions,
                 final Configuration defaults )
            throws NullPointerException
    {
        if ( null == descriptor )
        {
            throw new NullPointerException( "descriptor" );
        }
        if ( null == loggers )
        {
            throw new NullPointerException( "loggers" );
        }
        if ( null == context )
        {
            throw new NullPointerException( "context" );
        }
        if ( null == services )
        {
            throw new NullPointerException( "services" );
        }
        if ( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        if ( null == stages )
        {
            throw new NullPointerException( "stages" );
        }
        if ( null == extensions )
        {
            throw new NullPointerException( "extensions" );
        }

        m_descriptor = descriptor;
        m_loggers = loggers;
        m_context = context;
        m_services = services;
        m_dependencies = dependencies;
        m_stages = stages;
        m_extensions = extensions;
        m_configuration = defaults;
    }

    /**
     * Return the Component descriptor.
     *
     * @return the Component descriptor.
     */
    public InfoDescriptor getInfo()
    {
        return m_descriptor;
    }

    /**
     * Return the set of Logger that this Component will use.
     *
     * @return the set of Logger that this Component will use.
     */
    public CategoryDescriptor[] getCategories()
    {
        return m_loggers;
    }

    /**
     * Return TRUE if the set of Logger descriptors includes the supplied name.
     *
     * @param name the logging subcategory name
     * @return TRUE if the logging subcategory is declared.
     */
    public boolean isaCategory( String name )
    {
        CategoryDescriptor[] loggers = getCategories();
        for( int i = 0; i < loggers.length; i++ )
        {
            CategoryDescriptor logger = loggers[ i ];
            if( logger.getName().equals( name ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the ContextDescriptor for component, may be null.
     * If null then this component does not implement Contextualizable.
     *
     * @return the ContextDescriptor for component, may be null.
     */
    public ContextDescriptor getContext()
    {
        return m_context;
    }

    /**
     * Return the set of Services that this component is capable of providing.
     *
     * @return the set of Services that this component is capable of providing.
     */
    public ServiceDescriptor[] getServices()
    {
        return m_services;
    }

    /**
     * Retrieve a service with a particular reference.
     *
     * @param reference a service reference descriptor
     * @return the service descriptor or null if it does not exist
     */
    public ServiceDescriptor getService( final ReferenceDescriptor reference )
    {
        for ( int i = 0; i < m_services.length; i++ )
        {
            final ServiceDescriptor service = m_services[i];
            if ( service.getReference().matches( reference ) )
            {
                return service;
            }
        }
        return null;
    }

    /**
     * Retrieve a service with a particular classname.
     *
     * @param classname the service classname
     * @return the service descriptor or null if it does not exist
     */
    public ServiceDescriptor getService( final String classname )
    {
        for ( int i = 0; i < m_services.length; i++ )
        {
            final ServiceDescriptor service = m_services[i];
            if ( service.getReference().getClassname().equals( classname ) )
            {
                return service;
            }
        }
        return null;
    }

    /**
     * Return the set of Dependencies that this component requires to operate.
     *
     * @return the set of Dependencies that this component requires to operate.
     */
    public DependencyDescriptor[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Retrieve a dependency with a particular role.
     *
     * @param key the service key
     * @return the dependency or null if it does not exist
     */
    public DependencyDescriptor getDependency( final String key )
    {
        for ( int i = 0; i < m_dependencies.length; i++ )
        {
            if ( m_dependencies[i].getKey().equals( key ) )
            {
                return m_dependencies[i];
            }
        }
        return null;
    }

    /**
     * Returns the default configuration supplied with the type.
     *
     * @return the default configuration or null if no packaged defaults
     */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Return the lifecycle stages extensions required by this component type.
     *
     * @return an array of stage descriptors.
     */
    public StageDescriptor[] getStages()
    {
        return m_stages;
    }

    /**
     * Return the stages extension handling provided by this extension.
     *
     * @return an array of extension descriptors.
     */
    public ExtensionDescriptor[] getExtensions()
    {
        return m_extensions;
    }

    /**
     * Return the extension supporting the supplied stage.
     *
     * @param stage the lifecycle stage that this type requires a handler for
     * @return a matching extension or null if no matching extension
     */
    public ExtensionDescriptor getExtension( StageDescriptor stage )
    {
        return getExtension( stage.getKey() );
    }

    /**
     * Return the extension supporting the supplied stage.
     *
     * @param key the lifecycle stage that this type requires a handler for
     * @return a matching extension or null if no matching extension
     */
    public ExtensionDescriptor getExtension( String key )
    {
        ExtensionDescriptor[] extensions = getExtensions();
        for ( int i = 0; i < extensions.length; i++ )
        {
            ExtensionDescriptor extension = extensions[i];
            String ref = extension.getKey();
            if ( key.equals( ref ) )
            {
                return extension;
            }
        }
        return null;
    }

    /**
     * Return a string representation of the type.
     * @return the stringified type
     */
    public String toString()
    {
        return getInfo().toString();
    }

   /**
    * Test is the supplied object is equal to this object.
    * @return true if the object are equivalent
    */
    public boolean equals(Object other)
    {
        boolean isEqual = other instanceof Type;
        isEqual = isEqual && m_descriptor.equals(((Type)other).m_descriptor);
        isEqual = isEqual && m_configuration.equals(((Type)other).m_configuration);
        isEqual = isEqual && m_context.equals(((Type)other).m_context);
        for( int i=0; i<m_loggers.length; i++ )
        {
            isEqual = isEqual && m_loggers[i].equals(((Type)other).m_loggers[i]);
        }
        for( int i=0; i<m_services.length; i++ )
        {
            isEqual = isEqual && m_services[i].equals(((Type)other).m_services[i]);
        }
        for( int i=0; i<m_dependencies.length; i++ )
        {
            isEqual = isEqual && m_dependencies[i].equals(((Type)other).m_dependencies[i]);
        }
        for( int i=0; i<m_stages.length; i++ )
        {
            isEqual = isEqual && m_stages[i].equals(((Type)other).m_stages[i]);
        }
        for( int i=0; i<m_extensions.length; i++ )
        {
            isEqual = isEqual && m_extensions[i].equals(((Type)other).m_extensions[i]);
        }
        return isEqual;
    }

   /**
    * Return the hashcode for the object.
    * @return the hashcode value
    */
    public int hashCode()
    {
        int hash = m_descriptor.hashCode();
        hash >>>= 13;
        hash ^= m_context.hashCode();
        hash >>>= 13;
        if( m_configuration != null )
        { 
            hash ^= m_context.hashCode();
            hash >>>= 13;
        }
        hash >>>= 13;
        for( int i=0; i<m_services.length; i++ )
        {
            hash ^= m_services[i].hashCode();
            hash >>>= 13;
        }
        for( int i=0; i<m_dependencies.length; i++ )
        {
            hash ^= m_dependencies[i].hashCode();
            hash >>>= 13;
        }
        for( int i=0; i<m_loggers.length; i++ )
        {
            hash ^= m_loggers[i].hashCode();
            hash >>>= 13;
        }
        for( int i=0; i<m_stages.length; i++ )
        {
            hash ^= m_stages[i].hashCode();
            hash >>>= 13;
        }
        for( int i=0; i<m_extensions.length; i++ )
        {
            hash ^= m_extensions[i].hashCode();
            hash >>>= 13;
        }
        return hash;
    }
}
