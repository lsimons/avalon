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

package org.apache.avalon.merlin;

import java.net.URL;
import java.io.File;
import java.util.Map;

/**
 * A service that provides access to versioned resources.
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/12/08 15:37:12 $
 */
public interface KernelCriteria extends Map
{
   /**
    * Shared application repository root directory.
    */
    String MERLIN_REPOSITORY = 
      "merlin.repository";

   /**
    * Merlin system home.
    */
    String MERLIN_HOME = 
      "merlin.home";

   /**
    * Merlin system repository cache path.
    */
    String MERLIN_SYSTEM = 
      "merlin.system";

   /**
    * Overide directives.
    */
    String MERLIN_CONFIG = 
      "merlin.config";

   /**
    * Install directive path sequence.
    */
    String MERLIN_INSTALL = 
      "merlin.install";

   /**
    * A comma seperated sequence of block urls.
    */
    String MERLIN_DEPLOYMENT = 
      "merlin.deployment";

   /**
    * Merlin kernel url key.
    */
    String MERLIN_KERNEL = 
      "merlin.kernel";

   /**
    * Merlin target configuration override path.
    */
    String MERLIN_OVERRIDE = 
      "merlin.override";

   /**
    * Maerlin working directory.
    */
    String MERLIN_DIR = 
      "merlin.dir";

   /**
    * The temp directory parameter.
    */
    String MERLIN_TEMP = 
      "merlin.temp";

   /**
    * Base directory parameter.
    */
    String MERLIN_CONTEXT = 
      "merlin.context";

   /**
    * Anchor directory for extension and classpath 
    * relative references.
    */
    String MERLIN_ANCHOR = 
      "merlin.anchor";

   /**
    * Info policy parameter.
    */
    String MERLIN_INFO = 
      "merlin.info";

   /**
    * Debug policy parameter.
    */
    String MERLIN_DEBUG = 
      "merlin.debug";

   /**
    * Server model parameter.
    */
    String MERLIN_SERVER = 
      "merlin.server";

   /**
    * Auto start mode.
    */
    String MERLIN_AUTOSTART = 
      "merlin.autostart";

   /**
    * Language override.
    */
    String MERLIN_LANG = 
      "merlin.lang";


   /**
    * Return the lang code.  A null value indicates that the 
    * default language applies.
    * @return the language code
    */
    String getLanguageCode();

   /**
    * Return the root directory to the shared repository.
    * @return the root common repository directory
    */
    File getRepositoryDirectory();

   /**
    * Return the root directory to the merlin installation
    * @return the merlin home directory
    */
    File getHomeDirectory();

   /**
    * Return the root directory to the merlin system repository
    * @return the merlin system repository directory
    */
    File getSystemDirectory();

   /**
    * Return the set of block URLs to be included in the root application
    * @return the block deployment urls
    */
    URL[] getDeploymentURLs();

   /**
    * Return the root directory to the merlin configurations
    * @return the merlin configuration directory
    */
    File getConfigDirectory();

   /**
    * Return the url to the kernel confiuration
    * @return the kernel configuration url
    */
    URL getKernelURL();

   /**
    * Return the url to the configuration override targets.
    * @return the override url
    */
    String getOverridePath();

   /**
    * Return the working client directory.
    * @return the working directory
    */
    File getWorkingDirectory();

   /**
    * Return the temporary client directory.
    * @return the temp directory
    */
    File getTempDirectory();

   /**
    * Return the context directory from which relative 
    * runtrime home directories will be established for 
    * components referencing urn:avalon:home
    *
    * @return the working directory
    */
    File getContextDirectory();

   /**
    * Return the anchor directory to be used when resolving 
    * library declarations in classload specifications.
    *
    * @return the anchor directory
    */
    File getAnchorDirectory();

   /**
    * Return info generation policy.  If TRUE the parameters 
    * related to deployment will be listed on startup. 
    *
    * @return the info policy
    */
    boolean isInfoEnabled();

   /**
    * Return debug policy.  If TRUE all logging channels will be 
    * set to debug level (useful for debugging).
    *
    * @return the debug policy
    */
    boolean isDebugEnabled();

   /**
    * Return server execution policy.  If TRUE the kernel will 
    * continue until explicitly terminated.  If FALSE the kernel
    * will initiate decommissioning on completion of deployment.
    *
    * @return the server execution mode
    */
    boolean isServerEnabled();

   /**
    * Return the autostart policy.  If TRUE (the default) the 
    * deployment of the application container will be initiated
    * following kernel initialization.
    *
    * @return the autostart policy
    */
    boolean isAutostartEnabled();

}
