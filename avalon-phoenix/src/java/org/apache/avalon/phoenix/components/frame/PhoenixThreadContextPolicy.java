/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.frame;

import org.apache.log.ContextMap;
import org.apache.avalon.excalibur.lang.DefaultThreadContextPolicy;
import org.apache.avalon.excalibur.lang.ThreadContextAccessor;
import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * This <code>ThreadContextPolicy</code> sets appropriate ContextClassLoader
 * and LogKit's ContextMap.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PhoenixThreadContextPolicy
    extends DefaultThreadContextPolicy
{
    /**
     * Key used to store LogKits <code>ContextMap</code> in ThreadContextAccessor.
     */
    public final static String  LOG_CONTEXT     = "LogContextMap";

    /**
     * The activate method is called when the ThreadContext 
     * is associated with a thread.
     *
     * @param accessor the accessor to retrieve values from ThreadContext
     */
    public void activate( final ThreadContextAccessor accessor )
    {
        super.activate( accessor );

        final ContextMap map = (ContextMap)get( accessor, LOG_CONTEXT, null, ContextMap.class );
        if( null != map ) ContextMap.bind( map );
    }

    /**
     * Verify that the key/value pair is valid.
     *
     * @param key The key
     * @param value the value
     * @exception IllegalArgumentException if pair is not valid
     */
    public void verifyKeyValue( final String key, final Object value )
        throws IllegalArgumentException
    {
        if( key.equals( LOG_CONTEXT ) && !(value instanceof ContextMap) )
        {
            throw new IllegalArgumentException( "Key " + key + " must be of type " + 
                                                ContextMap.class.getName() );
        }
        else
        {
            super.verifyKeyValue( key, value );
        }
    }
}
