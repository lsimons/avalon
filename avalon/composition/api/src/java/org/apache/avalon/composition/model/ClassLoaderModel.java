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
import java.security.Permission;

import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.framework.logger.Logger;

/**
 * <p>Specification of a classloader model from which a 
 * a fully qualifed classpath can be established.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/19 01:26:19 $
 */
public interface ClassLoaderModel
{

   /**
    * Return the classloader model type repository.
    *
    * @return the repository
    */
    TypeRepository getTypeRepository();

   /**
    * Return the classloader model service repository.
    *
    * @return the repository
    */
    ServiceRepository getServiceRepository();

   /**
    * Return the optional extensions manager.
    * @return the extension manager
    */
    ExtensionManager getExtensionManager();

   /**
    * Return the set of local established optional packages.
    *
    * @return the local set of optional packages
    */
    OptionalPackage[] getOptionalPackages();

   /**
    * Return the set of optional packages already established including
    * the optional packages established by any parent classloader model.
    *
    * @param policy if TRUE, return the local and all ancestor optional 
    *   package - if FALSE only return the local packages
    * @return the OptionalPackage instances
    */
    OptionalPackage[] getOptionalPackages( boolean policy );

   /**
    * Return the fully qualified classpath including extension jar files
    * resolved relative to a classpath directives.
    *
    * @return an array of URL representing the qualified classpath 
    */
    URL[] getQualifiedClassPath();

   /**
    * Return the classloader for a containment context.
    * An implementation is required to fulfill the the 
    * criteria expressed by the associated classloader 
    * directive, and, deliver support for any optional  
    * extension criteria expressed within a jar manifest
    * in accordance with the Java Optional Extensions 
    * specification.
    *
    * @return the classloader
    */
    ClassLoader getClassLoader();

   /**
    * Creation of a classloader context using this model as the 
    * relative parent.
    *
    * @param logger the logging channel 
    * @param profile the containment profile
    * @param implied any implied urls
    * @return a new classloader context
    */
    ClassLoaderContext createChildContext( 
      Logger logger, ContainmentProfile profile, URL[] implied );
      
   /** 
    * Return the security Permissions defined for this ClassLoaderModel.
    * 
    * These Permissions will be enforced if code level security is enabled
    * globally. If no Permissions are returned, all the components under
    * this container will run without Permissions.
    *
    * @return A SecurityPolicy which should be enagaged if codelevel
    *         security is enabled for the Classloader.
    **/
    Permission[] getSecurityPermissions();
}
