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
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a static implementation of a <code>ServiceManager</code>. Allow ineritance
 * and extension so you can generate a tree of <code>ServiceManager</code> each defining
 * Object scope.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.18 $ $Date: 2003/02/11 15:58:42 $
 */
public class DefaultServiceManager
    implements ServiceManager
{
    private final HashMap m_objects = new HashMap();
    private final ServiceManager m_parent;
    private boolean m_readOnly;

    /**
     * Construct <code>ServiceManager</code> with no parent.
     *
     */
    public DefaultServiceManager()
    {
        this( null );
    }

    /**
     * Construct <code>ServiceManager</code> with specified parent.
     *
     * @param parent this <code>ServiceManager</code>'s parent
     */
    public DefaultServiceManager( final ServiceManager parent )
    {
        m_parent = parent;
    }

    /**
     * Retrieve <code>Object</code> by key from <code>ServiceManager</code>.
     *
     * @param key the key
     * @return the <code>Object</code>
     * @throws ServiceException if an error occurs
     */
    public Object lookup( final String key )
        throws ServiceException
    {
        final Object object = m_objects.get( key );
        if( null != object )
        {
            return object;
        }
        else if( null != m_parent )
        {
            return m_parent.lookup( key );
        }
        else
        {
            final String message = "Unable to provide implementation for " + key;
            throw new ServiceException( key, message, null );
        }
    }

    /**
     * Check to see if a <code>Object</code> exists for a key.
     *
     * @param key  a string identifying the key to check.
     * @return True if the object exists, False if it does not.
     */
    public boolean hasService( final String key )
    {
        try
        {
            lookup( key );
            return true;
        }
        catch( final Throwable t )
        {
            return false;
        }
    }

    /**
     * Place <code>Object</code> into <code>ServiceManager</code>.
     *
     * @param key the object's key
     * @param object an <code>Object</code> value
     */
    public void put( final String key, final Object object )
    {
        checkWriteable();
        m_objects.put( key, object );
    }

    /**
     * Build a human readable representation of this
     * <code>ServiceManager</code>.
     *
     * @return the description of this <code>ServiceManager</code>
     */
    public String toString()
    {
        final StringBuffer buffer = new StringBuffer();
        final Iterator objects = m_objects.keySet().iterator();
        buffer.append( "Services:" );

        while( objects.hasNext() )
        {
            buffer.append( "[" );
            buffer.append( objects.next() );
            buffer.append( "]" );
        }

        return buffer.toString();
    }

    /**
     * Helper method for subclasses to retrieve parent.
     *
     * @return the parent <code>ServiceManager</code>
     */
    protected final ServiceManager getParent()
    {
        return m_parent;
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
     * Makes this <code>ServiceManager</code> read-only.
     *
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Checks if this <code>ServiceManager</code> is writeable.
     *
     * @throws IllegalStateException if this <code>ServiceManager</code> is
     * read-only
     */
    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            final String message =
                "ServiceManager is read only and can not be modified";
            throw new IllegalStateException( message );
        }
    }

    /**
     * Release the <code>Object</code>.
     * @param object The <code>Object</code> to release.
     */
    public void release( Object object )
    {
    }
}
