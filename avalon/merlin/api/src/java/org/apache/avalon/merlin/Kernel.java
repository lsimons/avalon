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

import org.apache.avalon.merlin.event.KernelEventListener;
import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;

/**
 * A Kernel is the root of a containment solution. This interfaces 
 * defines the contract for any kernel implementation covering 
 * management aspects and service resolution aspects.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/01/14 16:32:39 $
 */
public interface Kernel extends KernelController
{
    static final int INITIALIZING = 0;
    static final int INITIALIZED = 1;
    static final int STARTING = 2;
    static final int ASSEMBLY = 3;
    static final int DEPLOYMENT = 4;
    static final int STARTED = 5;
    static final int STOPPING = 6;
    static final int DECOMMISSIONING = 7;
    static final int DISSASSEMBLY = 8;
    static final int STOPPED = 9;

   /**
    * Add a kernel listener.
    * @param listener the kernel listener to be added
    */
    void addKernelEventListener( KernelEventListener listener );

   /**
    * Remove a kernel listener.
    * @param listener the kernel listener to be removed
    */
    void removeKernelEventListener( KernelEventListener listener );

   /**
    * Return the root block.
    * @return the root application containment block
    */
    Block getBlock();

   /**
    * Return the applicance matching the supplied path.
    * @return the appliance
    */
    Appliance locate( String path ) throws KernelException;

   /**
    * Return the current state of the kernel.
    * @return the kernel state
    */
    int getState();

}
