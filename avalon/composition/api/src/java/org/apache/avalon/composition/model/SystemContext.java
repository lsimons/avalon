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

import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * Defintion of a system context that exposes a system wide set of parameters.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.1.1.2.1 $ $Date: 2004/01/06 23:16:49 $
 */
public interface SystemContext extends Context
{
   /**
    * Return the model factory.
    *
    * @return the factory
    */
    ModelFactory getFactory();

   /**
    * Return the base directory from which relative references 
    * should be resolved.
    *
    * @return the base directory
    */
    File getBaseDirectory();

   /**
    * Return the home directory from which containers may establish
    * persistent content.
    *
    * @return the working directory
    */
    File getHomeDirectory();

   /**
    * Return the temp directory from which containers may establish
    * non-persistent content.
    *
    * @return the temp directory
    */
    File getTempDirectory();

   /**
    * Return the system wide repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    Repository getRepository();

   /**
    * Return the system trace flag.
    *
    * @return the trace flag
    */
    boolean isTraceEnabled();

   /**
    * Return the system classloader.
    *
    * @return the system classloader
    */
    ClassLoader getSystemClassLoader();

   /**
    * Return the system classloader.
    *
    * @return the system classloader
    */
    ClassLoader getCommonClassLoader();

   /**
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    LoggingManager getLoggingManager();

   /**
    * Return the system logging channel.
    *
    * @return the system logging channel
    */
    Logger getLogger();

    /** 
     * Returns the configurable kernel parameters.
     *
     * @return a Parameters object populated with the system
     * parameters.
     */
    Parameters getSystemParameters();
}
