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

import java.net.URL;

import org.apache.avalon.composition.data.Profile;
import org.apache.avalon.composition.data.ServiceDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * Containment model is an extended deployment model that aggregates 
 * a set of models.  A containment model describes a logical containment 
 * context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2003/12/05 01:27:12 $
 */
public interface ContainmentModel extends Model
{
    public static String SEPERATOR = "/";

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
    * Return the partition established by the containment model.
    *
    * @return the partition name
    */
    String getPartition();

   /**
    * Return the classloader model.
    *
    * @return the classloader model
    */
    ClassLoaderModel getClassLoaderModel();

   /**
    * Return the set of models nested within this model.
    * @return the classloader model
    */
    Model[] getModels();

   /**
    * Return a model relative to a supplied name.
    * @return the named model or null if the name is unknown
    */
    Model getModel( String name );

   /**
    * Return a model relative to a supplied dependency.
    * @return a matching model or null if the dependency is unresolvable
    * @exception ModelRuntimeException if an error occurs during model establishment
    */
    Model getModel( DependencyDescriptor dependency ) 
      throws ModelRuntimeException;

   /**
    * Return a model relative to a supplied stage descriptor.
    * @param stage the stage descriptor
    * @return model of a an stage handler or null if the stage is unresolvable
    * @exception ModelRuntimeException if an error occurs during model establishment
    */
    Model getModel( StageDescriptor stage ) 
      throws ModelRuntimeException;

   /**
    * Addition of a new subsidiary model within
    * the containment context using a supplied profile url.
    *
    * @param url a containment profile url
    * @return the model based on the derived profile
    * @exception ModelException if an error occurs during model establishment
    */
    Model addModel( URL url ) throws ModelException;

   /**
    * Addition of a new subsidiary containment model within
    * the containment context using a supplied url.
    *
    * @param block a url referencing a containment profile
    * @param config containment configuration targets
    * @return the model created using the derived profile and configuration
    * @exception ModelException if an error occurs during model establishment
    */
    public ContainmentModel addContainmentModel( URL block, URL config ) 
      throws ModelException;

   /**
    * Addition of a new subsidiary model within
    * the containment context using a supplied profile.
    *
    * @param profile a containment or deployment profile 
    * @return the model based on the supplied profile
    * @exception ModelException if an error occurs during model establishment
    */
    Model addModel( Profile profile ) throws ModelException;

   /**
    * Return the set of service export directives.
    * @return t he export directives
    */
    ServiceDirective[] getExportDirectives();

   /**
    * Return the set of service export directives for a supplied class.
    * @return the export directives
    */
    ServiceDirective getExportDirective( Class clazz );

   /**
    * Apply a set of override targets resolvable from a supplied url.
    * @param url a url resolvable to a TargetDirective[]
    * @exception ModelException if an error occurs
    */
    void applyTargets( URL url )
      throws ModelException;

   /**
    * Apply a set of override targets.
    * @param targets a set of target directives
    */
    void applyTargets( TargetDirective[]targets );

}
