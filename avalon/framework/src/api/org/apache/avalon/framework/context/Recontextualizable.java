/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.context;

/**
 * Extends Contextualizable to allow recontextualizing.
 * This allows a component to re-receive it's context if 
 * container environment has changed.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface Recontextualizable
    extends Contextualizable
{
    /**
     * Pass the new Context to the component. 
     * This method is usually called when component is suspended via use of
     * Suspendable.suspend() method.
     *
     * @param context the context
     * @exception ContextException if context is invalid
     */
    void recontextualize( Context context )
        throws ContextException;
}
