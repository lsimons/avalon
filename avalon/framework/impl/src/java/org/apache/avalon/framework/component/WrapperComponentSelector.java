/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.framework.component;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.ServiceException;

/**
 * This is a {@link ServiceSelector} implementation that can wrap around a legacy
 * {@link ComponentSelector} object effectively adapting a {@link ComponentSelector}
 * interface to a {@link ServiceSelector} interface.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.8 $ $Date: 2003/02/11 07:13:45 $
 */
public class WrapperComponentSelector
    implements ComponentSelector
{
    /**
     * The Selector we are wrapping.
     */
    private final ServiceSelector m_selector;

    /**
     * The role that this selector was aquired via.
     */
    private final String m_key;

    /**
     * This constructor is a constructor for a WrapperComponentSelector.
     *
     * @param key the key used to aquire this selector
     * @param selector the selector to wrap
     */
    public WrapperComponentSelector( final String key,
                                     final ServiceSelector selector )
    {
        if( null == key )
        {
            throw new NullPointerException( "key" );
        }
        if( null == selector )
        {
            throw new NullPointerException( "selector" );
        }

        m_key = key + "/";
        m_selector = selector;
    }

    /**
     * Select a Component based on a policy.
     *
     * @param policy the policy
     * @return the Component
     * @throws ComponentException if unable to select service
     */
    public Component select( final Object policy )
        throws ComponentException
    {
        try
        {
            final Object object = m_selector.select( policy );
            if( object instanceof Component )
            {
                return (Component)object;
            }
        }
        catch( final ServiceException se )
        {
            throw new ComponentException( m_key + policy, se.getMessage(), se );
        }

        final String message = "Role does not implement the Component " 
           + "interface and thus can not be accessed via ComponentSelector";
        throw new ComponentException( m_key + policy, message );
    }

    /**
     * Check to see if a {@link Component} exists relative to the supplied policy.
     *
     * @param policy a {@link Object} containing the selection criteria
     * @return True if the component is available, False if it not.
     */
    public boolean hasComponent( final Object policy )
    {
        return m_selector.isSelectable( policy );
    }

    /**
     * Return the {@link Object} when you are finished with it.  This
     * allows the {@link ServiceSelector} to handle the End-Of-Life Lifecycle
     * events associated with the {@link Object}.  Please note, that no
     * Exception should be thrown at this point.  This is to allow easy use of the
     * ServiceSelector system without having to trap Exceptions on a release.
     *
     * @param object The {@link Object} we are releasing.
     */
    public void release( final Component object )
    {
        m_selector.release( object );
    }

    /**
     * The {@link WrapperComponentManager} wraps ServiceSelectors in
     *  WrapperServiceSelectors when they are looked up.  This method
     *  makes it possible to release the original component selector.
     *
     * @return The {@link ServiceSelector} being wrapped.
     */
    ServiceSelector getWrappedSelector()
    {
        return m_selector;
    }
}
