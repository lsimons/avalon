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

package org.apache.avalon.phoenix.containerkit.metadata;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.FeatureDescriptor;

/**
 * Each component declared in the application is represented by
 * a ComponentMetaData. Note that this does not necessarily imply
 * that there is only one instance of actual component. The
 * ComponentMetaData could represent a pool of components, a single
 * component or a component prototype that is reused to create
 * new components as needed.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.6 $ $Date: 2003/04/05 04:25:43 $
 */
public class ComponentMetaData
    extends FeatureDescriptor
{
    /**
     * The name of the component. This is an
     * abstract name used during assembly.
     */
    private final String m_name;

    /**
     * The implementationKey for this component.
     * Usually this represents a classname but
     * alternative mechanisms could be used (ie URL
     * of webservice).
     */
    private final String m_implementationKey;

    /**
     * The resolution of any dependencies required by
     * the component type.
     */
    private final DependencyMetaData[] m_dependencies;

    /**
     * The parameters for component (if any).
     */
    private final Parameters m_parameters;

    /**
     * The configuration for component (if any).
     */
    private final Configuration m_configuration;

    /**
     * Create a ComponentMetaData.
     *
     * @param name the abstract name of component meta data instance
     * @param implementationKey the key used to create component (usually a classname)
     * @param dependencies the meta data for any dependencies
     * @param parameters the parameters that the component will be provided (may be null)
     * @param configuration the configuration that the component will be provided (may be null)
     * @param attributes the extra attributes that are used to describe component
     */
    public ComponentMetaData( final String name,
                              final String implementationKey,
                              final DependencyMetaData[] dependencies,
                              final Parameters parameters,
                              final Configuration configuration,
                              final Attribute[] attributes )
    {
        super( attributes );
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == dependencies )
        {
            throw new NullPointerException( "dependencies" );
        }
        if( null == implementationKey )
        {
            throw new NullPointerException( "implementationKey" );
        }

        m_name = name;
        m_dependencies = dependencies;
        m_parameters = parameters;
        m_configuration = configuration;
        m_implementationKey = implementationKey;
    }

    /**
     * Return the name of component metadata.
     *
     * @return the name of the component metadata.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Return the implementationKey for component.
     *
     * @return the implementationKey for component.
     */
    public String getImplementationKey()
    {
        return m_implementationKey;
    }

    /**
     * Return the dependency for component.
     *
     * @return the dependency for component.
     */
    public DependencyMetaData[] getDependencies()
    {
        return m_dependencies;
    }

    /**
     * Return the Parameters for Component (if any).
     *
     * @return the Parameters for Component (if any).
     */
    public Parameters getParameters()
    {
        return m_parameters;
    }

    /**
     * Return the Configuration for Component (if any).
     *
     * @return the Configuration for Component (if any).
     */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Return the dependency for component with specified key.
     *
     * @return the dependency for component with specified key.
     */
    public DependencyMetaData getDependency( final String key )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getKey().equals( key ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }

    /**
     * Return all the dependencies for key. Used for Map and array dependencies.
     *
     * @return all the dependencies for key
     */
    public DependencyMetaData[] getDependencies( final String key )
    {
        final List result = new ArrayList();

        for( int i = 0; i < m_dependencies.length; i++ )
        {
            final DependencyMetaData dependency = m_dependencies[ i ];
            if( dependency.getKey().equals( key ) )
            {
                result.add( dependency );
            }
        }

        return (DependencyMetaData[])result.
            toArray( new DependencyMetaData[ result.size() ] );
    }
}
