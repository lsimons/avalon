/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.atlantis;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;

/**
 * This is the object that is interacted with to create, manage and
 * dispose of the kernel and related resources.
 *
 * @author <a href="mail@leosimons.com">Leo Simons</a>
 * @author <a href="donaldp@apache.org">Peter Donald</a>
 */
public interface Embeddor
    extends Component, Initializable, Startable, Disposable
{
    String ROLE = "org.apache.avalon.framework.atlantis.Embeddor";

    /**
     * After the Embeddor is initialized, this method is called to actually
     * do the work. It will return when the embeddor is ready to be disposed.
     *
     * @exception Exception if an error occurs
     */
    void execute()
        throws Exception;
}
