/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Kernel
    extends Component, Container, Initializable, Startable, Disposable
{
}
