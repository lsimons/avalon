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

import java.util.Map;

import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.meta.info.Type;

/**
 * Deployment model defintion.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2003/10/19 10:31:01 $
 */
public interface DeploymentModel extends Model
{

   /**
    * Return the deployment type.
    * 
    * @return the type
    */
    Type getType();

   /**
    * Return the activation policy for the model. 
    * @return the activaltion policy
    */
    boolean getActivationPolicy();

   /**
    * Return the collection policy for the model. If a profile
    * does not declare a collection policy, the collection policy 
    * declared by the type will be used.
    *
    * @return the collection policy
    * @see org.apache.avalon.meta.info.InfoDescriptor#LIBERAL
    * @see org.apache.avalon.meta.info.InfoDescriptor#DEMOCRAT
    * @see org.apache.avalon.meta.info.InfoDescriptor#CONSERVATIVE
    */
    int getCollectionPolicy();

   /**
    * Set the collection policy to a supplied value.
    *
    * @param the collection policy
    * @see org.apache.avalon.meta.info.InfoDescriptor#LIBERAL
    * @see org.apache.avalon.meta.info.InfoDescriptor#DEMOCRAT
    * @see org.apache.avalon.meta.info.InfoDescriptor#CONSERVATIVE
    * @see org.apache.avalon.meta.info.InfoDescriptor#UNDEFINED
    */
    void setCollectionPolicy( int policy );

   /**
    * Return the logging categories. 
    * @return the logging categories
    */
    CategoriesDirective getCategories();

   /**
    * Set categories. 
    * @param categories the logging categories
    */
    void setCategories( CategoriesDirective categories );

   /**
    * Set the activation policy for the model. 
    * @param policy the activaltion policy
    */
    void setActivationPolicy( boolean policy );

   /**
    * Set the activation policy for the model to the default value. 
    */
    void revertActivationPolicy();

   /**
    * Return the class for the deployable target.
    * @return the class
    */
    Class getDeploymentClass();

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration will replace the existing configuration.
    *
    * @param config the supplied configuration
    */
    void setConfiguration( Configuration config );

   /**
    * Set the configuration to the supplied value.  The supplied 
    * configuration may suppliment or replace the existing configuration.
    *
    * @param config the supplied configuration
    * @param policy if TRUE the supplied configuration replaces the current
    *   configuration otherwise the resoved configuration shall be layed above
    *   the configuration supplied with the profile which in turn is layer above 
    *   the type default configuration (if any)
    */
    void setConfiguration( Configuration config, boolean policy );

   /**
    * Return the configuration to be applied to the component.
    * The implementation returns the current configuration state.
    * If the the component type does not implementation the 
    * Configurable interface, the implementation returns null. 
    *
    * @return the qualified configuration
    */
    Configuration getConfiguration();

   /**
    * Test if the component type backing the model is 
    * parameterizable.
    *
    * @return TRUE if the component type is parameterizable
    *   otherwise FALSE
    */
    public boolean isParameterizable();

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value will replace the existing parameters value.
    *
    * @param parameters the supplied parameters value
    */
    public void setParameters( Parameters parameters );

   /**
    * Set the parameters to the supplied value.  The supplied 
    * parameters value may suppliment or replace the existing 
    * parameters value.
    *
    * @param parameters the supplied parameters
    * @param policy if TRUE the supplied parameters replaces the current
    *   parameters value otherwise the existing and supplied values
    *   are aggregrated
    */
    void setParameters( Parameters parameters, boolean policy );

   /**
    * Return the configuration to be applied to the component.
    * The implementation returns the current configuration state.
    * If the the component type does not implementation the 
    * Configurable interface, the implementation returns null. 
    *
    * @return the qualified configuration
    */
    Parameters getParameters();

   /**
    * Rest if the component type backing the model requires the 
    * establishment of a runtime context.
    *
    * @return TRUE if the component type requires a runtime
    *   context otherwise FALSE
    */
    boolean isContextDependent();

   /**
    * Return the context model for this deployment model.
    * 
    * @return the context model if this model is context dependent, else
    *   the return value is null
    */
    ContextModel getContextModel();

   /**
    * Return the dependency models for this deployment model.
    *
    * @return the dependency models
    */
    DependencyModel[] getDependencyModels();

   /**
    * Return the stage models for this deployment model.
    *
    * @return the stage models
    */
    StageModel[] getStageModels();

   /**
    * Return the set of services produced by the model as a array of classes.
    *
    * @return the service classes
    */
    Class[] getInterfaces();

}
