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
package org.apache.avalon.excalibur.component.servlet;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;

/**
 * Reference Proxy to a ComponentManager
 *
 * @deprecated The ComponentManager interface has been deprecated in favor
 *             of the ServiceManager.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1.1.1 $ $Date: 2003/11/09 12:44:17 $
 * @since 4.2
 */
final class ComponentManagerReferenceProxy
    extends AbstractReferenceProxy
    implements ComponentManager
{
    private ComponentManager m_componentManager;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a new proxy.
     *
     * @param componentManager ComponentManager being proxied.
     * @param latch Latch wich will be notified when this proxy is finalized.
     * @param name Name of the proxy.
     */
    ComponentManagerReferenceProxy( ComponentManager componentManager,
                                    AbstractReferenceProxyLatch latch,
                                    String name )
    {
        super( latch, name );
        m_componentManager = componentManager;
    }
    
    /*---------------------------------------------------------------
     * ComponentManager Methods
     *-------------------------------------------------------------*/
    /**
     * Get the <code>Component</code> associated with the given role.  For
     * instance, If the <code>ComponentManager</code> had a
     * <code>LoggerComponent</code> stored and referenced by role, I would use
     * the following call:
     * <pre>
     * try
     * {
     *     MyComponent log;
     *     myComponent = (MyComponent) manager.lookup(MyComponent.ROLE);
     * }
     * catch (...)
     * {
     *     ...
     * }
     * </pre>
     *
     * @param role The role name of the <code>Component</code> to retrieve.
     * @return the desired component
     * @throws ComponentException if an error occurs
     */
    public Component lookup( String role )
        throws ComponentException
    {
        return m_componentManager.lookup( role );
    }

    /**
     * Check to see if a <code>Component</code> exists for a role.
     *
     * @param role  a string identifying the role to check.
     * @return True if the component exists, False if it does not.
     */
    public boolean hasComponent( String role )
    {
        return m_componentManager.hasComponent( role );
    }

    /**
     * Return the <code>Component</code> when you are finished with it.  This
     * allows the <code>ComponentManager</code> to handle the End-Of-Life Lifecycle
     * events associated with the Component.  Please note, that no Exceptions
     * should be thrown at this point.  This is to allow easy use of the
     * ComponentManager system without having to trap Exceptions on a release.
     *
     * @param component The Component we are releasing.
     */
    public void release( Component component )
    {
        m_componentManager.release( component );
    }
}
