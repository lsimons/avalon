/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.atlantis.applications;

import org.apache.framework.context.Contextualizable;
import org.apache.framework.configuration.Configurable;

/**
 * The ServerApplication is a self-contained server component that performs a specific
 * user function.
 *
 * Example ServerApplications may be a Mail Server, File Server, Directory Server etc.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ServerApplication
    extends Application, Contextualizable, Configurable
    // and thus extends Initializable, Startable, Stoppable, Disposable, Container
{
}
