/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.container.Container;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.phoenix.components.kapi.BlockListenerEntry;

/**
 * The Application is a self-contained component that performs a specific
 * function.
 *
 * Example ServerApplications may be a Mail Server, File Server, Directory Server etc.
 * Example JesktopApplications may be a Spreadsheet program, browser, mail client
 * Example WebApplications may be a particular website or application within a website
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Application
    extends Container, Initializable, Startable, Disposable
{
    void addBlockListenerEntrys( BlockListenerEntry[] listeners );
}
