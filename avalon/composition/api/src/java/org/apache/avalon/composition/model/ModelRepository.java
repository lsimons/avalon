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

package org.apache.avalon.composition.model;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * The model repository interface declares operations through which 
 * clients may resolve new or existing model instances relative to
 * a stage or service dependency.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1.2.3 $ $Date: 2004/01/06 23:16:49 $
 */
public interface ModelRepository
{
    /**
     * Locate an model matching the supplied name.
     *
     * @param name the model name
     * @return the model or null if the model name is unknown
     */
    DeploymentModel getModel( String name );

    /**
     * Locate a model meeting the supplied criteria.
     *
     * @param dependency a component service dependency
     * @return the model
     */
    DeploymentModel getModel( DependencyDescriptor dependency );

    /**
     * Locate all models meeting the supplied dependency criteria.
     *
     * @param dependency a component service dependency
     * @return the candidate models
     */
    DeploymentModel[] getCandidateProviders( 
      DependencyDescriptor dependency );

    /**
     * Locate all models meeting the supplied criteria.
     *
     * @param stage a component stage dependency
     * @return the candidate models
     */
    DeploymentModel[] getCandidateProviders( StageDescriptor stage );

    /**
     * Locate a model meeting the supplied criteria.
     *
     * @param stage a component stage dependency
     * @return the model
     */
    DeploymentModel getModel( StageDescriptor stage );

    /**
     * Add an model to the repository.
     *
     * @param model the model to add
     */
    void addModel( DeploymentModel model );

    /**
     * Add an model to the repository.
     *
     * @param name the name to register the model under
     * @param model the model to add
     */
    void addModel( String name, DeploymentModel model );

    /**
     * Remove an model from the repository.
     *
     * @param model the model to remove
     */
    void removeModel( DeploymentModel model );

    /**
     * Locate an model meeting the supplied criteria.
     *
     * @return the model
     */
    public DeploymentModel[] getModels();

}
