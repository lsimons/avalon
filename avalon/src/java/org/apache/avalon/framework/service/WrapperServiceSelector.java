/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights
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

package org.apache.avalon.framework.service;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;

/**
 * This is a {@link ServiceSelector} implementation that can wrap around a legacy
 * {@link ComponentSelector} object effectively adapting a {@link ComponentSelector}
 * interface to a {@link ServiceSelector} interface.
 * <p>
 * This class implements the {@link Component} interface because it is used in
 * environments which expect all components to implement Component.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/02/10 07:19:18 $
 */
public class WrapperServiceSelector
    implements ServiceSelector
{
    /**
     * The Selector we are wrapping.
     */
    private final ComponentSelector m_selector;

    /**
     * The role that this selector was aquired via.
     */
    private final String m_key;

    /**
     * This constructor is a constructor for a ComponentServiceManager
     *
     * @param key the key used to aquire this selector
     * @param selector the selector to wrap
     */
    public WrapperServiceSelector( final String key,
                                   final ComponentSelector selector )
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
     * Select a service based on a policy.
     *
     * @param policy the policy
     * @return the service
     * @throws ServiceException if unable to select service
     */
    public Object select( final Object policy )
        throws ServiceException
    {
        try
        {
            return m_selector.select( policy );
        }
        catch( final ComponentException ce )
        {
            throw new ServiceException( m_key + policy, ce.getMessage(), ce );
        }
    }

    /**
     * Check to see if a {@link Object} exists relative to the supplied policy.
     *
     * @param policy a {@link Object} containing the selection criteria
     * @return True if the component is available, False if it not.
     */
    public boolean isSelectable( final Object policy )
    {
        return m_selector.hasComponent( policy );
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
    public void release( Object object )
    {
        m_selector.release( (Component)object );
    }

    /**
     * The {@link WrapperServiceManager} wraps ComponentSelectors in
     *  WrapperServiceSelectors when they are looked up.  This method
     *  makes it possible to release the original component selector.
     *
     * @return The {@link ComponentSelector} being wrapped.
     */
    ComponentSelector getWrappedSelector()
    {
        return m_selector;
    }
}
