/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.core;

import java.lang.Runnable;
import java.lang.UnsupportedOperationException;

import org.apache.framework.context.Contextualizable;
import org.apache.framework.configuration.Configurable;
import org.apache.framework.lifecycle.Initializable;
import org.apache.framework.lifecycle.Disposable;


/**
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public interface Embeddor
    extends Contextualizable, Configurable, Initializable, Runnable, Disposable
{
    public void restart() throws UnsupportedOperationException;
}
