/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.context;

/**
 * This inteface should be implemented by classes that need
 * a Context to work. Context contains runtime generated object
 * provided by the parent to this class.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:pier@apache.org">Pierpaolo Fumagalli</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 */
public interface Contextualizable
{
    /**
     * Pass the Context to the contextualizable class. This method
     * is always called after the constructor and, if present,
     * after configure but before any other method.
     *
     */
    void contextualize( Context context )
        throws ContextException;
}
