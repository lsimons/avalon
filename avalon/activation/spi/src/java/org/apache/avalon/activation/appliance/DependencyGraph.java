/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.activation.appliance;

import java.util.ArrayList;

import org.apache.avalon.composition.model.Model;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * <p>Utility class to help aquire a ordered graph of
 * consumers and providers for specific components.</p>
 * <p><b>UML</b></p>
 * <p><image src="doc-files/DependencyGraph.gif" border="0"/></p>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:30:59 $
 */
public class DependencyGraph
{
    /**
     * Parent Map. Components in parent
     * Map are potential Providers for services
     * if no appliance in the current assembly satisfies
     * a dependency.
     */
    private final DependencyGraph m_parent;

    /**
     * The set of components declared by the container as available.,
     * Used when searching for providers/consumers.
     */
    private final ArrayList m_appliances = new ArrayList();

    /**
     * The child {@link DependencyGraph} objects.
     * Possible consumers of services in this assembly.
     */
    private final ArrayList m_children = new ArrayList();

    /**
     * Creation of a new empty dependency graph.
     */
    public DependencyGraph()
    {
        this( null );
    }

    /**
     * Creation of a new dependecy graph holding a reference to a parent
     * graph.  Appliance instances in the parent graph are potential providers
     * for services if no appliance in current assembly satisfies a dependency.
     *
     * @param parent the parent graph
     */
    public DependencyGraph( final DependencyGraph parent )
    {
        m_parent = parent;
    }

    /**
     * Addition of a consumer dependency graph.
     *
     * @param child the child map
     */
    public void addChild( final DependencyGraph child )
    {
        m_children.add( child );
    }

    /**
     * Removal of a consumer dependency graph.
     *
     * @param child the child map
     */
    public void removeChild( final DependencyGraph child )
    {
        m_children.remove( child );
    }

    /**
     * Add an appliance to current dependency graph.
     *
     * @param appliance the appliance
     */
    public void add( final Appliance appliance )
    {
        if( !m_appliances.contains( appliance ) )
        {
            m_appliances.add( appliance );
        }
    }

    /**
     * Remove an appliance from the dependency graph.
     *
     * @param appliance the appliance
     */
    public void remove( final Appliance appliance )
    {
        m_appliances.remove( appliance );
    }

    /**
     * Get the serilized graph of {@link Appliance} objects
     * required when starting up all the target. This makes sure
     * that all providers are established before their coresponding
     * consumers in the graph.
     *
     * @return the ordered list of appliances
     */
    public Appliance[] getStartupGraph()
    {
        try
        {
            return walkGraph( true );
        }
        catch( Throwable e )
        {
            final String error = "Unexpect error while resolving startup graph.";
            throw new ApplianceRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link Appliance} objects
     * required when shutting down all the components. This makes
     * sure that all consumer shutdown actions occur before their
     * coresponding providers in graph.
     *
     * @return the ordered list of appliance instances
     */
    public Appliance[] getShutdownGraph()
    {
        try
        {
            return walkGraph( false );
        } 
        catch( Throwable e )
        {
            final String error = "Unexpect error while resolving shutdown graph.";
            throw new ApplianceRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link Appliance} objects
     * that use services of the specified appliance.
     *
     * @param appliance the appliance
     * @return the ordered list of consumer appliance instances
     */
    public Appliance[] getConsumerGraph( final Appliance appliance )
    {
        try
        {
            return referencedAppliances( appliance, getComponentGraph( appliance, false ) );
        } 
        catch( Throwable e )
        {
            final String error =
                    "Unexpect error while resolving consumer graph for appliance: " + appliance;
            throw new ApplianceRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link Appliance} objects
     * that provide specified appliance with services.
     *
     * @param appliance the appliance
     * @return the ordered list of providers
     */
    public Appliance[] getProviderGraph( final Appliance appliance )
    {
        try
        {
            return referencedAppliances( appliance, getComponentGraph( appliance, true ) );
        } 
        catch( Throwable e )
        {
            final String error =
                    "Unexpect error while resolving provider graph for: " + appliance;
            throw new ApplianceRuntimeException( error, e );
        }
    }

    /**
     * Return an appliance array that does not include the provided appliance.
     */
    private Appliance[] referencedAppliances( final Appliance appliance, Appliance[] appliances )
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < appliances.length; i++ )
        {
            if( !appliances[i].equals( appliance ) )
            {
                list.add( appliances[i] );
            }
        }
        return (Appliance[]) list.toArray( new Appliance[0] );
    }

    /**
     * Get the graph of a single appliance.
     *
     * @param appliance the appliance
     * @param providers true if traversing providers, false if consumers
     * @return the list of components in graph
     */
    private Appliance[] getComponentGraph( final Appliance appliance, final boolean providers )
    {
        final ArrayList result = new ArrayList();
        visitcomponent( appliance,
                providers,
                new ArrayList(),
                result );

        final Appliance[] returnValue = new Appliance[result.size()];
        return (Appliance[]) result.toArray( returnValue );
    }

    /**
     * Method to generate an ordering of nodes to traverse.
     * It is expected that the specified components have passed
     * verification tests and are well formed.
     *
     * @param direction true if forward dependencys traced, false if dependencies reversed
     * @return the ordered node names
     */
    private Appliance[] walkGraph( final boolean direction )
    {
        final ArrayList result = new ArrayList();
        final ArrayList done = new ArrayList();

        final int size = m_appliances.size();
        for( int i = 0; i < size; i++ )
        {
            final Appliance appliance =
                    (Appliance) m_appliances.get( i );

            visitcomponent( appliance,
                    direction,
                    done,
                    result );
        }

        final Appliance[] returnValue = new Appliance[result.size()];
        return (Appliance[]) result.toArray( returnValue );
    }

    /**
     * Visit a appliance when traversing dependencies.
     *
     * @param appliance the appliance
     * @param direction true if walking tree looking for providers, else false
     * @param done those nodes already traversed
     * @param order the order in which nodes have already been
     *             traversed
     */
    private void visitcomponent( final Appliance appliance,
            final boolean direction,
            final ArrayList done,
            final ArrayList order )
    {

        //If already visited this appliance return

        if( done.contains( appliance ) ) return;

        done.add( appliance );

        if( direction )
        {
            visitProviders( appliance, done, order );
        } 
        else
        {
            visitConsumers( appliance, done, order );
        }

        order.add( appliance );
    }

    /**
     * Traverse graph of components that provide services to
     * the specified appliance.
     *
     * @param appliance the appliance
     */
    private void visitProviders( final Appliance appliance,
            final ArrayList done,
            final ArrayList order )
    {
        if( appliance instanceof Composite )
        {
            Appliance[] providers = ((Composite)appliance).getProviders();
            for( int i = (providers.length - 1); i > -1; i-- )
            {
                visitcomponent( providers[i], true, done, order );
            }
        }
    }

    /**
     * Traverse all consumers of an appliance. I.e. all appliances that use
     * service provided by supplied appliance.
     *
     * @param appliance the Appliance
     */
    private void visitConsumers( final Appliance appliance,
            final ArrayList done,
            final ArrayList order )
    {

        final String name = appliance.getModel().getName();

        final int size = m_appliances.size();
        for( int i = 0; i < size; i++ )
        {
            final Appliance other =
                    (Appliance) m_appliances.get( i );

            if( appliance instanceof Composite )
            {
                final Appliance[] providers = ((Composite)other).getProviders();
                for( int j = 0; j < providers.length; j++ )
                {
                    Appliance provider = providers[j];
                    if( provider.equals( appliance ) )
                    {
                        visitcomponent( other, false, done, order );
                    }
                }
            }
        }
        final int childCount = m_children.size();
        for( int i = 0; i < childCount; i++ )
        {
            final DependencyGraph map = (DependencyGraph) m_children.get( i );
            map.visitConsumers( appliance, done, order );
        }
    }
}
