/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.context;

/**
 * This inteface should be implemented by components that need
 * a Context to work. Context contains runtime generated object
 * provided by the Container to this Component.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 */
public interface Contextualizable
{
    /**
     * Pass the Context to the component. 
     * This method is called after the Loggable.setLogger() (if present) 
     * method and before any other method.
     *
     * @param context the context
     * @exception ContextException if context is invalid
     */
    void contextualize( Context context )
        throws ContextException;
}
