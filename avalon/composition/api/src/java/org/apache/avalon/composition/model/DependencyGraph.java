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

package org.apache.avalon.composition.model;

import java.util.ArrayList;

/**
 * <p>Utility class to aquire an ordered graph of
 * consumers and providers models.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.3 $ $Date: 2004/01/04 21:28:59 $
 */
public class DependencyGraph
{
    /**
     * Parent Map. Components in the parent
     * Map are potential Providers for services
     * if no model in the current graph satisfies
     * a dependency.
     */
    private final DependencyGraph m_parent;

    /**
     * The set of models declared by the container as available.
     * Used when searching for providers/consumers.
     */
    private final ArrayList m_models = new ArrayList();

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
     * graph.  DeploymentModel instances in the parent graph are potential providers
     * for services if no model in current assembly satisfies a dependency.
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
     * Add a model to current dependency graph.
     *
     * @param model the model to add to the graph
     */
    public void add( final DeploymentModel model )
    {
        if( !m_models.contains( model ) )
        {
            m_models.add( model );
        }
    }

    /**
     * Remove a model from the dependency graph.
     *
     * @param model the model to remove
     */
    public void remove( final DeploymentModel model )
    {
        m_models.remove( model );
    }

    /**
     * Get the serilized graph of {@link DeploymentModel} objects
     * required when starting up the target. This makes sure
     * that all providers are established before their coresponding
     * consumers in the graph.
     *
     * @return the ordered list of models
     */
    public DeploymentModel[] getStartupGraph()
    {
        try
        {
            return walkGraph( true );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpect error while resolving startup graph.";
            throw new ModelRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link DeploymentModel} instances
     * required when shutting down all the components. This makes
     * sure that all consumer shutdown actions occur before their
     * coresponding providers in graph.
     *
     * @return the ordered list of model instances
     */
    public DeploymentModel[] getShutdownGraph()
    {
        try
        {
            return walkGraph( false );
        } 
        catch( Throwable e )
        {
            final String error = "Unexpect error while resolving shutdown graph.";
            throw new ModelRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link DeploymentModel} instances
     * that use services of the specified model.
     *
     * @param model the model
     * @return the ordered list of consumer model instances
     */
    public DeploymentModel[] getConsumerGraph( final DeploymentModel model )
    {
        try
        {
            return referencedModels( model, getComponentGraph( model, false ) );
        } 
        catch( Throwable e )
        {
            final String error =
                    "Unexpect error while resolving consumer graph for model: " + model;
            throw new ModelRuntimeException( error, e );
        }
    }

    /**
     * Get the serilized graph of {@link DeploymentModel} istances
     * that provide specified model with services.
     *
     * @param model the model
     * @return the ordered list of providers
     */
    public DeploymentModel[] getProviderGraph( final DeploymentModel model )
    {
        try
        {
            return referencedModels( model, getComponentGraph( model, true ) );
        } 
        catch( Throwable e )
        {
            final String error =
                    "Unexpect error while resolving provider graph for: " + model;
            throw new ModelRuntimeException( error, e );
        }
    }

    /**
     * Return an model array that does not include the provided model.
     */
    private DeploymentModel[] referencedModels( final DeploymentModel model, DeploymentModel[] models )
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < models.length; i++ )
        {
            if( !models[i].equals( model ) )
            {
                list.add( models[i] );
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    /**
     * Get the graph of a single model.
     *
     * @param model the target model
     * @param providers true if traversing providers, false if consumers
     * @return the list of models 
     */
    private DeploymentModel[] getComponentGraph( final DeploymentModel model, final boolean providers )
    {
        final ArrayList result = new ArrayList();
        visitcomponent( model,
                providers,
                new ArrayList(),
                result );

        final DeploymentModel[] returnValue = new DeploymentModel[result.size()];
        return (DeploymentModel[]) result.toArray( returnValue );
    }

    /**
     * Method to generate an ordering of nodes to traverse.
     * It is expected that the specified components have passed
     * verification tests and are well formed.
     *
     * @param direction true if forward dependencys traced, false if dependencies reversed
     * @return the ordered model list
     */
    private DeploymentModel[] walkGraph( final boolean direction )
    {
        final ArrayList result = new ArrayList();
        final ArrayList done = new ArrayList();

        final int size = m_models.size();
        for( int i = 0; i < size; i++ )
        {
            final DeploymentModel model =
                    (DeploymentModel) m_models.get( i );

            visitcomponent( model,
                    direction,
                    done,
                    result );
        }

        final DeploymentModel[] returnValue = new DeploymentModel[result.size()];
        return (DeploymentModel[]) result.toArray( returnValue );
    }

    /**
     * Visit a model when traversing dependencies.
     *
     * @param model the model
     * @param direction true if walking tree looking for providers, else false
     * @param done those nodes already traversed
     * @param order the order in which nodes have already been
     *             traversed
     */
    private void visitcomponent( final DeploymentModel model,
            final boolean direction,
            final ArrayList done,
            final ArrayList order )
    {

        //If already visited this model return

        if( done.contains( model ) ) return;

        done.add( model );

        if( direction )
        {
            visitProviders( model, done, order );
        } 
        else
        {
            visitConsumers( model, done, order );
        }

        order.add( model );
    }

    /**
     * Traverse graph of components that provide services to
     * the specified model.
     *
     * @param model the model
     */
    private void visitProviders( final DeploymentModel model,
            final ArrayList done,
            final ArrayList order )
    {
        DeploymentModel[] providers = model.getProviders();
        for( int i = (providers.length - 1); i > -1; i-- )
        {
            visitcomponent( providers[i], true, done, order );
        }
    }

    /**
     * Traverse all consumers of a model. I.e. all models that use
     * service provided by the supplied model.
     *
     * @param model the DeploymentModel
     */
    private void visitConsumers( final DeploymentModel model,
            final ArrayList done,
            final ArrayList order )
    {

        final String name = model.getName();

        final int size = m_models.size();
        for( int i = 0; i < size; i++ )
        {
            final DeploymentModel other =
                    (DeploymentModel) m_models.get( i );

            final DeploymentModel[] providers = other.getProviders();
            for( int j = 0; j < providers.length; j++ )
            {
                DeploymentModel provider = providers[j];
                if( provider.equals( model ) )
                {
                    visitcomponent( other, false, done, order );
                }
            }
        }
        final int childCount = m_children.size();
        for( int i = 0; i < childCount; i++ )
        {
            final DependencyGraph map = (DependencyGraph) m_children.get( i );
            map.visitConsumers( model, done, order );
        }
    }
}
