/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.composition.model;

import java.net.URL;
import java.security.Permission;
import java.security.cert.Certificate;

import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.framework.logger.Logger;

/**
 * <p>Specification of a classloader model from which a 
 * a fully qualifed classpath can be established.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:25:25 $
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
    * Returns the Certificates associated with the classes that
    * can be loaded by the classloader.
    **/ 
    Certificate[] getCertificates();
    
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
