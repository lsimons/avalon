/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis.applications;

import org.apache.framework.lifecycle.Disposable;
import org.apache.framework.lifecycle.Initializable;
import org.apache.framework.lifecycle.Executable;

import org.apache.avalon.camelot.Container;

/**
 * An <code>Application</code> is used to represent a self-contained component.
 * These are usually not created directly, but rather are created by the
 * Avalon application server to wrap around an application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public interface Application
    extends Initializable, Executable, Disposable, Container
{
}
