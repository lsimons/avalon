/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis.core;

import java.lang.UnsupportedOperationException;

import org.apache.framework.parameters.Parametizable;
import org.apache.framework.lifecycle.Initializable;
import org.apache.framework.lifecycle.Executable;
import org.apache.framework.lifecycle.Disposable;


/**
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public interface Embeddor
    extends Parametizable, Initializable, Executable
{
    /**
     * Provide a reference to the class that loads the Embeddor.
     * Call immediately before init().
     *
     * The stop() method of Embeddor calls dispose() on this
     * object. You may pass a null value to avoid this.
     */
    public void setRunner( Disposable runner );
}
