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

import java.io.File;

import org.apache.avalon.meta.info.ContextDescriptor;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.meta.info.Type;
import org.apache.avalon.composition.data.DeploymentProfile;

/**
 * Defintion of a component deployment context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2004/01/04 17:23:16 $
 */
public interface ComponentContext
{
    /**
     * The standard context entry key for the partition name.
     */
    static final String PARTITION_KEY = ContextDescriptor.PARTITION_KEY;

    /**
     * The standard context entry key for the partition name.
     */
    static final String NAME_KEY = ContextDescriptor.NAME_KEY;

    /**
     * The standard context entry key for the partition name.
     */
    static final String CLASSLOADER_KEY = ContextDescriptor.CLASSLOADER_KEY;

    /**
     * The standard context entry key for the partition name.
     */
    static final String HOME_KEY = ContextDescriptor.HOME_KEY;

    /**
     * The standard context entry key for the partition name.
     */
    static final String TEMP_KEY = ContextDescriptor.TEMP_KEY;

   /**
    * Return the partition name that the component will execute within.
    *
    * @return the partition name
    */
    String getPartitionName();

   /**
    * Return the name that the component will execute under.
    *
    * @return the name
    */
    String getName();

   /**
    * Return the system context.
    *
    * @return the system context
    */
    SystemContext getSystemContext();

   /**
    * Return the containment context.
    *
    * @return the containment context
    */
    ContainmentContext getContainmentContext();

   /**
    * Return the working directory for the component.
    *
    * @return the working directory
    */
    File getHomeDirectory();

   /**
    * Return the temporary directory for the component.
    *
    * @return the temporary directory
    */
    File getTempDirectory();

   /**
    * Return the logging channel assignable to the deployment model.
    *
    * @return the logging channel
    */
    Logger getLogger();

   /**
    * Return the deployment profile.
    *
    * @return the profile
    */
    DeploymentProfile getProfile();

   /**
    * Return the component type.
    *
    * @return the type defintion
    */
    Type getType();

   /**
    * Return the component class.
    *
    * @return the class
    */
    Class getDeploymentClass();

   /**
    * Return the classloader for the component.
    *
    * @return the classloader
    */
    ClassLoader getClassLoader();

   /**
    * Add a context entry model to the deployment context.
    * @param model the entry model
    */
    public void register( EntryModel model );

   /**
    * Get a context entry from the deployment context.
    * @param alias the entry lookup key
    * @return value the corresponding value
    * @exception ContextException if a key corresponding to the supplied alias is unknown
    */
    Object resolve( String alias ) throws ContextException;

}
