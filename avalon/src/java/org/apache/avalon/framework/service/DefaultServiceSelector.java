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
package org.apache.avalon.framework.service;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the default implementation of the ServiceSelector
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.14 $ $Date: 2003/02/25 15:35:43 $
 */
public class DefaultServiceSelector
    implements ServiceSelector
{
    private final HashMap m_objects = new HashMap();
    private boolean m_readOnly;
    private final String m_role;
    
    /**
     * Create a DefaultServiceSelector with a default empty role.
     */
    public DefaultServiceSelector()
    {
        this("");
    }
    
    /**
     * Create a DefaultServiceSelector with a role for debug purposes.
     * 
     * @param role  The role for this selector.
     * 
     * @throws NullPointerException if the role is null.
     */
    public DefaultServiceSelector(String role)
    {
        if ( null==role )
        {
            throw new NullPointerException(role);
        }
        
        m_role = role;
    }

    /**
     * Select the desired object.
     *
     * @param hint the hint to retrieve Object
     * @return the Object
     * @throws ServiceException if an error occurs
     */
    public Object select( Object hint )
        throws ServiceException
    {
        final Object object = m_objects.get( hint );

        if( null != object )
        {
            return object;
        }
        else
        {
            throw new ServiceException( m_role + "/" + hint.toString(), "Unable to provide implementation" );
        }
    }

    /**
     * Returns whether a Object exists or not
     * @param hint the hint to retrieve Object
     * @return <code>true</code> if the Object exists
     */
    public boolean isSelectable( final Object hint )
    {
        boolean objectExists = false;

        try
        {
            this.release( this.select( hint ) );
            objectExists = true;
        }
        catch( Throwable t )
        {
            // Ignore all throwables--we want a yes or no answer.
        }

        return objectExists;
    }

    /**
     * Release object.
     *
     * @param object the <code>Object</code> to release
     */
    public void release( final Object object )
    {
        // if the ServiceManager handled pooling, it would be
        // returned to the pool here.
    }

    /**
     * Populate the ServiceSelector.
     * @param hint the hint to be used to retrieve the Object later
     * @param object the Object to hold
     */
    public void put( final Object hint, final Object object )
    {
        checkWriteable();
        m_objects.put( hint, object );
    }

    /**
     * Helper method for subclasses to retrieve object map.
     *
     * @return the object map
     */
    protected final Map getObjectMap()
    {
        return m_objects;
    }

    /**
     * Makes this service selector read-only.
     *
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Checks if this service selector is writeable.
     *
     * @throws IllegalStateException if this service selector is read-only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException
                ( "ServiceSelector is read only and can not be modified" );
        }
    }
}
