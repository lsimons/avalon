/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress;

import org.apache.avalon.framework.service.ServiceException;

/**
 * The Container is an interface used to mark the Containers in your system.
 * It is used to functionally identify Containers. It's primary use is to
 * assist Container developers to obtain the desired object from the Context.
 * All communication from the ContainerManager to the Container is through the
 * Context object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/03/22 12:46:32 $
 * @see ContainerConstants for the contract surrounding the Container context
 * @see <a href="http://jakarta.apache.org/avalon/framework/guide-cop-in-avalon.html">COP In Avalon</a>
 */
public interface Container
{
    /**
     * This is the method that the ContainerComponentManager and Selector use
     * to gain access to the ComponentHandlers and ComponentSelectors.  The
     * actual access of the ComponentHandler is delegated to the Container.
     *
     * @param  key  The role we intend to access a Component for.
     * @param  hint  The hint that we use as a qualifier
     *         (note: if null, the default implementation is returned).
     *
     * @return Object  a reference to the ComponentHandler or
     *                 ComponentSelector for the role/hint combo.
     */
    Object get( String key, Object hint ) throws ServiceException;

    /**
     * This is the method that the ContainerComponentManager and Selector use
     * to gain access to the ComponentHandlers and ComponentSelectors.  The
     * actual access of the ComponentHandler is delegated to the Container.
     *
     * @param  key  The role we intend to access a Component for.
     * @param  hint  The hint that we use as a qualifier
     *         (note: if null, the default implementation is returned).
     *
     * @return true  if a reference to the role exists.
     */
    boolean has( String key, Object hint );
}

