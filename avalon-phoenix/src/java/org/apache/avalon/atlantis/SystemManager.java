/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis;

import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Startable;
import org.apache.avalon.Stoppable;
import org.apache.avalon.component.Component;

/**
 * This component is responsible for managing the system.
 * This includes managing the embeddor, deployer and kernel.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface SystemManager
    extends Component, Initializable, Startable, Stoppable, Disposable
{
}
