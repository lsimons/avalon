/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis;

import org.apache.avalon.Startable;
import org.apache.avalon.Stoppable;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.camelot.Container;

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
    extends Container, Initializable, Startable, Stoppable, Disposable
{
}
