/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.servlet;

/**
 * Common Reference Proxy implementation.
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/21 06:03:16 $
 * @since 4.0
 */
abstract class AbstractReferenceProxy
    implements ReferenceProxy
{
    /** AbstractReferenceProxyLatch which owns the proxy. */
    private AbstractReferenceProxyLatch m_latch;
    
    /** Name of the proxy. */
    private String m_name;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a new AbstractReferenceProxy around a given object.
     *
     * @param object The object to protect with the proxy.
     * @param latch ReferenceProxyLatch which owns the proxy.
     */
    AbstractReferenceProxy( AbstractReferenceProxyLatch latch, String name )
    {
        m_latch = latch;
        m_name = name;
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name of the proxy.
     *
     * @return The name of the proxy.
     */
    String getName()
    {
        return m_name;
    }
    
    /**
     * Called when all references to the ReferenceProxy have been removed.
     */
    protected void finalize()
    {
        m_latch.notifyFinalized( this );
    }
}
