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

package org.apache.avalon.composition.model.impl;

import java.util.ArrayList;

import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ModelSelector;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.composition.data.Mode;

/**
 * Default selector class. The default selector selcts profiles based
 * of ranking of profile relative to EXPLICIT, PACKAGED and IMPLICIT
 * status. For each category, if a supplied profile matches the category
 * the first profile matching the category is returned.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/13 11:41:26 $
 */
class DefaultModelSelector implements ModelSelector
{
    //==============================================================
    // ModelSelector
    //==============================================================

    /**
     * Returns the preferred model from an available selection of
     * candidates capable of fulfilling a supplied service dependency.
     *
     * @param models the set of candidate models
     * @param dependency a service dependency
     * @return the preferred model or null if no satisfactory 
     *    provider can be established
     */
    public DeploymentModel select( DeploymentModel[] models, DependencyDescriptor dependency )
    {
        DeploymentModel[] candidates = filterCandidateProviders( models, dependency );
        return select( candidates );
    }

    /**
     * Returns the preferred model from an available selection of candidates
     * @param models the set of candidate models 
     * @param stage the stage dependency
     * @return the preferred provider or null if no satisfactory 
     *    provider can be established
     */
    public DeploymentModel select( DeploymentModel[] models, StageDescriptor stage )
    {
        DeploymentModel[] candidates = filterCandidateProviders( models, stage );
        return select( candidates );
    }

    //==============================================================
    // implementation
    //==============================================================

    private DeploymentModel[] filterCandidateProviders( 
      DeploymentModel[] models, DependencyDescriptor dependency )
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < models.length; i++ )
        {
            DeploymentModel model = models[i];
            if( model.isaCandidate( dependency ) )
            {
                list.add( model );
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    private DeploymentModel[] filterCandidateProviders( 
      DeploymentModel[] models, StageDescriptor stage )
    {
        ArrayList list = new ArrayList();
        for( int i = 0; i < models.length; i++ )
        {
            DeploymentModel model = models[i];
            if( model.isaCandidate( stage ) )
            {
                list.add( model );
            }
        }
        return (DeploymentModel[]) list.toArray( new DeploymentModel[0] );
    }

    /**
     * Select a model from a set of models based on a priority ordering
     * of EXPLICIT, PACKAGE and lastly IMPLICIT.  If multiple candidates
     * exist for a particulr mode, return the first candidate.
     *
     * @param profiles the set of candidate profiles
     * @param dependency the service dependency
     * @return the preferred profile or null if no satisfactory 
     *   provider can be established
     */
    private DeploymentModel select( DeploymentModel[] models )
    {
        if( models.length == 0 )
        {
            return null;
        }

        for( int i=0; i<models.length; i++ )
        {
            if( models[i].getMode().equals( Mode.EXPLICIT ) )
            {
                return models[i];
            }
        }

        for( int i=0; i<models.length; i++ )
        {
            if( models[i].getMode().equals( Mode.PACKAGED ) )
            {
                return models[i];
            }
        }

        for( int i=0; i<models.length; i++ )
        {
            if( models[i].getMode().equals( Mode.IMPLICIT ) )
            {
                return models[i];
            }
        }

        return null;
    }
}
