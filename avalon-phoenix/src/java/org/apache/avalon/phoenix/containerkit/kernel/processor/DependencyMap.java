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

package org.apache.avalon.phoenix.containerkit.kernel.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.avalon.phoenix.framework.info.DependencyDescriptor;
import org.apache.avalon.phoenix.containerkit.kernel.ComponentStore;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.avalon.phoenix.containerkit.metadata.DependencyMetaData;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;

/**
 * Utility class to help aquire a ordered graph of
 * consumers and providers for specific components.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.6 $ $Date: 2003/03/22 12:07:11 $
 */
public class DependencyMap
{
    /**
     * Get the serilized graph of {@link ComponentProfile} objects
     * required when starting up all the components. This makes sure
     * that all providers occur before their coresponding
     * consumers in graph.
     *
     * @return the ordered list of components
     */
    public ComponentProfile[] getStartupGraph( final ComponentStore store )
    {
        return walkGraph( true, store );
    }

    /**
     * Get the serilized graph of {@link ComponentProfile} objects
     * required when shutting down all the components. This makes
     * sure that all consumers occur before their coresponding
     * providers in graph.
     *
     * @return the ordered list of components
     */
    public ComponentProfile[] getShutdownGraph( final ComponentStore store )
    {
        return walkGraph( false, store );
    }

    /**
     * Get the serilized graph of {@link ComponentProfile} objects
     * that use services of specified component.
     *
     * @param component the component
     * @return the ordered list of consumers
     */
    public ComponentProfile[] getConsumerGraph( final ComponentProfile component,
                                                final ComponentStore store )
    {
        return getComponentGraph( component, false, store );
    }

    /**
     * Get the serilized graph of {@link ComponentProfile} objects
     * that provide specified component with services.
     *
     * @param component the component
     * @return the ordered list of providers
     */
    public ComponentProfile[] getProviderGraph( final ComponentProfile component,
                                                final ComponentStore store )
    {
        return getComponentGraph( component, true, store );
    }

    /**
     * Get the graph of a single component.
     *
     * @param component the component
     * @param providers true if traversing providers, false if consumers
     * @return the list of components in graph
     */
    private ComponentProfile[] getComponentGraph( final ComponentProfile component,
                                                  final boolean providers,
                                                  final ComponentStore store )
    {
        final ArrayList result = new ArrayList();
        visitcomponent( component,
                        providers,
                        new ArrayList(),
                        result,
                        store );

        final ComponentProfile[] returnValue = new ComponentProfile[ result.size() ];
        return (ComponentProfile[])result.toArray( returnValue );
    }

    /**
     * Method to generate an ordering of nodes to traverse.
     * It is expected that the specified components have passed
     * verification tests and are well formed.
     *
     * @param providers true if forward dependencys traced, false if dependencies reversed
     * @return the ordered node names
     */
    private ComponentProfile[] walkGraph( final boolean providers,
                                          final ComponentStore store )
    {
        final ArrayList result = new ArrayList();
        final ArrayList done = new ArrayList();

        final Collection components = store.getComponents();
        final ComponentProfile[] entrySet =
            (ComponentProfile[])components.toArray( new ComponentProfile[ components.size() ] );
        for( int i = 0; i < entrySet.length; i++ )
        {
            final ComponentProfile component = entrySet[ i ];
            visitcomponent( component,
                            providers,
                            done,
                            result,
                            store );
        }

        final ComponentProfile[] returnValue = new ComponentProfile[ result.size() ];
        return (ComponentProfile[])result.toArray( returnValue );
    }

    /**
     * Visit a component when traversing dependencies.
     *
     * @param component the component
     * @param providers true if walking tree looking for providers, else false
     * @param done those nodes already traversed
     * @param order the order in which nodes have already been
     *             traversed
     */
    private void visitcomponent( final ComponentProfile component,
                                 final boolean providers,
                                 final ArrayList done,
                                 final ArrayList order,
                                 final ComponentStore store )
    {
        //If already visited this component then bug out early
        if( done.contains( component ) )
        {
            return;
        }
        done.add( component );

        if( providers )
        {
            visitProviders( component, done, order, store );
        }
        else
        {
            visitConsumers( component, done, order, store );
        }

        order.add( component );
    }

    /**
     * Traverse graph of components that provide services to
     * the specified component.
     *
     * @param component the ComponentProfile
     */
    private void visitProviders( final ComponentProfile component,
                                 final ArrayList done,
                                 final ArrayList order,
                                 final ComponentStore store )
    {
        final DependencyDescriptor[] descriptors =
            component.getInfo().getDependencies();
        final ComponentMetaData metaData = component.getMetaData();

        for( int i = 0; i < descriptors.length; i++ )
        {
            final DependencyMetaData[] dependencySet =
                metaData.getDependencies( descriptors[ i ].getKey() );

            // added != null clause to catch cases where an optional
            // dependency exists and the dependecy has not been bound
            // to a provider

            for( int j = 0; j < dependencySet.length; j++ )
            {
                final DependencyMetaData dependency = dependencySet[ j ];
                if( dependency != null )
                {
                    final ComponentProfile other =
                        getComponent( dependency.getProviderName(), store );
                    visitcomponent( other, true, done, order, store );
                }
            }
        }
    }

    /**
     * Traverse all Consumers of component. ie Anyone that uses
     * service provided by component.
     *
     * @param component the ComponentProfile
     */
    private void visitConsumers( final ComponentProfile component,
                                 final ArrayList done,
                                 final ArrayList order,
                                 final ComponentStore store )
    {
        final String name = component.getMetaData().getName();

        final Collection components = store.getComponents();
        final ComponentProfile[] entrySet =
            (ComponentProfile[])components.toArray( new ComponentProfile[ components.size() ] );
        for( int i = 0; i < entrySet.length; i++ )
        {
            final ComponentProfile other = entrySet[ i ];
            final DependencyMetaData[] dependencies =
                other.getMetaData().getDependencies();

            for( int j = 0; j < dependencies.length; j++ )
            {
                final String depends = dependencies[ j ].getProviderName();
                if( depends.equals( name ) )
                {
                    visitcomponent( other, false, done, order, store );
                }
            }
        }

        final List childStores = store.getChildStores();
        final int childCount = childStores.size();
        for( int i = 0; i < childCount; i++ )
        {
            final ComponentStore child = (ComponentStore)childStores.get( i );
            visitConsumers( component, done, order, child );
        }
    }

    /**
     * Utility method to get component with specified name from specified array.
     *
     * @param name the name of component
     * @return the component
     */
    private ComponentProfile getComponent( final String name,
                                           final ComponentStore store )
    {
        final ComponentProfile component = store.getComponent( name );
        if( null != component )
        {
            return component;
        }

        final ComponentStore parent = store.getParent();
        if( null != parent )
        {
            return parent.getComponent( name );
        }

        //Should never happen if Verifier passed checks
        throw new IllegalStateException();
    }
}
