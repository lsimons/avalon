/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2004 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.composition.model.impl;

import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ModelRepository;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;


/**
 * The model repository interface declares operations through which 
 * clients may resolve registered model instances relative to
 * a stage or service dependencies.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/21 00:10:27 $
 */
public class DefaultModelRepository implements ModelRepository
{
    //------------------------------------------------------------------
    // immutable state
    //------------------------------------------------------------------

    /**
     * The parent appliance repository.
     */
    private ModelRepository m_parent;
    
    private Logger m_logger;
    
    /**
     * Table of registered appliance instances keyed by name.
     */
    private final Map m_models = new Hashtable();

    //------------------------------------------------------------------
    // constructor
    //------------------------------------------------------------------

    public DefaultModelRepository( 
      ModelRepository parent, Logger logger )
    {
        m_parent = parent;
        m_logger = logger;
    }
    
    //------------------------------------------------------------------
    // ModelRepository
    //------------------------------------------------------------------

    /**
     * Locate an model meeting the supplied criteria.
     *
     * @param dependency a component service dependency
     * @return the model or null if no matching model is resolved
     */
    public DeploymentModel getModel( DependencyDescriptor dependency )
    {
        //
        // attempt to locate a solution locally
        //

        Iterator iterator = m_models.values().iterator();
        while( iterator.hasNext() )
        {
            DeploymentModel model = (DeploymentModel) iterator.next();
            if( model.isaCandidate( dependency ) )
            {
                return model;
            }
        }

        //
        // attempt to locate a solution from the parent
        //

        if( m_parent != null )
        {
            return m_parent.getModel( dependency );
        }

        return null;
    }

    /**
     * Locate all models meeting the supplied criteria.
     *
     * @param stage a component stage dependency
     * @return the candidate models
     */
    public DeploymentModel[] getCandidateProviders( StageDescriptor stage )
    {
        ArrayList list = new ArrayList();
        Iterator iterator = m_models.values().iterator();
        while( iterator.hasNext() )
        {
            DeploymentModel model = (DeploymentModel) iterator.next();
            if( model.isaCandidate( stage ) )
            {
                list.add( model );
            }
        }

        if( m_parent != null )
        {
            DeploymentModel[] models = m_parent.getCandidateProviders( stage );
            for( int i=0; i<models.length; i++ )
            {
                list.add( models[i] );
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    /**
     * Locate all models meeting the supplied dependency criteria.
     *
     * @param dependency a component service dependency
     * @return the candidate models
     */
    public DeploymentModel[] getCandidateProviders( 
      DependencyDescriptor dependency )
    {
        ArrayList list = new ArrayList();
        Iterator iterator = m_models.values().iterator();
        while( iterator.hasNext() )
        {
            DeploymentModel model = (DeploymentModel) iterator.next();
            if( model.isaCandidate( dependency ) )
            {
                list.add( model );
            }
        }

        if( m_parent != null )
        {
            DeploymentModel[] models = m_parent.getCandidateProviders( dependency );
            for( int i=0; i<models.length; i++ )
            {
                list.add( models[i] );
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    /**
     * Locate a model meeting the supplied criteria.
     *
     * @param stage a component stage dependency
     * @return the model
     */
    public DeploymentModel getModel( StageDescriptor stage )
    {
        Iterator iterator = m_models.values().iterator();
        while( iterator.hasNext() )
        {
            DeploymentModel model = (DeploymentModel) iterator.next();

            if( model.isaCandidate( stage ) )
            {
                return model;
            }
        }

        if( m_parent != null )
        {
            return m_parent.getModel( stage );
        }

        return null;
    }

    //------------------------------------------------------------------
    // implementation
    //------------------------------------------------------------------

    /**
     * Add an model to the repository.
     *
     * @param model the model to add
     */
    public void addModel( DeploymentModel model )
    {
        m_models.put( model.getName(), model );
    }

    /**
     * Add an model to the repository.
     *
     * @param name the name to register the model under
     * @param model the model to add
     */
    public void addModel( String name, DeploymentModel model )
    {
        m_models.put( name, model );
    }

    /**
     * Remove an model from the repository.
     *
     * @param model the model to remove
     */
    public void removeModel( DeploymentModel model )
    {
        m_models.remove( model.getName() );
    }

    /**
     * Return a sequence of all of the local models.
     *
     * @return the model sequence
     */
    public DeploymentModel[] getModels()
    {
        return (DeploymentModel[]) m_models.values().toArray( 
          new DeploymentModel[0] );
    }

    /**
     * Locate a local model matching the supplied name.
     *
     * @param name the model name
     * @return the model or null if the model name is unknown
     */
    public DeploymentModel getModel( String name )
    {
        DeploymentModel model = (DeploymentModel) m_models.get( name );
        if( model == null && m_logger != null )
        {
            m_logger.debug( 
              "Can't find '" 
              + name 
              + "' in model repository: " 
              + m_models );
        }
        return model;
    }
}
