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
import java.net.URL;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.composition.data.ClassLoaderDirective;

/**
 * Defintion of a working context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:15 $
 */
public interface ClassLoaderContext extends Context
{
   /**
    * Return the logging channel to be applied to the 
    * classloader model.
    *
    * @return the system logging channel
    */
    Logger getLogger();

   /**
    * Return the local repository.
    *
    * @return the repository
    */
    Repository getRepository();

   /**
    * Return the base directory from which relative library directives
    * and fileset directory paths may be resolved.
    *
    * @return the base directory
    */
    File getBaseDirectory();

   /**
    * Return the classloader to be assigned as the parent classloader
    * of the classloader created by the model.
    *
    * @return the classloader
    */
    ClassLoader getClassLoader();

   /**
    * Return the optional packages already establised relative to 
    * the parent classloader.
    *
    * @return the array of established optional packages
    */
    OptionalPackage[] getOptionalPackages();

   /**
    * Return the extension manager established by the parent 
    * classloader model.
    *
    * @return the extension manager
    */
    ExtensionManager getExtensionManager();

   /**
    * Return the classloader directive to be applied to the 
    * classloader model.
    *
    * @return the classloader directive
    */
    ClassLoaderDirective getClassLoaderDirective();

   /**
    * Return the type repository established by the parent classloader.
    *
    * @return the type repository
    */
    TypeRepository getTypeRepository();

   /**
    * Return the service repository established by the parent classloader.
    *
    * @return the service repository
    */
    ServiceRepository getServiceRepository();

   /**
    * Return any implied urls to include in the classloader.
    *
    * @return the implied urls
    */
    URL[] getImplicitURLs();

}
