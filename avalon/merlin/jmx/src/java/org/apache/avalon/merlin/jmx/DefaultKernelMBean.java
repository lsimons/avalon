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

package org.apache.avalon.merlin.impl;

import org.apache.avalon.merlin.KernelController;


/**
 * Management interface for the Merlin Kernel.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2004/01/09 20:29:49 $
 */
public interface DefaultKernelMBean extends KernelController
{
    /**
     * Return an approximation to the total amount of memory currently 
     * available for future allocated objects, measured in bytes.
     * @return the number of bytes of estimated free memory
     */
    long getMemoryFree();

   /**
    * Returns the total amount of memory in the Java virtual machine. The value 
    * returned by this method may vary over time, depending on the host environment. 
    *
    * @return the total amount of memory currently available for current and 
    *   future objects, measured in bytes.
    */
    long getMemoryTotal();

   /**
    * Return the percentage of free memory available.
    * @return the free memory percentage
    */
    int getMemoryVariableRatio();

   /**
    * Return the number of active threads.
    * @return the active thread count
    */
    int getThreadCount();

   /**
    * Return the state of the kernel.
    * @return a string representing the kernel state
    */
    String getKernelState();

   /**
    * Return the root directory to the shared repository.
    * @return the avalon home root repository directory
    */
    String getRepositoryDirectory();

   /**
    * Return the root directory to the merlin installation
    * @return the merlin home directory
    */
    String getHomePath();

   /**
    * Return the root directory to the merlin system repository
    * @return the merlin system repository directory
    */
    String getSystemPath();

   /**
    * Return the root directory to the merlin configurations
    * @return the merlin configuration directory
    */
    String getConfigPath();

   /**
    * Return the url to the kernel confiuration
    * @return the kernel configuration url
    */
    String getKernelPath();

   /**
    * Return the working client directory.
    * @return the working directory
    */
    String getWorkingPath();

   /**
    * Return the temporary directory.
    * @return the temp directory
    */
    String getTempPath();

   /**
    * Return the context directory from which relative 
    * runtime home directories will be established for 
    * components referencing urn:avalon:home
    *
    * @return the working directory
    */
    String getContextPath();

   /**
    * Return the anchor directory to be used when resolving 
    * library declarations in classload specifications.
    *
    * @return the anchor directory
    */
    String getAnchorPath();

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
}
