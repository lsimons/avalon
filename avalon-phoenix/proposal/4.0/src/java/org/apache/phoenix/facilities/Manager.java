/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.facilities;

import javax.management.MBeanServer;

import org.apache.framework.lifecycle.Suspendable;
import org.apache.framework.lifecycle.Resumable;
import org.apache.framework.lifecycle.Stoppable;
import org.apache.framework.lifecycle.Disposable;


/**
 *
 *
 * <h3>Contract</h3>
 * <p>Besides the interface, the Manager implementation should provide at least
 * an empty public constructor and a public constructor that accepts a
 * <code>javax.management.MBeanServer</code> as its argument.</p>
 *
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 */
public interface Manager extends Facility, Runnable, Suspendable, Resumable, Stoppable, Disposable
    // and thus Component
{
}