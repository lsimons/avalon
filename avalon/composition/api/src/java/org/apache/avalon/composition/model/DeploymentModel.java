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

package org.apache.avalon.composition.model;

import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.ServiceDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * Model desribing a deployment scenario.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.7.2.3 $ $Date: 2004/01/04 21:28:59 $
 */
public interface DeploymentModel
{
    String SEPARATOR = "/";

   /**
    * Return the name of the model.
    * @return the name
    */
    String getName();

   /**
    * Return the model partition path.
    * @return the path
    */
    String getPath();

   /**
    * Return the model fully qualified name.
    * @return the fully qualified name
    */
    String getQualifiedName();

   /**
    * Return the mode of model establishment.
    * @return the mode
    */
    Mode getMode();

    //-----------------------------------------------------------
    // service production
    //-----------------------------------------------------------
    
   /**
    * Return the set of services produced by the model.
    * @return the services
    */
    ServiceDescriptor[] getServices();

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * depedendency.
    * @return true if this model can fulfill the dependency
    */
    boolean isaCandidate( DependencyDescriptor dependency );

   /**
    * Return TRUE is this model is capable of supporting a supplied 
    * stage dependency.
    * @return true if this model can fulfill the dependency
    */
    boolean isaCandidate( StageDescriptor stage );

    //-----------------------------------------------------------
    // composite assembly
    //-----------------------------------------------------------

    /**
     * Returns the assembled state of the model.
     * @return true if this model is assembled
     */
    boolean isAssembled();

    /**
     * Assemble the model.
     * @exception Exception if an error occurs during model assembly
     */
    void assemble() throws AssemblyException;

   /**
    * Return the set of models consuming this model.
    * @return the consumers
    */
    DeploymentModel[] getConsumerGraph();

   /**
    * Return the set of models supplying this model.
    * @return the providers
    */
    DeploymentModel[] getProviderGraph();

    /**
     * Disassemble the model.
     */
    void disassemble();

    /**
     * Return the set of models assigned as providers.
     * @return the providers consumed by the model
     * @exception IllegalStateException if invoked prior to 
     *    the completion of the assembly phase 
     */
    DeploymentModel[] getProviders();


}
